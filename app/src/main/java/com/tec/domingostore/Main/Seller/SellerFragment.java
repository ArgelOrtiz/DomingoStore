package com.tec.domingostore.Main.Seller;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.tec.domingostore.Common.Core;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.Entities.SellerDao;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.Main.Seller.Detail.SellerCreateActivity;
import com.tec.domingostore.MainActivity;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SellerFragment extends Fragment {

    private RecyclerView sellersRecyclerView;
    private ConstraintLayout chargeConstraintLayout;
    private  TextView noContentTextView;

    private String FILE_NAME = "SellersReport.pdf";

    List<Seller> sellerList;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Vendedores");

        initComponents();
    }


    private void initComponents(){

        sellersRecyclerView                             = getView().findViewById(R.id.sellersRecyclerView);
        chargeConstraintLayout                          = getView().findViewById(R.id.chargeConstraintLayout);
        noContentTextView                               = getView().findViewById(R.id.noContentSellersTextView);
        FloatingActionButton addFlotatingActionButton   = getView().findViewById(R.id.addSellerFlotatingActionButton);
        FloatingActionButton syncFlotatingActionButton  = getView().findViewById(R.id.syncSellerFlotatingActionButton);
        FloatingActionButton shareFlotatingActionButton = getView().findViewById(R.id.shareSellerFlotatingActionButton);

        sellersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));


        addFlotatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SellerCreateActivity.class));
            }
        });

        syncFlotatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncFirebase.syncSeller(getContext());
                getSellers();
            }
        });


        shareFlotatingActionButton.setOnClickListener(new View.OnClickListener() {
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
                PdfPTable table = new PdfPTable(6);
                table.addCell("Codigo ");
                table.addCell("Nombre ");
                table.addCell("apellidos ");
                table.addCell("Colonia ");
                table.addCell("Telefono ");
                table.addCell("Comision ");


                for (Seller currentSEller:sellerList) {
                    table.addCell(currentSEller.getCode());
                    table.addCell( currentSEller.getName());
                    table.addCell( currentSEller.getLast_name());
                    table.addCell(currentSEller.getColony());
                    table.addCell( currentSEller.getPhone());
                    table.addCell(String.valueOf(currentSEller.getCommission()));

                }


                Core.crearPDF(getContext(),table, FILE_NAME);

            }
        });

        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(SyncFirebase.UPDATED_SELLERS));

        getSellers();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
            getSellers();
        }
    };

    private void getSellers(){
        chargeConstraintLayout.setVisibility(View.VISIBLE);
        noContentTextView.setVisibility(View.GONE);

        QueryBuilder<Seller> sellerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getSellerDao().queryBuilder();
        sellerQueryBuilder.orderAsc(SellerDao.Properties.Name);
         sellerList   = sellerQueryBuilder.list();

        if (sellerList.size() > 0)
            sellersRecyclerView.setAdapter(new SellerRecyclerViewAdapter(sellerList));
        else
            noContentTextView.setVisibility(View.VISIBLE);

        chargeConstraintLayout.setVisibility(View.GONE);
    }





    @Override
    public void onResume() {
        super.onResume();
        SyncFirebase.syncSeller(getContext());
        getSellers();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_seller, container, false);
    }
}