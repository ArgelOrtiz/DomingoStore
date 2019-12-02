package com.tec.domingostore.Main.Seller;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tec.domingostore.DB.Entities.Seller;
import com.tec.domingostore.Main.Seller.Detail.SellerEditActivity;
import com.tec.domingostore.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SellerRecyclerViewAdapter extends RecyclerView.Adapter<SellerRecyclerViewAdapter.ViewHolder> {

    List<Seller> sellerList;

    public SellerRecyclerViewAdapter(List<Seller> sellerList) {
        this.sellerList = sellerList;
    }

    @NonNull
    @Override
    public SellerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SellerRecyclerViewAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_seller_recycler_view_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SellerRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.process(sellerList.get(position));
    }

    @Override
    public int getItemCount() {
        return sellerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, lastNameextView,commissionTextView;
        ConstraintLayout itemConstraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView            = itemView.findViewById(R.id.nameSellerItemTextView);
            lastNameextView         = itemView.findViewById(R.id.lastNameSellerItemTextView);
            commissionTextView      = itemView.findViewById(R.id.commissionSellerItemTextView);
            itemConstraintLayout    = itemView.findViewById(R.id.itemConstraintLayout);
        }

        private void process(final Seller currentSeller){

            nameTextView.setText(currentSeller.getName());
            lastNameextView.setText(currentSeller.getLast_name());

            String commission   = "$"+currentSeller.getCommission();

            commissionTextView.setText(commission);

            itemConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent   = new Intent(itemView.getContext(),SellerEditActivity.class);
                    intent.putExtra("id",currentSeller.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
