package com.tec.domingostore.Main.Sale;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.SaleDao;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

public class SalesFragment extends Fragment {

    RecyclerView salesRecyclerView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initComponents();

    }

    private void initComponents(){
        salesRecyclerView   = getView().findViewById(R.id.salesRecyclerView);

        salesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));

        getSales();
    }

    private void getSales(){
        QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
        saleQueryBuilder.orderDesc(SaleDao.Properties.Total);
        List<Sale> saleList = saleQueryBuilder.list();

        salesRecyclerView.setAdapter(new SalesRecyclerViewAdapter(saleList));


    }

    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sales, container, false);
    }
}