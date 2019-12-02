package com.tec.domingostore.Main.Seller.Detail;

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
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SellerEditActivity extends AppCompatActivity {

    TextInputEditText codeTextInputEditText, nameTextInputEditText, lastNameTextInputEditText, streetTextInputEditText,
            colonyTextInputEditText, phoneTextInputEditText, commissionTextInputEditText;
    Long id;
    Seller currentSeller;
    String seller_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_edit);

        setTitle("Modificar vendedor");

        id  = getIntent().getExtras().getLong("id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
        getSeller();
    }

    private void initComponents(){
        codeTextInputEditText       = findViewById(R.id.codeSellerEditTextInputEditText);
        nameTextInputEditText       = findViewById(R.id.nameSellerEditTextInputEditText);
        lastNameTextInputEditText   = findViewById(R.id.lastNameSellerEditTextInputEditText);
        streetTextInputEditText     = findViewById(R.id.streetSellerEditTextInputEditText);
        colonyTextInputEditText     = findViewById(R.id.colonySellerEditTextInputEditText);
        phoneTextInputEditText      = findViewById(R.id.phoneSellerEditTextInputEditText);
        commissionTextInputEditText = findViewById(R.id.commissionSellerEditTextInputEditText);
        Button editButton           = findViewById(R.id.editSellerEditButton);
        Button deleteButton         = findViewById(R.id.deleteSellerEditButton);

        editButton.setOnClickListener(new View.OnClickListener() {
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

                currentSeller.setCode(code);
                currentSeller.setName(name);
                currentSeller.setLast_name(last_name);
                currentSeller.setStreet(street);
                currentSeller.setColony(colony);
                currentSeller.setPhone(phone);
                currentSeller.setCommission(commission);
                currentSeller.setSync(false);

                DAOAccess.sharedInstance().getDaoSession().update(currentSeller);
                finish();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSellerInSale()){
                    Toast.makeText(getApplicationContext(),"No se puede eliminar el vendedor, tiene ventas vigentes",Toast.LENGTH_LONG).show();
                    return;
                }

                currentSeller.setStatus(false);
                currentSeller.setSync(false);

                DAOAccess.sharedInstance().getDaoSession().update(currentSeller);
                SyncFirebase.syncSeller(getApplicationContext());
                finish();
            }
        });

    }

    private void getSeller(){
        QueryBuilder<Seller> sellerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        sellerQueryBuilder.where(SellerDao.Properties.Id.eq(id));
        currentSeller   = sellerQueryBuilder.unique();

        seller_id   = currentSeller.getDb_id();
        codeTextInputEditText.setText(currentSeller.getCode());
        nameTextInputEditText.setText(currentSeller.getName());
        lastNameTextInputEditText.setText(currentSeller.getLast_name());
        streetTextInputEditText.setText(currentSeller.getStreet());
        colonyTextInputEditText.setText(currentSeller.getColony());
        phoneTextInputEditText.setText(String.valueOf(currentSeller.getPhone()));
        commissionTextInputEditText.setText(String.valueOf(currentSeller.getCommission()));
    }

    private boolean isSellerInSale(){
        QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        List<Sale> saleList = saleQueryBuilder.list();

        for (int i=0 ; i<saleList.size();i++){
            Sale currentSale    = saleList.get(i);

            if (currentSale.getSeller_id().equals(seller_id))
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
