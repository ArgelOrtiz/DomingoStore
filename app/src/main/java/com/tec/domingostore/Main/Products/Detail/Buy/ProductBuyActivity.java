package com.tec.domingostore.Main.Products.Detail.Buy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.MonthlyPayment;
import com.tec.domingostore.DB.Entities.MonthlyPaymentDao;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProductBuyActivity extends AppCompatActivity {

    ImageView productImageView;
    TextView titleTextView, descriptionTextView, priceTextView,amountTextView, totalTextView;
    Spinner quantitySpinner,sellersSpinner, customerSpinner, monthlyPaymentSpinner;
    EditText coverEditText;
    Long id;
    Product currentProduct;
    MonthlyPayment currentMonnthlyPayment ;
    Seller currentSeller;
    Customer currentCustomer;
    RecyclerView paymentsRecyclerView;
    ConstraintLayout paymentsConstraintLayout, chargeConstraintLayout;
    List<MonthlyPayment> monthlyPaymentList;
    List<Seller> sellerList;
    List<Customer> customerList;
    List<Payment> paymentList;

    double price, amount, cover, total, commission, months;
    int quantity;

    public String UPLOAD_SALE   = "UPLOAD_SALE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_buy);

        setTitle("Procesar venta");

        id  = getIntent().getExtras().getLong("id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    private void initComponents(){
        productImageView                = findViewById(R.id.productProductBuyImageView);
        titleTextView                   = findViewById(R.id.titleProductBuyTextView);
        descriptionTextView             = findViewById(R.id.descriptionProductBuyTextView);
        quantitySpinner                 = findViewById(R.id.queantityProductBuySpinner);
        sellersSpinner                  = findViewById(R.id.sellersProductBuySpinner);
        customerSpinner                 = findViewById(R.id.customerProductBuySpinner);
        priceTextView                   = findViewById(R.id.priceProductBuyTextView);
        monthlyPaymentSpinner           = findViewById(R.id.monthlyPaymentProductBuySpinner);
        amountTextView                  = findViewById(R.id.amountProductBuyTextView);
        coverEditText                   = findViewById(R.id.coverProductBuyEditText);
        totalTextView                   = findViewById(R.id.totalProductBuyTextView);
        paymentsRecyclerView            = findViewById(R.id.paymentsProductBuyRecyclerView);
        paymentsConstraintLayout        = findViewById(R.id.paymentsProductBuyConstraintLayout);
        chargeConstraintLayout          = findViewById(R.id.chargeConstraintLayout);
        Button generatePaymentsButton   = findViewById(R.id.generatePaymentsProductBuyButton);
        Button saleButton               = findViewById(R.id.saleProductBuyButton);

        paymentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false));

        generatePaymentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentsConstraintLayout.setVisibility(View.VISIBLE);
                getPayments();
            }
        });

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentsConstraintLayout.setVisibility(View.VISIBLE);
                getPayments();
                saveSale();

            }
        });

        quantitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                quantity    = position + 1;

                calculate();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthlyPaymentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentMonnthlyPayment  = monthlyPaymentList.get(position);

                commission  = ((price * currentMonnthlyPayment.getCommission())/100);
                months      = currentMonnthlyPayment.getNumber_of_months();
                calculate();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        customerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCustomer = customerList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sellersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentSeller   = sellerList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        coverEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                try{
                    if (s.toString().isEmpty()){
                        coverEditText.setText("0");
                        cover = 0;
                        calculate();
                        return;
                    }

                    cover    = Double.parseDouble(s.toString());

                    if (cover <= total) {
                        calculate();
                    }else{
                        cover = total;
                        coverEditText.setText(String.valueOf(cover));
                        calculate();
                    }


                    String totalLabel   = "$" +total;
                    totalTextView.setText(totalLabel);

                }catch (NumberFormatException e){
                    Log.e("ProductBuyActivity",e.getMessage());
                    coverEditText.setText("0");
                    cover = 0;
                    calculate();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getProdcut();


    }

    private void getProdcut(){

        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Id.eq(id));
        currentProduct   = productQueryBuilder.unique();

        titleTextView.setText(currentProduct.getTitle());
        descriptionTextView.setText(currentProduct.getDescription());

        price       = currentProduct.getPrice();
        quantity    = 1;
        commission  = 0;
        cover       = 0;
        months      = 1;

        calculate();

        String priceLabel = "$"+price;
        priceTextView.setText(priceLabel);



        if (!currentProduct.getImage_url().isEmpty())
            Picasso.with(getApplicationContext()).
                    load(currentProduct.getImage_url()).
                    placeholder(R.drawable.ic_image_load).
                    error(R.drawable.ic_image_error).
                    into(productImageView);

        getQuantity();
        getSellers();
        getCustomers();
        getMonthlyPayment();


    }

    private void calculate(){

        amount  = quantity * (price + commission);
        total   =  amount - cover;

        String amountLabel  = "$"+amount;
        amountTextView.setText(amountLabel);

        String totalLabel   = "$"+total;
        totalTextView.setText(totalLabel);

    }

    private void getPayments(){

        Calendar calendar = Calendar.getInstance();
        paymentList   = new ArrayList<>();

        if (months <= 1){
            paymentsConstraintLayout.setVisibility(View.GONE);
            return;
        }

        double payment  = total / months;

        for (int i=0; i<months; i++){
            Payment currentPayment = new Payment();

            calendar.add(Calendar.MONTH, 1);

            currentPayment.setPayment_number(i+1);
            currentPayment.setPayment(payment);
            currentPayment.setPayment_date(calendar.getTime());
            currentPayment.setBalance(total-(payment*(i+1)));
            currentPayment.setSync(false);
            currentPayment.setStatus(true);

            paymentList.add(currentPayment);
        }

        paymentsRecyclerView.setAdapter(new ProductBuyRecyclerViewAdapter(paymentList));



    }

    private void getQuantity(){
        double existence = currentProduct.getExistence();
        ArrayList<String> existenceList = new ArrayList<>();

        for (int i=1; i<= existence; i++){
           existenceList.add(String.valueOf(i));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_adapter_item, R.id.textLabelEditText, existenceList);
        quantitySpinner.setAdapter(dataAdapter);

    }

    private void getSellers(){
        ArrayList<String> sellersNameList = new ArrayList<>();

        QueryBuilder<Seller> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        customerQueryBuilder.orderAsc(SellerDao.Properties.Name);
        sellerList = customerQueryBuilder.list();

        if (sellerList.size() > 0)
            currentSeller   = sellerList.get(0);

        for (Seller currentSeller : sellerList) {
            sellersNameList.add(currentSeller.getName()+" "+currentSeller.getLast_name());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_adapter_item, R.id.textLabelEditText, sellersNameList);
        sellersSpinner.setAdapter(dataAdapter);

    }

    private void getCustomers(){
        ArrayList<String> customersNameList = new ArrayList<>();

        QueryBuilder<Customer> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.orderAsc(CustomerDao.Properties.Name);
        customerList = customerQueryBuilder.list();

        if (customerList.size() > 0)
            currentCustomer = customerList.get(0);

        for (Customer currentCustomer : customerList) {
            customersNameList.add(currentCustomer.getName()+" "+currentCustomer.getLast_name());
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_adapter_item, R.id.textLabelEditText, customersNameList);
        customerSpinner.setAdapter(dataAdapter);
    }

    private void getMonthlyPayment(){
        ArrayList<String> monthlyPaymentNameList    = new ArrayList<>();

        QueryBuilder<MonthlyPayment> monthlyPaymentQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getMonthlyPaymentDao().queryBuilder();
        monthlyPaymentQueryBuilder.orderAsc(MonthlyPaymentDao.Properties.Number_of_months);
        monthlyPaymentList = monthlyPaymentQueryBuilder.list();

        if (monthlyPaymentList.size() > 0)
            currentMonnthlyPayment  = monthlyPaymentList.get(0);

        for (MonthlyPayment currentMonthlyPayment : monthlyPaymentList) {
            monthlyPaymentNameList.add(currentMonthlyPayment.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_adapter_item, R.id.textLabelEditText, monthlyPaymentNameList);
        monthlyPaymentSpinner.setAdapter(dataAdapter);
    }

    private void saveSale(){
        chargeConstraintLayout.setVisibility(View.VISIBLE);

        Sale newSale    = new Sale();

        newSale.setDb_id("");
        newSale.setCustomer_id(currentCustomer.getDb_id());
        newSale.setSeller_id(currentSeller.getDb_id());
        newSale.setMonthly_payment_id(currentMonnthlyPayment.getDb_id());
        newSale.setProduct_id(currentProduct.getDb_id());

        newSale.setQuantity(quantity);
        newSale.setAmount(amount);
        newSale.setCover(cover);
        newSale.setTotal(total);

        newSale.setSync(false);
        newSale.setStatus(true);


        DAOAccess.sharedInstance().getDaoSession().insert(newSale);

        Sale.uploadToServer(SyncFirebase.db,newSale,getApplicationContext(),new Intent(UPLOAD_SALE));


        if (getApplicationContext() != null)
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(UPLOAD_SALE));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String db_id    = intent.getExtras().getString("db_id");

            for (Payment payment : paymentList) {
                payment.setDb_id("");
                payment.setSale_id(db_id);
                DAOAccess.sharedInstance().getDaoSession().insert(payment);
            }

            SyncFirebase.syncPayment(getApplicationContext());

            chargeConstraintLayout.setVisibility(View.GONE);

            finish();

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
