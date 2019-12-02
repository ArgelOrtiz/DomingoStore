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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.StorageFirebase;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ProductEditActivity extends AppCompatActivity {

    public static final int PICK_IMAGE = 1001;
    TextInputEditText titleTextInputEditText, codeTextInputEditText, costTextInputEditText, priceTextInputEditText,
            existenceTextInputEditText;
    ConstraintLayout chargeConstraintLayout;
    EditText descriptionEditText;
    ImageView imageImageView;
    ImageButton editImageButton;
    Button saveButton;
    Product currentProduct;
    Long id;

    Uri selectedImage;
    String title;
    String code;
    String description;
    double cost;
    double price,existence;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);

        setTitle("Modificar producto");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        id  = getIntent().getExtras().getLong("id");
        initComponents();
        getProdcut();
    }

    private void initComponents(){
        titleTextInputEditText     = findViewById(R.id.titleProductEditTextInputEditText);
        codeTextInputEditText       = findViewById(R.id.codeProductEditTextInputEditText);
        descriptionEditText         = findViewById(R.id.descriptionProductEditEditText);
        costTextInputEditText       = findViewById(R.id.costProductEditTextInputEditText);
        priceTextInputEditText      = findViewById(R.id.priceProductEditTextInputEditText);
        existenceTextInputEditText  = findViewById(R.id.existenceProductEditTextInputEditText);
        imageImageView              = findViewById(R.id.imageProductEditImageView);
        editImageButton             = findViewById(R.id.editProductEditImageButton);
        saveButton                  = findViewById(R.id.saveProductCreateButton);
        Button deleteButton         = findViewById(R.id.deleteProductEditButton);
        chargeConstraintLayout      = findViewById(R.id.chargeConstraintLayout);

        selectedImage   = null;

        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
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
                    updateProduct("");
                }

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentProduct.setDeleted(true);
                currentProduct.setSync(false);

                DAOAccess.sharedInstance().getDaoSession().update(currentProduct);
                SyncFirebase.syncProducts(getApplicationContext());

                setResult(21);
                finish();
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
            updateProduct(intent.getStringExtra("url"));
        }
    };

    private void getProdcut(){

        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Id.eq(id));
        currentProduct   = productQueryBuilder.unique();

        titleTextInputEditText.setText(currentProduct.getTitle());
        codeTextInputEditText.setText(currentProduct.getCode());
        descriptionEditText.setText(currentProduct.getDescription());
        costTextInputEditText.setText(String.valueOf(currentProduct.getCost()));
        priceTextInputEditText.setText(String.valueOf(currentProduct.getPrice()));
        existenceTextInputEditText.setText(String.valueOf(currentProduct.getExistence()));

        if (!currentProduct.getImage_url().isEmpty())
            Picasso.with(getApplicationContext()).
                    load(currentProduct.getImage_url()).
                    placeholder(R.drawable.ic_image_load).
                    error(R.drawable.ic_image_error).
                    into(imageImageView);

    }

    private void updateProduct(String url){

        currentProduct.setTitle(title);
        currentProduct.setCode(code);
        currentProduct.setDescription(description);
        currentProduct.setExistence(existence);
        currentProduct.setPrice(price);
        currentProduct.setCost(cost);
        currentProduct.setSync(false);

        if (!url.isEmpty())
            currentProduct.setImage_url(url);

        DAOAccess.sharedInstance().getDaoSession().update(currentProduct);
        SyncFirebase.syncProducts(getApplicationContext());
        finish();

        chargeConstraintLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            //TODO: action
            selectedImage = data.getData();
            imageImageView.setImageURI(selectedImage);

        }
    }
}
