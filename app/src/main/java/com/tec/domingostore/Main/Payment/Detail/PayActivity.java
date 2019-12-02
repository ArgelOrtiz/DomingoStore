package com.tec.domingostore.Main.Payment.Detail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.PaymentDao;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.SaleDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.Main.Products.Detail.Buy.ProductBuyRecyclerViewAdapter;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.List;

public class PayActivity extends AppCompatActivity {

    TextView customerNameTextView, sellerNameTextView, productTitleTextView,
            paymentDateTextView, monthlyPaymentTextView, balancePaymentTextView;

    String sale_id;
    Payment currentPayment;
    Sale currentSale;
    Customer currentCustomer;
    Seller currentSeller;
    Product currentProduct;

    List<Payment> paymentList;
    RecyclerView paymentsRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        setTitle("Abonar");

        sale_id = getIntent().getExtras().getString("payment_id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }

    private void initComponents(){
        customerNameTextView        = findViewById(R.id.customerNamePayTextView);
        sellerNameTextView          = findViewById(R.id.sellerNamePayTextView);
        productTitleTextView        = findViewById(R.id.productTitlePayTextView);
        paymentDateTextView         = findViewById(R.id.paymentDatePayTextView);
        monthlyPaymentTextView      = findViewById(R.id.monthlyPaymentPayTextView);
        balancePaymentTextView      = findViewById(R.id.balancePaymentPayTextView);
        Button monthlyPaymentButton = findViewById(R.id.monthlyPaymentPayButton);
        Button balancePaymentButton = findViewById(R.id.balancePaymentPayButton);
        paymentsRecyclerView        = findViewById(R.id.paymentsPayRecyclerView);

        paymentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        monthlyPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payMonthlyPayment();
            }
        });

        balancePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payBalancePayment();
            }
        });

        getPayment();

        String customerNameLabel    = currentCustomer.getName()+" "+currentCustomer.getLast_name();
        customerNameTextView.setText(customerNameLabel);

        String sellerNameLabel      = currentSeller.getName()+" "+currentSeller.getLast_name();
        sellerNameTextView.setText(sellerNameLabel);

        productTitleTextView.setText(currentProduct.getTitle());
        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        paymentDateTextView.setText(simpleDateFormat.format(currentPayment.getPayment_date()));
        monthlyPaymentTextView.setText(String.valueOf(currentPayment.getPayment()));
        balancePaymentTextView.setText(String.valueOf(currentSale.getTotal()));
    }

    private void getPayment(){
        QueryBuilder<Payment> paymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getPaymentDao().queryBuilder();
        paymentQueryBuilder.where(PaymentDao.Properties.Db_id.eq(sale_id));
        currentPayment  = paymentQueryBuilder.unique();
        getSale();
    }

    private void getSale(){
        QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        saleQueryBuilder.where(SaleDao.Properties.Db_id.eq(currentPayment.getSale_id()));
        currentSale = saleQueryBuilder.unique();

        getCustomer();
    }

    private void getCustomer(){
        QueryBuilder<Customer> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.where(CustomerDao.Properties.Db_id.eq(currentSale.getCustomer_id()));
        currentCustomer = customerQueryBuilder.unique();

        getSeller();
    }

    private void getSeller(){
        QueryBuilder<Seller> sellerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        sellerQueryBuilder.where(SellerDao.Properties.Db_id.eq(currentSale.getSeller_id()));
        currentSeller   = sellerQueryBuilder.unique();

        getProduct();
    }

    private void getProduct(){
        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Db_id.eq(currentSale.getProduct_id()));
        currentProduct  = productQueryBuilder.unique();
        getPayments();
    }

    private void getPayments(){
        QueryBuilder<Payment> paymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getPaymentDao().queryBuilder();
        paymentQueryBuilder.where(PaymentDao.Properties.Sale_id.eq(currentSale.getDb_id()));
        paymentQueryBuilder.orderAsc(PaymentDao.Properties.Payment_number);
        paymentList = paymentQueryBuilder.list();

        if (paymentList.size() > 0 ){
            paymentsRecyclerView.setAdapter(new ProductBuyRecyclerViewAdapter(paymentList));
        }

    }

    private void payMonthlyPayment(){

        currentPayment.setPaid(true);
        double amount   = currentSale.getTotal() - currentPayment.getPayment();
        currentSale.setTotal(amount);

        currentPayment.setSync(false);
        currentSale.setSync(false);

        DAOAccess.sharedInstance().getDaoSession().update(currentPayment);
        DAOAccess.sharedInstance().getDaoSession().update(currentSale);
        SyncFirebase.syncColletions(getApplicationContext());

        finish();
    }

    private void payBalancePayment(){

        for (Payment currentPayment : paymentList) {
            currentPayment.setPaid(true);
            currentPayment.setSync(false);
            DAOAccess.sharedInstance().getDaoSession().update(currentPayment);
        }

        currentSale.setTotal(0);
        currentSale.setSync(false);
        DAOAccess.sharedInstance().getDaoSession().update(currentSale);

        SyncFirebase.syncColletions(getApplicationContext());

        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
