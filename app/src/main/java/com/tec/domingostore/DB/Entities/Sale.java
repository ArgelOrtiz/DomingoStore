package com.tec.domingostore.DB.Entities;

import android.content.Context;
import android.content.Intent;
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
import org.greenrobot.greendao.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

@Entity(indexes = {
        @Index(value = "db_id DESC", unique = true)
})
public class Sale {
    @Id
    private Long id;

    @NotNull
    private String db_id;
    private String customer_id;
    private String monthly_payment_id;
    private String product_id;
    private String seller_id;

    private int quantity;
    private double amount;
    private double cover;
    private double total;

    private boolean status;
    private boolean sync;

    @Generated(hash = 1248922534)
    public Sale(Long id, @NotNull String db_id, String customer_id,
                String monthly_payment_id, String product_id, String seller_id,
                int quantity, double amount, double cover, double total, boolean status,
                boolean sync) {
        this.id = id;
        this.db_id = db_id;
        this.customer_id = customer_id;
        this.monthly_payment_id = monthly_payment_id;
        this.product_id = product_id;
        this.seller_id = seller_id;
        this.quantity = quantity;
        this.amount = amount;
        this.cover = cover;
        this.total = total;
        this.status = status;
        this.sync = sync;
    }

    @Generated(hash = 983461670)
    public Sale() {
    }

    public static Sale processFromServer(String db_id, Map<String, Object> result) {
        Sale currentSale = new Sale();

        currentSale.setDb_id(db_id);

        if (result.containsKey("customer_id") && result.get("customer_id") != null)
            currentSale.setCustomer_id(result.get("customer_id").toString());

        if (result.containsKey("monthly_payment_id") && result.get("monthly_payment_id") != null)
            currentSale.setMonthly_payment_id(result.get("monthly_payment_id").toString());

        if (result.containsKey("product_id") && result.get("product_id") != null)
            currentSale.setProduct_id(result.get("product_id").toString());

        if (result.containsKey("seller_id") && result.get("seller_id") != null)
            currentSale.setSeller_id(result.get("seller_id").toString());

        if (result.containsKey("quantity") && result.get("quantity") != null)
            currentSale.setQuantity(Integer.parseInt(result.get("quantity").toString()));

        if (result.containsKey("amount") && result.get("amount") != null)
            currentSale.setAmount(Double.parseDouble(result.get("amount").toString()));

        if (result.containsKey("cover") && result.get("cover") != null)
            currentSale.setCover(Double.parseDouble(result.get("cover").toString()));

        if (result.containsKey("total") && result.get("total") != null)
            currentSale.setTotal(Double.parseDouble(result.get("total").toString()));

        if (result.containsKey("status") && result.get("status") != null)
            currentSale.setStatus((boolean)result.get("status"));


        return currentSale;
    }

    public static void uploadToServer(FirebaseFirestore db, final Sale currentSale, final Context context, final Intent intent){
        Map<String, Object> product = new HashMap<>();
        product.put("customer_id",currentSale.getCustomer_id());
        product.put("monthly_payment_id",currentSale.getMonthly_payment_id());
        product.put("product_id",currentSale.getProduct_id());
        product.put("seller_id",currentSale.getSeller_id());
        product.put("quantity",currentSale.getQuantity());
        product.put("amount",currentSale.getAmount());
        product.put("cover",currentSale.getCover());
        product.put("total",currentSale.getTotal());
        product.put("status",currentSale.getStatus());

        if (!currentSale.getDb_id().isEmpty())
            db.collection("sale").document(currentSale.getDb_id())
                    .set(product)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            currentSale.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentSale);

                            if (context != null)
                                SyncFirebase.downloadSales(context);


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("sale", "Error adding document", e);

                        }
                    });
        else
            db.collection("sale")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            currentSale.setDb_id(documentReference.getId());
                            currentSale.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentSale);


                            if (intent != null && context != null) {
                                intent.putExtra("db_id",documentReference.getId());
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }else if (context != null) {
                                SyncFirebase.downloadSales(context);
                            }

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

    public String getCustomer_id() {
        return this.customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getMonthly_payment_id() {
        return this.monthly_payment_id;
    }

    public void setMonthly_payment_id(String monthly_payment_id) {
        this.monthly_payment_id = monthly_payment_id;
    }

    public String getProduct_id() {
        return this.product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getSeller_id() {
        return this.seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getAmount() {
        return this.amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCover() {
        return this.cover;
    }

    public void setCover(double cover) {
        this.cover = cover;
    }

    public double getTotal() {
        return this.total;
    }

    public void setTotal(double total) {
        this.total = total;
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
