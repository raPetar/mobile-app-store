package com.example.mobilestore.list;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;

import java.util.List;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.WishListViewHolder> {

    public List<Product> wishList;
    public Context context;
    private OnItemClickListener Listener;

    public WishListAdapter(List<Product> _wishList, Context _context) {
        this.wishList = _wishList;
        context = _context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        Listener = listener;
    }

    @NonNull
    @Override
    public WishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false);
        return new WishListViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final WishListViewHolder holder, final int position) {
        final Product currentProduct = wishList.get(position);

        holder.setProductImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
        String productDescription = currentProduct.getProductDescription();
        productDescription = productDescription.substring(0, Math.min(productDescription.length(), 150));
        productDescription = productDescription + "...";
        holder.productDescription.setText(productDescription);

        String price = context.getString(R.string.dollarSign) + String.valueOf(currentProduct.getProductPrice());
        holder.productPrice.setText(price);
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = holder.context.getSharedPreferences("wishList", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(holder.removeButton.getContext(), "Product removed", Toast.LENGTH_SHORT).show();
                wishList.remove(position);
                Gson gson = new Gson();
                String json = gson.toJson(wishList);
                editor.putString("wishList", json);
                editor.apply();
                notifyDataSetChanged();
            }


        });
    }

    @Override
    public int getItemCount() {
        return wishList.size();
    }

    public static class WishListViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView imageView;
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public ShapeableImageView removeButton;
        public Context context;

        public WishListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewProductList);
            productName = itemView.findViewById(R.id.textViewProductNameList);
            productDescription = itemView.findViewById(R.id.textViewProductDescriptionList);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
            removeButton = itemView.findViewById(R.id.imageButtonRemoveList);
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

        void setProductImage(String url) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(imageView);
        }
    }
}
