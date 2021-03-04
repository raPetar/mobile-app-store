package com.example.mobilestore.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.R;

import java.util.List;

public class TopPickAdapter extends RecyclerView.Adapter<TopPickAdapter.TopPickViewHolder> {

    public List<Product> topPickList;
    private OnItemClickListener Listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        Listener = listener;
    }

    public TopPickAdapter(List<Product> topPickList) {
        this.topPickList = topPickList;
    }

    @NonNull
    @Override
    public TopPickViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_pick_item, parent, false);
        return new TopPickViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPickViewHolder holder, int position) {
        Product currentProduct = topPickList.get(position);
        List<String> images = currentProduct.getImages();
        holder.setImage(currentProduct.getMainImage());
        holder.productName.setText(currentProduct.getProductName());
    }

    @Override
    public int getItemCount() {
        return topPickList.size();
    }

    static class TopPickViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView productName;

        TopPickViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            productName = itemView.findViewById(R.id.textViewProductNameTopPick);
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
            Glide.with(itemView.getContext()).setDefaultRequestOptions(options).load(url).into(imageView);
        }
    }
}
