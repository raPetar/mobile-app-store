package com.example.mobilestore.orders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.R;
import com.example.mobilestore.product.OpenProductActivity;
import com.example.mobilestore.product.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Used to show order specific details and items bound to that order
public class OrderDetailsActivity extends AppCompatActivity {

    private String orderNumber;
    private String orderDate;
    private String totalSum;
    private final List<Product> productList = new ArrayList<>();
    private TextView price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            orderNumber = bundle.getString("orderNumber");
            orderDate = bundle.getString("orderDate");
        }
        if (orderNumber != null && orderDate != null) {
            retrieveDetails();
            TextView tvOrderNumber = findViewById(R.id.textViewOrderNumberDetails);
            TextView tvOrderDate = findViewById(R.id.textViewOrderDateDetails);
            price = findViewById(R.id.textViewTotalProductPrice);
            tvOrderNumber.setText(orderNumber.toUpperCase());
            tvOrderDate.setText(orderDate);
        }
    }

    private void retrieveDetails() {
        // Responsible for retrieving order specific details
        if (orderNumber != null) {
            String connection = getString(R.string.connectionString);
            String url = connection + "orders/details/" + orderNumber.trim();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("productList");
                                totalSum = response.getString("totalSum");
                                Log.e("totalSum", totalSum);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int productID = jsonObject.getInt("productID");
                                    int categoryID = jsonObject.getInt("categoryID");
                                    String mainImage = jsonObject.getString("mainImage");
                                    String productName = jsonObject.getString("name");
                                    String productDescription = jsonObject.getString("description");
                                    productDescription = productDescription.substring(0, Math.min(productDescription.length(), 150));
                                    productDescription = productDescription + "...";
                                    int price = jsonObject.getInt("price");
                                    Product product = new Product(productID, categoryID, mainImage, null, productName, productDescription, price, false);
                                    productList.add(product);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            populateRecycler();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(OrderDetailsActivity.this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(request);
        } else {
            Toast.makeText(this, "An error occurred, please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private void populateRecycler() {

        // Populates the recycler with Products and sets total price of all the products
        String totalPrice = getString(R.string.dollarSign) + totalSum;
        price.setText(totalPrice);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrderDetails);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        OrderDetailsAdapter adapter = new OrderDetailsAdapter(productList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        adapter.setOnClickListener(new OrderDetailsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int forwardProductID;
                forwardProductID = productList.get(position).getProductID();
                Intent intent = new Intent(getApplicationContext(), OpenProductActivity.class);
                intent.putExtra("MainActivityExtraData", forwardProductID);
                onPause();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}