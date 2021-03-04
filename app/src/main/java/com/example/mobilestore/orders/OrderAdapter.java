package com.example.mobilestore.orders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    public List<Order> OrderList;
    public Context Context;

    public OrderAdapter(List<Order> orderList, Context context) {
        OrderList = orderList;
        Context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderViewHolder holder, final int position) {
        final Order currentOrder = OrderList.get(position);
        holder.orderNumber.setText(currentOrder.getOrderNumber());
        holder.orderDate.setText(currentOrder.getOrderDate());
        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(holder.itemView.getContext(), OrderDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("orderNumber", holder.orderNumber.getText().toString());
                bundle.putString("orderDate", holder.orderDate.getText().toString());
                intent.putExtras(bundle);
                Context.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return OrderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber;
        TextView orderDate;
        MaterialButton detailsButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.textViewOrderNumber);
            orderDate = itemView.findViewById(R.id.textViewOrderDate);
            detailsButton = itemView.findViewById(R.id.buttonOrderDetails);
        }
    }
}
