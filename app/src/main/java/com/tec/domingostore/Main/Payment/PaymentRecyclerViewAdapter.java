package com.tec.domingostore.Main.Payment;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.DB.Entities.SaleDao;
import com.tec.domingostore.Main.Customer.CustomersRecyclerViewAdapter;
import com.tec.domingostore.Main.Payment.Detail.PayActivity;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class PaymentRecyclerViewAdapter extends RecyclerView.Adapter<PaymentRecyclerViewAdapter.ViewHolder> {

    List<Payment> paymentList;

    public PaymentRecyclerViewAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public PaymentRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PaymentRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_payment_recyvler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.process(paymentList.get(position));
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView customerNameTextView, paymentDateTextView, paymentTextView;
        Customer currentCustomer;
        ConstraintLayout itemConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView    = itemView.findViewById(R.id.customerNamePaymentItemTextView);
            paymentDateTextView     = itemView.findViewById(R.id.paymentDatePaymentItemTextView);
            paymentTextView         = itemView.findViewById(R.id.paymentItemTextView);
            itemConstraintLayout    = itemView.findViewById(R.id.itemConstraintLayout);
        }

        private void process(final Payment currentPayment){

            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            paymentDateTextView.setText(simpleDateFormat.format(currentPayment.getPayment_date()));

            NumberFormat formater  = new DecimalFormat("#0.00");
            String paymentLabel   = "$"+formater.format(currentPayment.getPayment());
            paymentTextView.setText(paymentLabel);

            getCustomer(currentPayment.getSale_id());

            itemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent   = new Intent(itemView.getContext(), PayActivity.class);
                    intent.putExtra("payment_id",currentPayment.getDb_id());
                    itemView.getContext().startActivity(intent);
                }
            });

        }

        private void getCustomer(String sale_id){

            QueryBuilder<Sale> saleQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getSaleDao().queryBuilder();
            saleQueryBuilder.where(SaleDao.Properties.Db_id.eq(sale_id));

            QueryBuilder<Customer> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
            customerQueryBuilder.where(CustomerDao.Properties.Db_id.eq(saleQueryBuilder.unique().getCustomer_id()));
            currentCustomer = customerQueryBuilder.unique();

            String nameLabel    = currentCustomer.getName()+" "+currentCustomer.getLast_name();
            customerNameTextView.setText(nameLabel);

        }
    }
}
