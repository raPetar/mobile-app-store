package com.example.mobilestore.orders;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.R;
import com.example.mobilestore.users.Users;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Retrieves all user specific orders and starts the OrderDetailsActivity
public class OrdersActivity extends AppCompatActivity {

    public LinearLayoutManager layoutManager;
    public List<Order> orderList = new ArrayList<>();
    private String UserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        loadUser();
        retrieveOrders();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void loadUser() {
        // Retrieves the currently logged in user
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        String json = pref.getString("user data", null);

        Gson gson = new Gson();
        Type type = new TypeToken<Users>() {
        }.getType();
        Users user = gson.fromJson(json, type);
        if (user != null) {
            UserName = user.UserName;
        }
    }

    private void retrieveOrders() {
        String connection = getString(R.string.connectionString);
        String url = connection + "orders/" + UserName.trim();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("orderList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String productNumber = jsonObject.getString("orderNumber");
                                String orderDate = jsonObject.getString("dateOfPurchase");

                                Order order = new Order(productNumber, orderDate);
                                orderList.add(order);
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
                    }
                });
        requestQueue.add(request);
    }

    private void populateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        OrderAdapter adapter = new OrderAdapter(orderList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
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