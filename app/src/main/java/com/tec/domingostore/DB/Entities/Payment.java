package com.tec.domingostore.DB.Entities;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.SyncFirebase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.greenrobot.greendao.annotation.Generated;

import androidx.annotation.NonNull;

@Entity(indexes = {
        @Index(value = "db_id DESC")
})
public class Payment {
    @Id
    private Long id;

    private String db_id;

    @NotNull
    private String sale_id;

    private int payment_number;
    private Date payment_date;
    private double payment;
    private double balance;
    private boolean paid;
    private boolean status;
    private boolean sync;

    @Generated(hash = 1935214006)
    public Payment(Long id, String db_id, @NotNull String sale_id,
                   int payment_number, Date payment_date, double payment, double balance,
                   boolean paid, boolean status, boolean sync) {
        this.id = id;
        this.db_id = db_id;
        this.sale_id = sale_id;
        this.payment_number = payment_number;
        this.payment_date = payment_date;
        this.payment = payment;
        this.balance = balance;
        this.paid = paid;
        this.status = status;
        this.sync = sync;
    }

    @Generated(hash = 1565471489)
    public Payment() {
    }

    public static Payment processFromServer(String db_id, Map<String, Object> result, Date payment_date) {
        Payment currentPayment = new Payment();

        currentPayment.setDb_id(db_id);

        if (result.containsKey("sale_id") && result.get("sale_id") != null)
            currentPayment.setSale_id(result.get("sale_id").toString());

        if (result.containsKey("payment_number") && result.get("payment_number") != null)
            currentPayment.setPayment_number(Integer.parseInt(result.get("payment_number").toString()));

        currentPayment.setPayment_date(payment_date);

        if (result.containsKey("payment") && result.get("payment") != null)
            currentPayment.setPayment(Double.parseDouble(result.get("payment").toString()));

        if (result.containsKey("balance") && result.get("balance") != null)
            currentPayment.setBalance(Double.parseDouble(result.get("balance").toString()));

        if (result.containsKey("paid") && result.get("paid") != null)
            currentPayment.setPaid((boolean) result.get("paid"));

        if (result.containsKey("status") && result.get("status") != null)
            currentPayment.setStatus((boolean) result.get("status"));


        return currentPayment;
    }

    public static void uploadToServer(FirebaseFirestore db, final Payment currentPayment, final Context context) {
        Map<String, Object> product = new HashMap<>();
        product.put("sale_id", currentPayment.getSale_id());
        product.put("payment_number", currentPayment.getPayment_number());
        product.put("payment_date", currentPayment.getPayment_date());
        product.put("payment", currentPayment.getPayment());
        product.put("balance", currentPayment.getBalance());
        product.put("paid", currentPayment.getPaid());
        product.put("status", currentPayment.getStatus());

        if (!currentPayment.getDb_id().isEmpty())
            db.collection("payment").document(currentPayment.getDb_id())
                    .set(product)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            currentPayment.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentPayment);


                            if (context != null)
                                SyncFirebase.downloadPayment(context);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("sale", "Error adding document", e);

                        }
                    });
        else
            db.collection("payment")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            currentPayment.setDb_id(documentReference.getId());
                            currentPayment.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentPayment);

                            if (context != null)
                                SyncFirebase.downloadPayment(context);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Sale", "Error adding document", e);
                        }
                    });

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDb_id() {
        return this.db_id;
    }

    public void setDb_id(String db_id) {
        this.db_id = db_id;
    }

    public String getSale_id() {
        return this.sale_id;
    }

    public void setSale_id(String sale_id) {
        this.sale_id = sale_id;
    }

    public int getPayment_number() {
        return this.payment_number;
    }

    public void setPayment_number(int payment_number) {
        this.payment_number = payment_number;
    }

    public Date getPayment_date() {
        return this.payment_date;
    }

    public void setPayment_date(Date payment_date) {
        this.payment_date = payment_date;
    }

    public double getPayment() {
        return this.payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean getPaid() {
        return this.paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean getStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean getSync() {
        return this.sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

}
