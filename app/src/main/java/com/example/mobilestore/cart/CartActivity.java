package com.example.mobilestore.cart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.Volley;
import com.example.mobilestore.LoginActivity;
import com.example.mobilestore.orders.OrdersActivity;
import com.example.mobilestore.product.OpenProductActivity;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.R;

import com.example.mobilestore.users.Users;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

// Responsible for sending orders and purchased products as well
// as returning the Order Number
public class CartActivity extends AppCompatActivity {
    private List<Product> cartList = new ArrayList<>();
    private TextView totalCartPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        setupActionBar();
        loadCartList();
    }

    private void clearCartList() {
        SharedPreferences pref = getSharedPreferences("cart pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        cartList.clear();
        editor.putString("cartList", null);
        editor.apply();
    }

    private void loadCartList() {
        SharedPreferences pref = getSharedPreferences("cart pref", MODE_PRIVATE);
        String json = pref.getString("cartList", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        cartList = gson.fromJson(json, type);
        if (cartList == null || cartList.isEmpty()) {
            setContentView(R.layout.empty_cart);
        } else {
            populateRecycler();
            calculateTotalPrice();
        }
    }

    private void calculateTotalPrice() {
        int price = 0;
        for (Product p : cartList
        ) {
            price += p.getProductPrice();
        }
        String totalPrice = getString(R.string.dollarSign) + String.valueOf(price);
        totalCartPrice.setText(totalPrice);
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        totalCartPrice = findViewById(R.id.textViewTotalProductPrice);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        CartAdapter adapter = new CartAdapter(cartList, totalCartPrice, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnClickListener(new CartAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int forwardProductID;
                forwardProductID = cartList.get(position).getProductID();
                Intent intent = new Intent(getApplicationContext(), OpenProductActivity.class);
                intent.putExtra("MainActivityExtraData", forwardProductID);
                onPause();
                startActivity(intent);
            }
        });
    }

    public void ContinueShoppingPressed(View view) {
        onBackPressed();
        finish();
    }

    public void CheckOutPressed(View view) {
        //Responsible for checking if the user is logged in as well as converting current
        // cart items into JSON and sending them to the API
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        String jsonUser = pref.getString("user data", "");

        Gson gson = new Gson();
        Type type = new TypeToken<Users>() {
        }.getType();
        Users user = gson.fromJson(jsonUser, type);

        if (user == null) {
            @SuppressLint("InflateParams") View viewNotLoggedIn = getLayoutInflater().inflate(R.layout.no_profile_checkout,null);
            MaterialButton loginButton = viewNotLoggedIn.findViewById(R.id.buttonLoginCheckout);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            setContentView(viewNotLoggedIn);
        } else {
            JSONArray cartListProductID = new JSONArray();

            for (Product product : cartList
            ) {
                try {
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("ProductID", product.getProductID());
                    jsonObject1.put("Quantity", product.getQuantity());
                    cartListProductID.put(jsonObject1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", user.UserName);
                jsonObject.put("productList", cartListProductID);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String[] orderNumber = {""};
            String connection = getString(R.string.connectionString);
            String url = connection + "orders";

            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.PUT,
                    url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        orderNumber[0] = response.getString("orderNumber");
                        clearCartList();

                        @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.purchase_successful, null);
                        TextView tvOrderNumber = v.findViewById(R.id.textViewOrderNumber);
                        tvOrderNumber.setText(orderNumber[0]);
                        MaterialButton myOrdersButton = v.findViewById(R.id.buttonMyOrders);
                        myOrdersButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), OrdersActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        setContentView(v);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CartActivity.this, "An error has occurred, please try again later!", Toast.LENGTH_SHORT).show();
                }

            });
            queue.add(objectRequest);
        }
    }
}









