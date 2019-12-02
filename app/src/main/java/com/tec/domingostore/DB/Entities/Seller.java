package com.tec.domingostore.DB.Entities;

import android.content.Context;
import android.content.Intent;

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
public class Seller {
    @Id
    private Long id;

    @NotNull
    private String db_id;

    private String code;
    private String name;
    private String last_name;
    private String street;
    private String colony;
    private String phone;
    private double commission;
    private boolean status;
    private boolean sync;
@Generated(hash = 473887098)
public Seller(Long id, @NotNull String db_id, String code, String name,
        String last_name, String street, String colony, String phone,
        double commission, boolean status, boolean sync) {
    this.id = id;
    this.db_id = db_id;
    this.code = code;
    this.name = name;
    this.last_name = last_name;
    this.street = street;
    this.colony = colony;
    this.phone = phone;
    this.commission = commission;
    this.status = status;
    this.sync = sync;
}
@Generated(hash = 203966309)
public Seller() {
}
    public static Seller processFromServer(String db_id, Map<String, Object> result) {
        Seller currentSeller = new Seller();

        currentSeller.setDb_id(db_id);

        if (result.containsKey("code"))
            currentSeller.setCode(result.get("code").toString());

        if (result.containsKey("name"))
            currentSeller.setName(result.get("name").toString());

        if (result.containsKey("last_name"))
            currentSeller.setLast_name(result.get("last_name").toString());

        if (result.containsKey("street"))
            currentSeller.setStreet(result.get("street").toString());

        if (result.containsKey("colony"))
            currentSeller.setColony(result.get("colony").toString());

        if (result.containsKey("phone"))
            currentSeller.setPhone(result.get("phone").toString());

        if (result.containsKey("commission"))
            currentSeller.setCommission(Double.parseDouble(result.get("commission").toString()));

        if (result.containsKey("status"))
            currentSeller.setStatus((boolean)result.get("status"));


        return currentSeller;
    }

    public static void uploadToServer(FirebaseFirestore db, final Seller currentSeller,final Context context){
        Map<String, Object> seller = new HashMap<>();
        seller.put("code",currentSeller.getCode());
        seller.put("name",currentSeller.getName());
        seller.put("last_name",currentSeller.getLast_name());
        seller.put("street",currentSeller.getStreet());
        seller.put("colony",currentSeller.getColony());
        seller.put("phone",currentSeller.getPhone());
        seller.put("commission",currentSeller.getCommission());
        seller.put("status",currentSeller.getStatus());

        if (!currentSeller.getDb_id().isEmpty())
            db.collection("seller").document(currentSeller.getDb_id())
                    .set(seller)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            currentSeller.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentSeller);

                            if (context != null)
                                SyncFirebase.downloadSeller(context);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error writing document", e);
                        }
                    });
        else
            db.collection("seller")
                    .add(seller)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            currentSeller.setDb_id(documentReference.getId());
                            currentSeller.setSync(true);

                            DAOAccess.sharedInstance().getDaoSession().update(currentSeller);
                            if (context != null)
                                SyncFirebase.downloadSeller(context);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Log.w(TAG, "Error adding document", e);
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
public String getCode() {
    return this.code;
}
public void setCode(String code) {
    this.code = code;
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
public double getCommission() {
    return this.commission;
}
public void setCommission(double commission) {
    this.commission = commission;
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
