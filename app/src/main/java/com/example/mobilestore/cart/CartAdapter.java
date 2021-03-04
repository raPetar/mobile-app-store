package com.example.mobilestore.cart;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.StartViewHolder> {
    public List<Product> cartList;
    private OnItemClickListener Listener;
    public TextView totalCartPrice;
    public Context context;

    public CartAdapter(List<Product> _cartList, TextView _totalPrice, Context context) {
        cartList = _cartList;
        totalCartPrice = _totalPrice;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        Listener = listener;
    }

    public static class StartViewHolder extends RecyclerView.ViewHolder {
        public ImageView productImage;
        public TextView productName;
        public TextView productPrice;
        public TextView quantity;
        public ShapeableImageView removeButton;
        public ShapeableImageView quantityUp;
        public ShapeableImageView quantityDown;

        public StartViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageViewProductCart);
            productName = itemView.findViewById(R.id.textViewProductNameCart);
            productPrice = itemView.findViewById(R.id.textViewProductPriceCart);
            quantity = itemView.findViewById(R.id.textViewNumberQuantityCart);
            quantityUp = itemView.findViewById(R.id.imageButtonQuantityUp);
            quantityDown = itemView.findViewById(R.id.imageButtonQuantityDown);
            removeButton = itemView.findViewById(R.id.imageButtonRemoveCart);
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
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(productImage);
        }
    }

    @NonNull
    @Override
    public StartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new StartViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final StartViewHolder holder, final int position) {
        final Product currentProduct = cartList.get(position);

        holder.setProductImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
        String price = context.getString(R.string.dollarSign) + currentProduct.getProductPrice();
        holder.productPrice.setText(price);
        holder.quantity.setText(String.valueOf(currentProduct.getQuantity()));
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences pref = context.getSharedPreferences("cart pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Toast.makeText(holder.removeButton.getContext(), "Product removed", Toast.LENGTH_SHORT).show();
                cartList.remove(position);
                Gson gson = new Gson();
                String json2 = gson.toJson(cartList);
                editor.putString("cartList", json2);
                editor.apply();
                totalPrice();
            }


        });
        final int getQuantity = currentProduct.getQuantity();
        final int[] currentQuantity = {1};
        holder.quantityUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentProduct.getQuantity() <= 98) {
                    currentQuantity[0] = getQuantity + 1;

                    currentProduct.setQuantity(currentQuantity[0]);
                    holder.quantity.setText(String.valueOf(currentProduct.getQuantity()));

                    SharedPreferences pref = context.getSharedPreferences("cart pref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    if (cartList.contains(currentProduct)) {
                        int index = cartList.indexOf(currentProduct);
                        cartList.get(index).setQuantity(currentProduct.getQuantity());
                    }

                    Gson gson = new Gson();
                    String json2 = gson.toJson(cartList);
                    editor.putString("cartList", json2);
                    editor.apply();

                    totalPrice();
                } else {
                    Toast.makeText(context, "Maximum Quantity Reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.quantityDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (currentProduct.getQuantity() >= 2) {
                    currentQuantity[0] = getQuantity - 1;

                    currentProduct.setQuantity(currentQuantity[0]);
                    holder.quantity.setText(String.valueOf(currentProduct.getQuantity()));
                    totalPrice();
                }
            }
        });
    }

    private void totalPrice() {
        int price = 0;
        for (Product p : cartList
        ) {
            price += p.getProductPrice() * p.getQuantity();
        }
        String totalPrice = context.getString(R.string.dollarSign) + String.valueOf(price);
        totalCartPrice.setText(String.valueOf(totalPrice));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }
}
