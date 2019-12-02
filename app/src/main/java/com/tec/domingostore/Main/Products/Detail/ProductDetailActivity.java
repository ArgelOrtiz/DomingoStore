package com.tec.domingostore.Main.Products.Detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.Main.Products.Detail.Buy.ProductBuyActivity;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProductDetailActivity extends AppCompatActivity {

    private final String TAG = "ProductDetailActivity";
    Product currentProduct;
    ImageView productImageView;
    TextView descriptionTextView, titleTextView, costTextView,existenceTextView;
    Long id;
    ConstraintLayout chargeConstraintLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        setTitle("Prodcucto seleccionado");

        Bundle bundle = getIntent().getExtras();

        id  = bundle.getLong("id");
        initComponent();
        getProdcut();
    }

    private void initComponent(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        productImageView        = findViewById(R.id.productDetailImageView);
        descriptionTextView     = findViewById(R.id.descriptionProductDetailTextView);
        Button buyButton        = findViewById(R.id.buyProdcutDetailButton);
        Button editButton       = findViewById(R.id.editProductDetailButton);
        chargeConstraintLayout  = findViewById(R.id.chargeConstraintLayout);
        titleTextView           = findViewById(R.id.titleProductDetailTextView);
        costTextView            = findViewById(R.id.costProductDetailTextView);
        existenceTextView       = findViewById(R.id.existenceProductDetailTextView);

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProductBuyActivity.class);
                intent.putExtra("id",currentProduct.getId());
                startActivity(intent);
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ProductEditActivity.class);
                intent.putExtra("id",currentProduct.getId());
                startActivityForResult(intent,2001);
            }
        });
    }

    private void getProdcut(){

        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Id.eq(id));
        currentProduct   = productQueryBuilder.unique();

        titleTextView.setText(currentProduct.getTitle());
        descriptionTextView.setText(currentProduct.getDescription());

        String cost = "$"+currentProduct.getCost();
        costTextView.setText(cost);

        if (currentProduct.getExistence() <= 0)
            existenceTextView.setTextColor(getColor(R.color.colorRed));

        existenceTextView.setText(String.valueOf(currentProduct.getExistence()));

        if (!currentProduct.getImage_url().isEmpty())
            Picasso.with(getApplicationContext()).
                                load(currentProduct.getImage_url()).
                                placeholder(R.drawable.ic_image_load).
                                error(R.drawable.ic_image_error).
                                into(productImageView);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 21)
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProdcut();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
