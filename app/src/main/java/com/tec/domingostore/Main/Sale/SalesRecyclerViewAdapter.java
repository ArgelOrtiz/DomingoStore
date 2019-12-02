package com.tec.domingostore.Main.Sale;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tec.domingostore.DB.DAOAccess;
import com.tec.domingostore.DB.Entities.Customer;
import com.tec.domingostore.DB.Entities.CustomerDao;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.DB.Entities.ProductDao;
import com.tec.domingostore.DB.Entities.Sale;
import com.tec.domingostore.Main.Sale.Detail.SaleDetailActivity;
import com.tec.domingostore.Main.Seller.SellerRecyclerViewAdapter;
import com.tec.domingostore.R;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SalesRecyclerViewAdapter extends RecyclerView.Adapter<SalesRecyclerViewAdapter.ViewHolder> {

    List<Sale> saleList;

    public SalesRecyclerViewAdapter(List<Sale> saleList) {
        this.saleList = saleList;
    }

    @NonNull
    @Override
    public SalesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SalesRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_sales_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SalesRecyclerViewAdapter.ViewHolder holder, int position) {

        holder.process(saleList.get(position));
    }

    @Override
    public int getItemCount() {
        return saleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleProductTextView, nameCustomerTextView, totalTextView;
        Product currentProduct;
        Customer currentCustomer;
        ConstraintLayout saleConstraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleProductTextView    = itemView.findViewById(R.id.titleProductSaleItemTextView);
            nameCustomerTextView    = itemView.findViewById(R.id.nameCustomerSaleItemTextView);
            totalTextView           = itemView.findViewById(R.id.totalSaleItemTextView);
            saleConstraintLayout    = itemView.findViewById(R.id.saleItemConstraintLayout);
        }

        private void process(final Sale currentSale){
            getProduct(currentSale.getProduct_id());
            getCustomer(currentSale.getCustomer_id());


            String titleProductLabel    = currentProduct.getTitle();
            titleProductTextView.setText(titleProductLabel);

            String nameCustomerLabel    = currentCustomer.getName()+" "+currentCustomer.getLast_name();
            nameCustomerTextView.setText(nameCustomerLabel);

            if (currentSale.getTotal() > 0) {
                totalTextView.setTextColor(itemView.getResources().getColor(R.color.colorRed));
                String totalLabel   = "$"+currentSale.getTotal();
                totalTextView.setText(totalLabel);
            }else {
                totalTextView.setTextColor(itemView.getResources().getColor(R.color.colorGreen));
                totalTextView.setText("Pagado");
            }




            saleConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent   = new Intent(itemView.getContext(),SaleDetailActivity.class);
                    intent.putExtra("sale_id",currentSale.getDb_id());
                    itemView.getContext().startActivity(intent);
                }
            });

        }

        private void getProduct(String product_id){
            QueryBuilder<Product> productQueryBuilder   = DAOAccess.sharedInstance().getDaoSession().getProductDao().queryBuilder();
            productQueryBuilder.where(ProductDao.Properties.Db_id.eq(product_id));
            currentProduct  = productQueryBuilder.unique();
        }

        private void getCustomer(String customer_id){
            QueryBuilder<Customer> customerQueryBuilder = DAOAccess.sharedInstance().getDaoSession().getCustomerDao().queryBuilder();
            customerQueryBuilder.where(CustomerDao.Properties.Db_id.eq(customer_id));
            currentCustomer = customerQueryBuilder.unique();
        }
    }
}
