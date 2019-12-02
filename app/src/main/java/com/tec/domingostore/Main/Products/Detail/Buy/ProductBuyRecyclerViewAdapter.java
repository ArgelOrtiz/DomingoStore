package com.tec.domingostore.Main.Products.Detail.Buy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tec.domingostore.DB.Entities.Payment;
import com.tec.domingostore.Main.Customer.CustomersRecyclerViewAdapter;
import com.tec.domingostore.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ProductBuyRecyclerViewAdapter extends RecyclerView.Adapter<ProductBuyRecyclerViewAdapter.ViewHolder> {

    private List<Payment> paymentList;

    public ProductBuyRecyclerViewAdapter(List<Payment> paymentList) {
        this.paymentList = paymentList;
    }

    @NonNull
    @Override
    public ProductBuyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ProductBuyRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_product_buy_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(ProductBuyRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.process(paymentList.get(position));
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView paymentNumberTextView, paymentDateTextView, paymentTextView, balanceTextView;

         ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            paymentNumberTextView   = itemView.findViewById(R.id.paymentNumberItemTextView);
            paymentDateTextView     = itemView.findViewById(R.id.paymentDateItemTextView);
            paymentTextView         = itemView.findViewById(R.id.paymentItemTextView);
            balanceTextView         = itemView.findViewById(R.id.balanceItemTextView);
            constraintLayout        = itemView.findViewById(R.id.productBuyConstraintLayout);

        }

        private void process(Payment currentPayment){

            paymentNumberTextView.setText(String.valueOf(currentPayment.getPayment_number()));

            String pattern = "dd-MM-yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            paymentDateTextView.setText(simpleDateFormat.format(currentPayment.getPayment_date()));

            double payment  = currentPayment.getPayment();

            NumberFormat formatter  = new DecimalFormat("#0.00");
            String paymentLabel     = "$"+formatter.format(payment);

            paymentTextView.setText(paymentLabel);

            String balanceLabel     = "$"+formatter.format(currentPayment.getBalance());
            balanceTextView.setText(balanceLabel);

            if (currentPayment.getPaid())
                constraintLayout.setBackgroundColor(itemView.getResources().getColor(R.color.colorBackground));
            else
                constraintLayout.setBackgroundColor(itemView.getResources().getColor(R.color.colorWhite));

        }
    }
}
