package com.example.mobilestore.orders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobilestore.R;
import com.example.mobilestore.product.Product;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder> {
    public List<Product> productList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public OrderDetailsAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public OrderDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_item, parent, false);
        return new OrderDetailsViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailsViewHolder holder, int position) {
        Product currentProduct = productList.get(position);

        holder.setImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
        holder.productDescription.setText(currentProduct.getProductDescription());
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class OrderDetailsViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView productName;
        TextView productDescription;

        public OrderDetailsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productDescription = itemView.findViewById(R.id.textViewProductDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }

        void setImage(String url) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(productImage);
        }
    }

}
