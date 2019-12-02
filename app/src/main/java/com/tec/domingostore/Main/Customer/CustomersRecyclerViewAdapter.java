package com.tec.domingostore.Main.Customer;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.Main.Customer.Detail.CustomerEditActivity;
import com.tec.domingostore.Main.Products.ProductsRecyclerViewAdapter;
import com.tec.domingostore.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class CustomersRecyclerViewAdapter extends RecyclerView.Adapter<CustomersRecyclerViewAdapter.ViewHolder> {

    List<Customer>  customerList;

    public CustomersRecyclerViewAdapter(List<Customer> customerList) {
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomersRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomersRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_customer_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CustomersRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.process(customerList.get(position));
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, lastNameTextView, balanceTextView;
        ConstraintLayout itemConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView            = itemView.findViewById(R.id.nameCustomerItemTextView);
            lastNameTextView        = itemView.findViewById(R.id.lastNameCustomerItemTextView);
            balanceTextView         = itemView.findViewById(R.id.balanceCustomerItemTextView);
            itemConstraintLayout    = itemView.findViewById(R.id.itemConstraintLayout);

        }

        private void process(final Customer currentCustomer){

            nameTextView.setText(currentCustomer.getName());
            lastNameTextView.setText(currentCustomer.getLast_name());
            balanceTextView.setText(String.valueOf(currentCustomer.getBalance()));

            itemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent   = new Intent(itemView.getContext(), CustomerEditActivity.class);
                    intent.putExtra("id",currentCustomer.getId());
                    itemView.getContext().startActivity(intent);

                }
            });

        }
    }
}
