package com.example.mobilestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mobilestore.product.OpenProductActivity;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.product.ProductAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {
    private LinearLayoutManager layoutManager;
    private List<Product> productList = new ArrayList<>();
    public Parcelable mListState;
    public String STATE_KEY = "listKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setActionbar();
        setContentView(R.layout.activity_search);
        loadProductList();
    }

    private void setActionbar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void loadProductList() {
        clearRecycler();
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String json = bundle.getString("searchProductList");
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();

        productList = gson.fromJson(json, type);
        if (productList == null || productList.isEmpty()) {
            Toast.makeText(this, "There is product with given criteria", Toast.LENGTH_SHORT).show();
        } else {
            populateRecycler();
        }
    }

    private void populateRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ProductAdapter adapter = new ProductAdapter(productList);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new ProductAdapter.OnItemClickListener() {
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

    private void clearRecycler() {
        productList.clear();
    }

    //Save RecyclerState
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mListState = layoutManager.onSaveInstanceState();
        outState.putParcelable(STATE_KEY, mListState);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (savedInstanceState != null) {
            mListState = savedInstanceState.getBundle(STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mListState != null) {
            layoutManager.onRestoreInstanceState(mListState);
        }
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