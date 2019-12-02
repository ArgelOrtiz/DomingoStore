package com.tec.domingostore.Main.Customer.Detail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class CustomerEditActivity extends AppCompatActivity {

    TextInputEditText nameTextInputEditText, lastNameTextInputEditText, emailTextInputEditText,
            phoneTextInputEditText, streetTextInputEditText, colonyTextInputEditText, balanceTextInputEditText;
    Customer currentCustomer;
    Long id;
    String customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_edit);

        setTitle("Modificar cliente");

        id  = getIntent().getExtras().getLong("id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();

        getCustomer();
    }

    private void initComponents(){
        nameTextInputEditText       = findViewById(R.id.nameCustomerEditTextInputEditText);
        lastNameTextInputEditText   = findViewById(R.id.lastNameCustomerEditTextInputEditText);
        emailTextInputEditText      = findViewById(R.id.emailCustomerEditTextInputEditText);
        phoneTextInputEditText      = findViewById(R.id.phoneCustomerEditTextInputEditText);
        streetTextInputEditText     = findViewById(R.id.streetCustomerEditTextInputEditText);
        colonyTextInputEditText     = findViewById(R.id.colonyCustomerEditTextInputEditText);
        balanceTextInputEditText    = findViewById(R.id.balanceCustomerEditTextInputEditText);
        Button editButton           = findViewById(R.id.editCustomerCeeateButton);
        Button deleteButton         = findViewById(R.id.deleteSellerEditButton);

        editButton.setOnClickListener(new View.OnClickListener() {
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

                editCustomer(name, last_name, email, street, colony, phone, balance);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isCustomerInSale()){
                    Toast.makeText(getApplicationContext(),"No se puede eliminar el Cliente , tiene ventas vigentes",Toast.LENGTH_LONG).show();
                    return;
                }

                currentCustomer.setStatus(false);
                currentCustomer.setSync(false);

                DAOAccess.sharedInstance().getDaoSession().update(currentCustomer);
                SyncFirebase.syncSeller(getApplicationContext());
                finish();
            }
        });

    }

    private void editCustomer(String name,String last_name,String  email,String street,String colony,
                                String phone,double balance){

        currentCustomer.setName(name);
        currentCustomer.setLast_name(last_name);
        currentCustomer.setEmail(email);
        currentCustomer.setStreet(street);
        currentCustomer.setColony(colony);
        currentCustomer.setPhone(phone);
        currentCustomer.setBalance(balance);
        currentCustomer.setSync(false);

        DAOAccess.sharedInstance().getDaoSession().update(currentCustomer);
        finish();

    }

    private void getCustomer(){
        QueryBuilder<Customer> customerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.where(CustomerDao.Properties.Id.eq(id));
        currentCustomer   = customerQueryBuilder.unique();


        nameTextInputEditText.setText(currentCustomer.getName());
        lastNameTextInputEditText.setText(currentCustomer.getLast_name());
        streetTextInputEditText.setText(currentCustomer.getStreet());
        colonyTextInputEditText.setText(currentCustomer.getColony());
        emailTextInputEditText.setText(currentCustomer.getEmail());
        phoneTextInputEditText.setText(String.valueOf(currentCustomer.getPhone()));
        balanceTextInputEditText.setText(String.valueOf(currentCustomer.getBalance()));

    }

    private boolean isCustomerInSale(){
        QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        List<Sale> saleList = saleQueryBuilder.list();

        for (int i=0 ; i<saleList.size();i++){
            Sale currentSale    = saleList.get(i);

            if (currentSale.getCustomer_id().equals(customer_id))
                return true;
        }

        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
