package com.tec.domingostore.DB.Entities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
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

public class Product {

    @Id
    private Long id;

    @NotNull
    private String db_id;

    private String code;
    private String title;
    private double cost;
    private String description;
    private String image_url;
    private double existence;
    private double price;
    private boolean deleted;
    private boolean status;
    private boolean sync;
@Generated(hash = 617560118)
public Product(Long id, @NotNull String db_id, String code, String title,
        double cost, String description, String image_url, double existence,
        double price, boolean deleted, boolean status, boolean sync) {
    this.id = id;
    this.db_id = db_id;
    this.code = code;
    this.title = title;
    this.cost = cost;
    this.description = description;
    this.image_url = image_url;
    this.existence = existence;
    this.price = price;
    this.deleted = deleted;
    this.status = status;
    this.sync = sync;
}
@Generated(hash = 1890278724)
public Product() {
}


    public static Product processFromServer(String db_id, Map<String, Object> result) {
        Product currentProduct = new Product();

        currentProduct.setDb_id(db_id);

        if (result.containsKey("code") && result.get("code") != null)
            currentProduct.setCode(result.get("code").toString());

        if (result.containsKey("title") && result.get("title") != null)
            currentProduct.setTitle(result.get("title").toString());

        if (result.containsKey("cost") && result.get("cost") != null)
            currentProduct.setCost(Double.parseDouble(result.get("cost").toString()));

        if (result.containsKey("description") && result.get("description") != null)
            currentProduct.setDescription(result.get("description").toString());

        if (result.containsKey("image_url") && result.get("image_url") != null)
            currentProduct.setImage_url(result.get("image_url").toString());

        if (result.containsKey("existence") && result.get("existence") != null)
            currentProduct.setExistence(Double.parseDouble(result.get("existence").toString()));

        if (result.containsKey("price") && result.get("price") != null)
            currentProduct.setPrice(Double.parseDouble(result.get("price").toString()));

        if (result.containsKey("deleted"))
            currentProduct.setDeleted((boolean)result.get("deleted"));

        if (result.containsKey("status"))
            currentProduct.setStatus((boolean)result.get("status"));

        return currentProduct;
    }

    public static void uploadToServer(FirebaseFirestore db, final Product currentProduct,final Context context){
        Map<String, Object> product = new HashMap<>();
        product.put("code",currentProduct.getCode());
        product.put("title",currentProduct.getTitle());
        product.put("cost",currentProduct.getCost());
        product.put("description",currentProduct.getDescription());
        product.put("image_url",currentProduct.getImage_url());
        product.put("existence",currentProduct.getExistence());
        product.put("price",currentProduct.getPrice());
        product.put("status",currentProduct.getStatus());
        product.put("deleted",currentProduct.getDeleted());

        if (!currentProduct.getDb_id().isEmpty())
            db.collection("product").document(currentProduct.getDb_id())
                    .set(product)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Log.d(TAG, "DocumentSnapshot successfully written!");
                            currentProduct.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentProduct);

                            if (context != null)
                                SyncFirebase.downloadProducts(context);

                            System.err.println("Prodcut yes");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Product", "Error adding document", e);
                            System.err.println("Prodcut id_ "+e.getMessage());
                        }
                    });
        else
            db.collection("product")
                    .add(product)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            //Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            currentProduct.setDb_id(documentReference.getId());
                            currentProduct.setSync(true);
                            DAOAccess.sharedInstance().getDaoSession().update(currentProduct);

                            if (context != null)
                                SyncFirebase.downloadProducts(context);

                            System.err.println("Prodcut "+documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Product", "Error adding document", e);
                            System.err.println("Prodcut "+e.getMessage());
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
public String getTitle() {
    return this.title;
}
public void setTitle(String title) {
    this.title = title;
}
public double getCost() {
    return this.cost;
}
public void setCost(double cost) {
    this.cost = cost;
}
public String getDescription() {
    return this.description;
}
public void setDescription(String description) {
    this.description = description;
}
public String getImage_url() {
    return this.image_url;
}
public void setImage_url(String image_url) {
    this.image_url = image_url;
}
public double getExistence() {
    return this.existence;
}
public void setExistence(double existence) {
    this.existence = existence;
}
public double getPrice() {
    return this.price;
}
public void setPrice(double price) {
    this.price = price;
}
public boolean getDeleted() {
    return this.deleted;
}
public void setDeleted(boolean deleted) {
    this.deleted = deleted;
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
