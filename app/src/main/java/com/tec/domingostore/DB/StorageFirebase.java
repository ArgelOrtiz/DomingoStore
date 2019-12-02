package com.tec.domingostore.DB;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StorageFirebase {
    public static final String UPLOAD_IMAGE = "UPLOAD_IMAGE";
    // Get a non-default Storage bucket
    private static FirebaseStorage storage;
    private static StorageReference storageRef;

    public static void setup(){
        storage = FirebaseStorage.getInstance("gs://domingostore-a.appspot.com");
        storageRef = storage.getReference();

    }

    public static void uploadImage(Uri file,final Context context){
        // Get the data from an ImageView as bytes
        final Intent intent = new Intent(UPLOAD_IMAGE);

        final StorageReference riversRef = storageRef.child("products/" + file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    intent.putExtra("url","");
                    intent.putExtra("message","Error al subir la imagen");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    intent.putExtra("url",downloadUri.toString());
                    intent.putExtra("message","Imagen subida correctamente");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });




        /*

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                intent.putExtra("url","");
                intent.putExtra("message","Error al subir la imagen");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                intent.putExtra("url",taskSnapshot.getUploadSessionUri());
                intent.putExtra("message","Imagen subida correctamente");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

         */
    }
}
