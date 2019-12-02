package com.tec.domingostore.Main.Customer.Detail;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerCreateActivity extends AppCompatActivity {

    TextInputEditText nameTextInputEditText, lastNameTextInputEditText, emailTextInputEditText,
            phoneTextInputEditText, streetTextInputEditText, colonyTextInputEditText, balanceTextInputEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_create);

        setTitle("Crear cliente");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    private void initComponents(){
        nameTextInputEditText       = findViewById(R.id.nameCustomerCreateTextInputEditText);
        lastNameTextInputEditText   = findViewById(R.id.lastNameCustomerCreateTextInputEditText);
        emailTextInputEditText      = findViewById(R.id.emailCustomerCreateTextInputEditText);
        phoneTextInputEditText      = findViewById(R.id.phoneCustomerCreateTextInputEditText);
        streetTextInputEditText     = findViewById(R.id.streetCustomerCreateTextInputEditText);
        colonyTextInputEditText     = findViewById(R.id.colonyCustomerCreateTextInputEditText);
        balanceTextInputEditText    = findViewById(R.id.balanceCustomerCreateTextInputEditText);
        final Button createButton         = findViewById(R.id.createCustomerCReateButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name, last_name, email, street, colony, phone;
                double  balance;

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

                if (!emailTextInputEditText.getText().toString().isEmpty()){
                    email   = emailTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"El email es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!streetTextInputEditText.getText().toString().isEmpty()){
                    street  = streetTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"La calle es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                if (!colonyTextInputEditText.getText().toString().isEmpty()){
                    colony  = colonyTextInputEditText.getText().toString();
                }else {
                    Snackbar.make(v,"La colonia es obligatorio",Snackbar.LENGTH_LONG).show();
                    return;
                }

                phone = "";
                if (!phoneTextInputEditText.getText().toString().isEmpty()){
                    phone   = phoneTextInputEditText.getText().toString();
                }

                balance = 0;
                if (!balanceTextInputEditText.getText().toString().isEmpty()){
                    balance = Double.parseDouble(balanceTextInputEditText.getText().toString());
                }

                createCustomer(name, last_name, email, street, colony, phone, balance);

            }
        });
    }

    private void createCustomer(String name,String last_name,String  email,String street,String colony,
            String phone,double balance){

        Customer newCustomer   = new Customer();

        newCustomer.setDb_id("");
        newCustomer.setName(name);
        newCustomer.setLast_name(last_name);
        newCustomer.setEmail(email);
        newCustomer.setStreet(street);
        newCustomer.setColony(colony);
        newCustomer.setPhone(phone);
        newCustomer.setBalance(balance);
        newCustomer.setStatus(true);
        newCustomer.setSync(false);

        DAOAccess.sharedInstance().getDaoSession().insert(newCustomer);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
