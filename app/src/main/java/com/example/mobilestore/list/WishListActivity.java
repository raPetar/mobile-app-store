package com.example.mobilestore.list;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.mobilestore.product.OpenProductActivity;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// Responsible for showing the wishlist items as well as removing them from the list
public class WishListActivity extends AppCompatActivity {
    private List<Product> wishList = new ArrayList<>();
    private WishListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);

        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_wishlist);
        loadWishlist();
    }

    private void loadWishlist() {
        SharedPreferences pref = getSharedPreferences("wishList", MODE_PRIVATE);

        Gson gson = new Gson();
        String json = pref.getString("wishList", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        wishList = gson.fromJson(json, type);
        if (wishList == null) {
            setContentView(R.layout.no_wishlist);
        } else {
            populateRecycler();
        }
    }

    private void populateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewMyList);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new WishListAdapter(wishList, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new WishListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int forwardProductID;
                forwardProductID = wishList.get(position).getProductID();
                Intent intent = new Intent(getApplicationContext(), OpenProductActivity.class);
                intent.putExtra("MainActivityExtraData", forwardProductID);
                onPause();
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.wishlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.clearAll) {
            clearWishlist();
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearWishlist() {
        if (wishList != null) {
            wishList.clear();
            saveWishlist();
            adapter.notifyDataSetChanged();
        }
    }

    private void saveWishlist() {
        SharedPreferences pref = getSharedPreferences("wishList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(wishList);
        editor.putString("wishList", json);
        editor.apply();
    }
}