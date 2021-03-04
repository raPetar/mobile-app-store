package com.example.mobilestore.start;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.product.ProductCategory;
import com.example.mobilestore.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.StartViewHolder> {

    private OnItemClickListener Listener;
    public List<ProductCategory> productCategoryList;

    public CategoryAdapter(List<ProductCategory> _productCategoryList) {
        productCategoryList = _productCategoryList;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        Listener = listener;
    }

    public static class StartViewHolder extends RecyclerView.ViewHolder {

        public TextView categoryName;
        public ImageView categoryImage;

        public StartViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textViewCategoryName);
            categoryImage = itemView.findViewById(R.id.imageViewCategory);

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
    }

    @NonNull
    @Override
    public StartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new StartViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final StartViewHolder holder, int position) {
        final ProductCategory currentCategory = productCategoryList.get(position);
        holder.categoryName.setText(currentCategory.getCategoryName());
        holder.categoryImage.setImageResource(currentCategory.CategoryImage);
    }

    @Override
    public int getItemCount() {
        return productCategoryList.size();
    }
}
