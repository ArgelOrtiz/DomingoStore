package com.tec.domingostore.Main.Sale.Detail;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.MonthlyPayment;
import com.tec.domingostore.DB.Entities.MonthlyPaymentDao;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.PaymentDao;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.SaleDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.Main.Products.Detail.Buy.ProductBuyRecyclerViewAdapter;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SaleDetailActivity extends AppCompatActivity {

    TextView sellerTextView, customerTextView, quantityTextView, monthlyPaymentTextView,
            amountTextView, coverTextView, totalTextView;

    TextView titleProductTextView;
    ImageView productImageImageView;

    RecyclerView paymentsRecyclerView;

    String id;

    Sale currentSale;
    Customer currentCutomer;
    Seller currentSeller;
    MonthlyPayment currentMonthlyPayment;
    Product currentProduct;
    List<Payment> paymentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_detail);

        setTitle("Detalles de venta");

        id  = getIntent().getExtras().getString("sale_id");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
    }


    private void initComponents(){
        sellerTextView          = findViewById(R.id.sellerSaleDetailTextView);
        customerTextView        = findViewById(R.id.customerSaleDetailTextView);
        quantityTextView        = findViewById(R.id.quantitySaleDEtailTextView);
        monthlyPaymentTextView  = findViewById(R.id.monthlyPaymentSaleDetailTextView);
        amountTextView          = findViewById(R.id.amountSaleDetailTextView);
        coverTextView           = findViewById(R.id.coverSaleDetailTextView);
        totalTextView           = findViewById(R.id.totalSaleDetailTextView);
        titleProductTextView    = findViewById(R.id.titleProductSaleDetailTextView);
        productImageImageView   = findViewById(R.id.productImageSaleDetailImageView);
        paymentsRecyclerView    = findViewById(R.id.paymentsSaleDetailRecyclerView);

        paymentsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        getSale();
        getCustomer();
        getSeller();
        getMonthlyPayment();
        getProduct();
        getPayments();
    }

    private void getSale(){
        QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        saleQueryBuilder.where(SaleDao.Properties.Db_id.eq(id));
        currentSale = saleQueryBuilder.unique();

        quantityTextView.setText(String.valueOf(currentSale.getQuantity()));
        amountTextView.setText(String.valueOf(currentSale.getAmount()));
        coverTextView.setText(String.valueOf(currentSale.getCover()));
        totalTextView.setText(String.valueOf(currentSale.getTotal()));
    }

    private void getCustomer(){
        QueryBuilder<Customer> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.where(CustomerDao.Properties.Db_id.eq(currentSale.getCustomer_id()));
        currentCutomer  = customerQueryBuilder.unique();

        customerTextView.setText(currentCutomer.getName());
    }

    private void getSeller(){
        QueryBuilder<Seller> sellerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        sellerQueryBuilder.where(SellerDao.Properties.Db_id.eq(currentSale.getSeller_id()));
        currentSeller   = sellerQueryBuilder.unique();

        sellerTextView.setText(currentSeller.getName());
    }

    private void getMonthlyPayment(){
        QueryBuilder<MonthlyPayment> monthlyPaymentQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getMonthlyPaymentDao().queryBuilder();
        monthlyPaymentQueryBuilder.where(MonthlyPaymentDao.Properties.Db_id.eq(currentSale.getMonthly_payment_id()));
        currentMonthlyPayment   = monthlyPaymentQueryBuilder.unique();

        monthlyPaymentTextView.setText(currentMonthlyPayment.getName());

    }

    private void getProduct(){
        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Db_id.eq(currentSale.getProduct_id()));
        currentProduct = productQueryBuilder.unique();

        titleProductTextView.setText(currentProduct.getTitle());

        if (!currentProduct.getImage_url().isEmpty())
        Picasso.with(getApplicationContext()).
                load(currentProduct.getImage_url()).
                placeholder(R.drawable.ic_image_load).
                error(R.drawable.ic_image_error).
                into(productImageImageView);
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
