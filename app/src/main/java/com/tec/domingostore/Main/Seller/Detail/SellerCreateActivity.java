package com.tec.domingostore.Main.Seller.Detail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.R;

public class SellerCreateActivity extends AppCompatActivity {

    TextInputEditText codeTextInputEditText, nameTextInputEditText, lastNameTextInputEditText,
        streetTextInputEditText, colonyTextInputEditText, phoneTextInputEditText, commissionTextInputEditText;
    ConstraintLayout chargeConstraintLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_create);

        setTitle("Nuevo vendedor");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    private void initComponents(){
        codeTextInputEditText       = findViewById(R.id.codeSellerCreateTextInputEditText);
        nameTextInputEditText       = findViewById(R.id.nameSellerCreateTextInputEditText);
        lastNameTextInputEditText   = findViewById(R.id.lastNameSellerCreateTextInputEditText);
        streetTextInputEditText     = findViewById(R.id.streetSellerCreateTextInputEditText);
        colonyTextInputEditText     = findViewById(R.id.colonySellerCreateTextInputEditText);
        phoneTextInputEditText      = findViewById(R.id.phoneSellerCreateTextInputEditText);
        commissionTextInputEditText = findViewById(R.id.commissionSellerCreateTextInputEditText);
        chargeConstraintLayout      = findViewById(R.id.chargeConstraintLayout);
        Button createButton         = findViewById(R.id.createSellerCreateButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code, name, last_name, street, colony, phone;
                double  commission;

                if (!codeTextInputEditText.getText().toString().isEmpty()){
                    code    = codeTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El codigo es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!nameTextInputEditText.getText().toString().isEmpty()){
                    name    = nameTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El nombre es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!lastNameTextInputEditText.getText().toString().isEmpty()){
                    last_name   = lastNameTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El apellido es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!streetTextInputEditText.getText().toString().isEmpty()){
                    street  = streetTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v," es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                colony  = "";
                if (!colonyTextInputEditText.getText().toString().isEmpty()){
                    colony  = colonyTextInputEditText.getText().toString();
                }

                phone   = "";
                if (!phoneTextInputEditText.getText().toString().isEmpty()){
                    phone   = phoneTextInputEditText.getText().toString();
                }

                commission  = 0;
                if (!commissionTextInputEditText.getText().toString().isEmpty()){
                    commission  = Double.parseDouble(commissionTextInputEditText.getText().toString());
                }

                chargeConstraintLayout.setVisibility(View.VISIBLE);

                createSeller(code,name,last_name,street,colony,phone,commission);

            }
        });

    }

    private void createSeller(String code,String name,String last_name,String street,String colony,
            String phone,double commission){

        Seller newSeller    = new Seller();

        newSeller.setDb_id("");
        newSeller.setCode(code);
        newSeller.setName(name);
        newSeller.setLast_name(last_name);
        newSeller.setStreet(street);
        newSeller.setColony(colony);
        newSeller.setPhone(phone);
        newSeller.setCommission(commission);
        newSeller.setSync(false);
        newSeller.setStatus(true);

        DAOAccess.sharedInstance().getDaoSession().insert(newSeller);
        //Seller.uploadToServer(SyncFirebase.db,newSeller);
        finish();
        chargeConstraintLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
