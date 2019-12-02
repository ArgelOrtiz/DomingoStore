package com.tec.domingostore.DB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class AuthFirebase {

    private static FirebaseAuth firebaseAuth;
    public static final String TAG   = "AuthFirebase";

    public static void setup(){
        firebaseAuth    = FirebaseAuth.getInstance();
    }


    public static FirebaseUser checkUser(){
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        return currentUser;
    }

    public static void createUser(String email, String password){

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        }else{
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        }
                    }
                });

    }

    public static void signIn(final Context context, String email, String password){

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Intent intent = new Intent(TAG);

                        if (task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            //editor.putString("name",user.getDisplayName());
                            editor.putString("email",user.getEmail());
                            //editor.putString("image_url",user.getPhoneNumber());
                            editor.putString("id",user.getUid());
                            editor.apply();

                            intent.putExtra("login",true);

                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            intent.putExtra("login",false);
                        }


                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    }
                });
    }

    public static boolean isLogged(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return !sharedPreferences.getString("id","").isEmpty();
    }


}
