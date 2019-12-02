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
import org.greenrobot.greendao.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

@Entity(indexes = {
        @Index(value = "db_id DESC", unique = true)
})

public class Customer {
    @Id
    private Long id;

    @NotNull
    private String db_id;

    private String name;
    private String last_name;
    private String email;
    private String street;
    private String colony;
    private String phone;
    private double balance;
    private boolean status;
    private boolean sync;
@Generated(hash = 381162430)
public Customer(Long id, @NotNull String db_id, String name, String last_name,
        String email, String street, String colony, String phone,
        double balance, boolean status, boolean sync) {
    this.id = id;
    this.db_id = db_id;
    this.name = name;
    this.last_name = last_name;
    this.email = email;
    this.street = street;
    this.colony = colony;
    this.phone = phone;
    this.balance = balance;
    this.status = status;
    this.sync = sync;
}
@Generated(hash = 60841032)
public Customer() {
}
    public static Customer processFromServer(String db_id, Map<String, Object> result) {
        Customer currentCustomer = new Customer();

        currentCustomer.setDb_id(db_id);

        if (result.containsKey("name") && result.get("name") != null)
            currentCustomer.setName(result.get("name").toString());

        if (result.containsKey("last_name") && result.get("last_name") != null)
            currentCustomer.setLast_name(result.get("last_name").toString());

        if (result.containsKey("email") && result.get("email") != null)
            currentCustomer.setEmail(result.get("email").toString());

        if (result.containsKey("street") && result.get("street") != null)
            currentCustomer.setStreet(result.get("street").toString());

        if (result.containsKey("colony") && result.get("colony") != null)
            currentCustomer.setColony(result.get("colony").toString());

        if (result.containsKey("phone") && result.get("phone") != null)
            currentCustomer.setPhone(result.get("phone").toString());

        if (result.containsKey("balance") && result.get("balance") != null)
            currentCustomer.setBalance(Double.parseDouble(result.get("balance").toString()));

        if (result.containsKey("status") && result.get("status") !=  null)
            currentCustomer.setStatus((boolean)result.get("status"));

        return currentCustomer;
    }

    public static void uploadToServer(FirebaseFirestore db, final Customer currentCustomer, final Context context){
        Map<String, Object> customer = new HashMap<>();
        customer.put("name",currentCustomer.getName());
        customer.put("last_name",currentCustomer.getLast_name());
        customer.put("email",currentCustomer.getEmail());
        customer.put("street",currentCustomer.getStreet());
        customer.put("colony",currentCustomer.getColony());
        customer.put("phone",currentCustomer.getPhone());
        customer.put("balance",currentCustomer.getBalance());
        customer.put("status",currentCustomer.getStatus());

        if (!currentCustomer.getDb_id().isEmpty())
            db.collection("customer").document(currentCustomer.getDb_id())
                    .set(customer)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            currentCustomer.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentCustomer);

                            if (context != null)
                                SyncFirebase.downloadCustomers(context);

                            System.err.println("Customer yes");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Customer", "Error adding document", e);
                            System.err.println("Customer id_ "+e.getMessage());
                        }
                    });
        else
            db.collection("customer")
                    .add(customer)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            currentCustomer.setDb_id(documentReference.getId());
                            currentCustomer.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentCustomer);

                            if (context != null)
                                SyncFirebase.downloadCustomers(context);

                            System.err.println("Customer "+documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Customer", "Error adding document", e);
                            System.err.println("Customer "+e.getMessage());
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
public String getName() {
    return this.name;
}
public void setName(String name) {
    this.name = name;
}
public String getLast_name() {
    return this.last_name;
}
public void setLast_name(String last_name) {
    this.last_name = last_name;
}
public String getEmail() {
    return this.email;
}
public void setEmail(String email) {
    this.email = email;
}
public String getStreet() {
    return this.street;
}
public void setStreet(String street) {
    this.street = street;
}
public String getColony() {
    return this.colony;
}
public void setColony(String colony) {
    this.colony = colony;
}
public String getPhone() {
    return this.phone;
}
public void setPhone(String phone) {
    this.phone = phone;
}
public double getBalance() {
    return this.balance;
}
public void setBalance(double balance) {
    this.balance = balance;
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
