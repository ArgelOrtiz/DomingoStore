package com.tec.domingostore.Main.Payment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.PaymentDao;
import com.tec.domingostore.Main.Products.Detail.Buy.ProductBuyRecyclerViewAdapter;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentFragment extends Fragment {

    RecyclerView paymentRecyclerView;
    List<Payment> paymentList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle("Cuentas por cobrar");

        initComponents();
    }

    private void initComponents(){
        paymentRecyclerView = getView().findViewById(R.id.paymentRecyclerView);

        paymentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));

        getPayments();
    }

    private void getPayments(){
        QueryBuilder<Payment> paymentQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getPaymentDao().queryBuilder();
        paymentQueryBuilder.where(PaymentDao.Properties.Paid.eq(false));
        paymentQueryBuilder.orderAsc(PaymentDao.Properties.Payment_date);
        paymentList = paymentQueryBuilder.list();

        paymentRecyclerView.setAdapter(new PaymentRecyclerViewAdapter(paymentList));

    }

    @Override
    public void onResume() {
        super.onResume();
        getPayments();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_payment, container, false);
    }
}
