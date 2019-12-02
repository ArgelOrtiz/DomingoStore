package com.tec.domingostore.Main.Products;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tec.domingostore.DB.Entities.Product;
import com.tec.domingostore.Main.Products.Detail.ProductDetailActivity;
import com.tec.domingostore.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class ProductsRecyclerViewAdapter extends RecyclerView.Adapter<ProductsRecyclerViewAdapter.ViewHolder> {

    List<Product> productList;

    public ProductsRecyclerViewAdapter(List<Product> productList){
        this.productList    = productList;
    }

    @NonNull
    @Override
    public ProductsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_products_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.process(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout itemConstraintLayout;
        ImageView productImageView;
        TextView titleTextView, descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemConstraintLayout    = itemView.findViewById(R.id.itemConstraintLayout);
            productImageView        = itemView.findViewById(R.id.productItemImageView);
            titleTextView           = itemView.findViewById(R.id.titleItemTextView);
            descriptionTextView     = itemView.findViewById(R.id.descriptionItemTextView);
        }

        protected void process(final Product currentProduct){

            titleTextView.setText(currentProduct.getTitle());
            descriptionTextView.setText(currentProduct.getDescription());

            if (!currentProduct.getImage_url().isEmpty())
                Picasso.with(itemView.getContext()).
                        load(currentProduct.getImage_url()).
                        placeholder(R.drawable.ic_image_load).
                        error(R.drawable.ic_image_error).
                        into(productImageView);

            itemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent   = new Intent(itemView.getContext(), ProductDetailActivity.class);
                    intent.putExtra("id",currentProduct.getId());
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
