package com.tec.domingostore.Main.Products;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.tec.domingostore.Common.Core;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.Main.Products.Detail.ProductCreateActivity;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {

    private ProductsViewModal productsViewModal;
    private RecyclerView productsRecyclerView;
    private ConstraintLayout chargeConstraintLayout;
    private TextView noContentTextView;


    private String FILE_NAME = "ProductReport.pdf";
    List<Product> productList;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Productos");

        initComponent();

    }

    private void initComponent(){
        productsViewModal               = ViewModelProviders.of(this).get(ProductsViewModal.class);
        productsRecyclerView            = getView().findViewById(R.id.productsRecyclerView);
        chargeConstraintLayout          = getView().findViewById(R.id.chargeConstraintLayout);
        noContentTextView               = getView().findViewById(R.id.noContentProductsTextView);
        ImageButton createImageButton   = getView().findViewById(R.id.createProductsImageButton);
        ImageButton refreshImageButton  = getView().findViewById(R.id.refreshProductsImageButton);
        ImageButton reportProductsImageButton  = getView().findViewById(R.id.reportProductsImageButton);

        productsRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL, false));

        productsViewModal.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        refreshImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chargeConstraintLayout.setVisibility(View.VISIBLE);
                SyncFirebase.syncColletions(getContext());
            }
        });

        createImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ProductCreateActivity.class));
            }
        });

        reportProductsImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Permisos
                if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                            1000);
                }

                // Insertamos una tabla

                PdfPTable table = new PdfPTable(5);


                table.addCell("title ");
                table.addCell("description ");
                table.addCell("existence ");
                table.addCell("price ");
                table.addCell("status ");

                getProductsData();


                for (Product currentPRoduct:productList) {
                    table.addCell(currentPRoduct.getTitle());
                    table.addCell( currentPRoduct.getDescription());
                    table.addCell(String.valueOf(currentPRoduct.getExistence()));
                    table.addCell(String.valueOf(currentPRoduct.getCost()));
                    table.addCell(String.valueOf(currentPRoduct.getStatus()));

                }


                Core.crearPDF(getContext(),table, FILE_NAME);

            }
        });



        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(SyncFirebase.UPDATED_PRODUCTS));

        getProductsData();
    }

        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
            getProductsData();
        }
    };

    private void getProductsData(){
        chargeConstraintLayout.setVisibility(View.VISIBLE);
        noContentTextView.setVisibility(View.GONE);

        QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
        productQueryBuilder.where(ProductDao.Properties.Deleted.eq(false));
        productQueryBuilder.orderAsc(ProductDao.Properties.Title);
         productList   = productQueryBuilder.list();

        if (productList.size() > 0)
            chargeConstraintLayout.setVisibility(View.GONE);
        else
            noContentTextView.setVisibility(View.VISIBLE);

        productsRecyclerView.setAdapter(new ProductsRecyclerViewAdapter(productList));
    }

    @Override
    public void onResume() {
        super.onResume();
       // getProductsData();
        chargeConstraintLayout.setVisibility(View.VISIBLE);
        SyncFirebase.syncColletions(getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_products, container, false);

    }
}