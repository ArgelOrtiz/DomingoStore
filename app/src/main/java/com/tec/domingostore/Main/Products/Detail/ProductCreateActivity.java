package com.tec.domingostore.Main.Products.Detail;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.StorageFirebase;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ProductCreateActivity extends AppCompatActivity {
    public static final int PICK_IMAGE = 1001;
    ImageButton imageImageButton;
    Uri selectedImage;
    ConstraintLayout chargeConstraintLayout;
    TextInputEditText titleTextInputEditText, costTextInputEditText, priceTextInputEditText, existenceTextInputEditText,
            codeTextInputEditText;
    EditText descriptionEditText;

    String title;
    String code;
    String description;
    double cost;
    double price,existence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle("Agregar nuevo producto");

        initComponent();
    }

    private void initComponent(){
        titleTextInputEditText      = findViewById(R.id.titleProductCreateTextInputEditText);
        costTextInputEditText       = findViewById(R.id.costProductCreateTextInputEditText);
        priceTextInputEditText      = findViewById(R.id.priceProductCreateTextInputEditText);
        existenceTextInputEditText  = findViewById(R.id.existenceProductCreateTextInputEditText);
        codeTextInputEditText       = findViewById(R.id.codeProductCreateTextInputEditText);
        imageImageButton            = findViewById(R.id.imageProductCreateImageButton);
        descriptionEditText         = findViewById(R.id.descriptionProductCreateEditText);
        chargeConstraintLayout      = findViewById(R.id.chargeConstraintLayout);
        Button createButton         = findViewById(R.id.createProductCreateButton);

        selectedImage   = null;

        imageImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!titleTextInputEditText.getText().toString().isEmpty()){
                    title   = titleTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El titulo es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!codeTextInputEditText.getText().toString().isEmpty()){
                    code    = codeTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El codigo es obligatoria",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!descriptionEditText.getText().toString().isEmpty()) {
                    description = descriptionEditText.getText().toString();
                }else {
                    Snackbar.make(v,"La descripcion es obligatoria",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!costTextInputEditText.getText().toString().isEmpty()){
                    cost    = Double.parseDouble(costTextInputEditText.getText().toString());
                }else {
                    Snackbar.make(v,"El costo es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!priceTextInputEditText.getText().toString().isEmpty()){
                    price   = Double.parseDouble(priceTextInputEditText.getText().toString());
                }else {
                    Snackbar.make(v,"El costo es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!existenceTextInputEditText.getText().toString().isEmpty()){
                    existence   = Double.parseDouble(existenceTextInputEditText.getText().toString());
                }


                chargeConstraintLayout.setVisibility(View.VISIBLE);

                if (selectedImage != null){
                    StorageFirebase.uploadImage(selectedImage,getApplicationContext());

                }else{
                    createProduct("");
                }


            }
        });

        if (getApplicationContext() != null)
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(StorageFirebase.UPLOAD_IMAGE));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
            createProduct(intent.getStringExtra("url"));
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            //TODO: action
            selectedImage = data.getData();
            imageImageButton.setImageURI(selectedImage);

        }
    }

    private void createProduct(String url){
        Product newProduct  = new Product();

        newProduct.setDb_id("");
        newProduct.setTitle(title);
        newProduct.setCode(code);
        newProduct.setDescription(description);
        newProduct.setExistence(existence);
        newProduct.setPrice(price);
        newProduct.setCost(cost);
        newProduct.setStatus(true);

        if (!url.isEmpty())
            newProduct.setImage_url(url);
        else
            newProduct.setImage_url("");

        DAOAccess.sharedInstance().getDaoSession().insert(newProduct);
        finish();

        chargeConstraintLayout.setVisibility(View.GONE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
