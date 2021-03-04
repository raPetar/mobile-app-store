package com.example.mobilestore.product;

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
import com.example.mobilestore.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public List<Product> productList;
    public List<Product> myProductList;
    private OnItemClickListener Listener;

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        Listener = listener;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        public ShapeableImageView productImage;
        public TextView productName;
        public TextView productDescription;
        public TextView productPrice;
        public Boolean isSaved;
        public ShapeableImageView savedForLaterButton;
        public Context context;


        public ProductViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageViewProduct);
            productName = itemView.findViewById(R.id.textViewProductName);
            productDescription = itemView.findViewById(R.id.textViewProductDescription);
            savedForLaterButton = itemView.findViewById(R.id.buttonSaveForLater);
            productPrice = itemView.findViewById(R.id.textViewProductPrice);
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

        void setImage(String url) {
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(productImage);
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view, Listener);
    }

    public ProductAdapter(List<Product> mProductList) {
        productList = mProductList;
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder holder, int position) {
        final Product currentProduct = productList.get(position);

        holder.setImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
        holder.productDescription.setText(currentProduct.getProductDescription());
        String price = holder.context.getString(R.string.dollarSign) + currentProduct.getProductPrice();
        holder.productPrice.setText(price);
        holder.savedForLaterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = holder.context.getSharedPreferences("wishList", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String json = pref.getString("wishList", null);
                Type type = new TypeToken<ArrayList<Product>>() {
                }.getType();
                myProductList = gson.fromJson(json, type);
                currentProduct.setIsSaved();
                boolean saved = false;
                if (myProductList != null) {
                    for (Product p : myProductList
                    ) {
                        if (currentProduct.getProductID() == p.getProductID()) {
                            saved = true;
                        } else {
                            saved = false;
                        }
                    }
                    if (!saved) {
                        myProductList.add(currentProduct);
                        Toast.makeText(holder.savedForLaterButton.getContext(), "Product saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(holder.context, "Product already saved", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    myProductList = new ArrayList<>();
                    myProductList.add(currentProduct);
                }
                json = gson.toJson(myProductList);
                editor.putString("wishList", json);
                editor.apply();
            }
        });
    }
}
