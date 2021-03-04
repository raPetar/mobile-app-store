package com.example.mobilestore.start;

import android.content.Context;
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

public class BrowseAllAdapter extends RecyclerView.Adapter<BrowseAllAdapter.BrowseAllViewHolder> {

    public List<Product> browseProductList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public BrowseAllAdapter(List<Product> productList) {
        this.browseProductList = productList;
    }

    @NonNull
    @Override
    public BrowseAllViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_product_item, parent, false);
        return new BrowseAllViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BrowseAllViewHolder holder, int position) {
        Product currentProduct = browseProductList.get(position);
        holder.setImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
        String price = holder.context.getString(R.string.dollarSign) + currentProduct.getProductPrice();
        holder.productPrice.setText(price);
    }

    @Override
    public int getItemCount() {
        return browseProductList.size();
    }

    static class BrowseAllViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView productName, productPrice;
        Context context;

        public BrowseAllViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewBrowseProduct);
            productName = itemView.findViewById(R.id.textViewBrowseProductName);
            productPrice = itemView.findViewById(R.id.textViewBrowsePrice);
            context = itemView.getContext();

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

        public void setImage(String url) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(productImage);
        }
    }
}
