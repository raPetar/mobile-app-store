package com.example.mobilestore.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.R;

public class TopPickImageAdapter extends  RecyclerView.Adapter<TopPickImageAdapter.TopPickImageViewHolder>{

    public Product product;
    public TopPickImageAdapter(Product product) {
        this.product = product;
    }

    @NonNull
    @Override
    public TopPickImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewImage = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_image_container,parent,false);
        return new TopPickImageViewHolder(viewImage);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPickImageViewHolder holder, int position) {
        holder.setImageView(product.getImages().get(position));
    }

    @Override
    public int getItemCount() {
        return product.getImages().size();
    }

    static class TopPickImageViewHolder extends RecyclerView.ViewHolder{
        private final ImageView imageView;
        public TopPickImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageContainer);
        }
        void setImageView(String url){
            RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(imageView);
        }
    }
}
