package com.tec.domingostore.Main.Customer;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lowagie.text.pdf.PdfPTable;
import com.tec.domingostore.Common.Core;
import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.DB.SyncFirebase;
import com.tec.domingostore.Main.Customer.Detail.CustomerCreateActivity;
import com.tec.domingostore.Main.Products.ProductsRecyclerViewAdapter;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class CustomerFragment extends Fragment {

    private CustomerViewModel customerViewModel;
    RecyclerView customersRecyclerView;
    TextView noContentTextView;
    ConstraintLayout chargeConstraintLayout;

    private String FILE_NAME = "CustomersReport.pdf";




    List<Customer> customerList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Clientes");

        initComponents();
    }

    private void initComponents(){
        customerViewModel                               = ViewModelProviders.of(this).get(CustomerViewModel.class);
        customersRecyclerView                           = getView().findViewById(R.id.customersRecyclerView);
        noContentTextView                               = getView().findViewById(R.id.noContentCustomersTextView);
        chargeConstraintLayout                          = getView().findViewById(R.id.chargeConstraintLayout);
        FloatingActionButton addFlotatingActionButton   = getView().findViewById(R.id.addCustomerFlotatingActionButton);
        FloatingActionButton syncFlotatingActionButton  = getView().findViewById(R.id.syncCustomerFlotatingActionButton);
        FloatingActionButton shareFlotatingActionButton = getView().findViewById(R.id.shareSellerFlotatingActionButton);

        customersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));


        customerViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });

        addFlotatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), CustomerCreateActivity.class));
            }
        });

        syncFlotatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SyncFirebase.syncCustomer(getContext());
            }
        });

        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(SyncFirebase.UPDATED_CUSTOMERS));

        getCustomerData();



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
                PdfPTable table = new PdfPTable(5);

                table.addCell("Nombre ");
                table.addCell("apellidos ");
                table.addCell("Colonia ");
                table.addCell("Phone ");
                table.addCell("Saldo ");



                for (Customer currentCUstomer:customerList) {
                    table.addCell( currentCUstomer.getName());
                    table.addCell( currentCUstomer.getLast_name());
                    table.addCell(currentCUstomer.getColony());
                    table.addCell(currentCUstomer.getPhone());
                    table.addCell(String.valueOf(currentCUstomer.getBalance()));


                }


                Core.crearPDF(getContext(),table, FILE_NAME);

            }
        });

        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver,
                    new IntentFilter(SyncFirebase.UPDATED_CUSTOMERS));

        getCustomerData();





    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,intent.getStringExtra("message"),Toast.LENGTH_LONG).show();
            getCustomerData();
        }
    };

    private void getCustomerData(){
        chargeConstraintLayout.setVisibility(View.VISIBLE);
        noContentTextView.setVisibility(View.GONE);

        QueryBuilder<Customer> customerQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
        customerQueryBuilder.orderAsc(CustomerDao.Properties.Name);
        //List<Customer> customerList   = customerQueryBuilder.list();
        customerList   = customerQueryBuilder.list();

        if (customerList.size() > 0)
            chargeConstraintLayout.setVisibility(View.GONE);
        else
            noContentTextView.setVisibility(View.VISIBLE);

        customersRecyclerView.setAdapter(new CustomersRecyclerViewAdapter(customerList));

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_customer, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        SyncFirebase.syncCustomer(getContext());
        getCustomerData();
    }
}