package com.example.mobilestore.product;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.mobilestore.reviews_questions.Question;
import com.example.mobilestore.reviews_questions.Review;
import com.example.mobilestore.reviews_questions.ReviewsQuestionsAdapter;
import com.example.mobilestore.start.TopPickImageAdapter;
import com.example.mobilestore.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Responsible for fetching product specific information as well as Reviews and Questions regarding the product
//Handles adding to Shopping Cart and Wishlist

public class OpenProductActivity extends AppCompatActivity {

    private ShapeableImageView productImage;
    private TextView productName, productDescription, productPrice;
    private Button loadMoreButton;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ReviewsQuestionsAdapter adapter;
    private final List<Question> questionList = new ArrayList<>();
    private final List<Review> reviewList = new ArrayList<>();
    private final List<String> urlList = new ArrayList<>();
    private List<Product> cartList = new ArrayList<>();
    private int ProductID, type;
    private Product product;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_product);
        setUpActionBar();
        retrieveIntentData();
        findViews();
        retrieveProductInformation();
        tabLayoutListener();
        retrieveReviewsQuestions(0);
    }

    private void setUpActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.actionBarTittle));
    }

    private void retrieveIntentData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        ProductID = bundle.getInt("MainActivityExtraData");
    }

    private void findViews() {
        loadMoreButton = findViewById(R.id.buttonLoadMore);
        recyclerView = findViewById(R.id.RecyclerViewReviewQuestion);
        tabLayout = findViewById(R.id.tabLayoutReviewsQuestions);
        productImage = findViewById(R.id.imageViewProduct);
        productDescription = findViewById(R.id.textViewDescriptionText);
        productName = findViewById(R.id.textViewProductName);
        productPrice = findViewById(R.id.textViewProductPrice);
    }

    private void retrieveProductInformation() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String connection = getString(R.string.connectionString);
        String url = connection + "products/" + ProductID;
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int productID = response.getInt("productID");
                    int categoryID = response.getInt("categoryID");
                    String mainImage = response.getString("mainImage");
                    String productName = response.getString("name");
                    String productDescription = response.getString("description");
                    int price = response.getInt("price");
                    JSONArray array = response.getJSONArray("productImages");
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject object = array.getJSONObject(j);
                        String url = object.getString("url");
                        urlList.add(url);
                    }
                    product = new Product(productID, categoryID, mainImage, urlList, productName, productDescription, price, false);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e("errorProduct", ex.toString());
                }
                populateProductInformation();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response", error.toString());
            }
        });
        requestQueue.add(request);
    }

    private void populateProductInformation() {
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_error);
        Glide.with(getApplicationContext()).setDefaultRequestOptions(options).load(product.getMainImage()).into(productImage);
        productName.setText(product.getProductName());
        productDescription.setText(product.getProductDescription());
        String price = getString(R.string.dollarSign) + String.valueOf(product.getProductPrice());
        productPrice.setText(price);

        ViewPager2 viewPager2 = findViewById(R.id.viewPager2ProductImages);
        TopPickImageAdapter adapter = new TopPickImageAdapter(product);
        viewPager2.setAdapter(adapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
    }

    private void retrieveReviewsQuestions(int tabPosition) {
        //Depending on the tab selected, it will either return and fill the review list
        //or the question list. tabPosition is acquired from the TabListener
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        if (tabPosition == 0) {
            String connection = getString(R.string.connectionString);
            String url = connection + "reviews/" + ProductID;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("reviewList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            int reviewID = jsonObject.getInt("reviewID");
                            int mainThread = jsonObject.getInt("mainThread");
                            String userName = jsonObject.getString("userName");
                            String text = jsonObject.getString("text");
                            reviewList.add(new Review(reviewID, mainThread, userName, text));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    populateWithReviews();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response", error.toString());
                }
            });
            requestQueue.add(request);
        } else if (tabPosition == 1) {
            String connection = getString(R.string.connectionString);
            String url = connection + "questions/" + ProductID;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray array = response.getJSONArray("questionList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);
                            int questionID = jsonObject.getInt("questionID");
                            int mainThread = jsonObject.getInt("mainThread");
                            String userName = jsonObject.getString("userName");
                            String text = jsonObject.getString("text");
                            questionList.add(new Question(questionID, mainThread, userName, text));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    populateWithQuestions();
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            requestQueue.add(request);
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

    private void loadCartList() {
        SharedPreferences pref = getSharedPreferences("cart pref", MODE_PRIVATE);
        String json = pref.getString("cartList", null);
        Gson gson = new Gson();
        Type type = new TypeToken<List<Product>>() {
        }.getType();
        cartList = gson.fromJson(json, type);
    }

    private void checkIfInCart() {
        //Responsible for checking if the Product is already in the wishlist or not
        boolean isAdded = false;
        if (cartList != null) {
            for (Product p : cartList
            ) {
                if (p.getProductID() == product.getProductID()) {
                    isAdded = true;
                    break;
                }
            }
        } else {
            cartList = new ArrayList<>();
        }
        if (isAdded) {
            Toast.makeText(this, "Product already added", Toast.LENGTH_SHORT).show();
        } else {
            cartList.add(product);
        }
    }

    public void LoadMorePressed(View view) {
        //Loads the next 5 comments for the select tab (Reviews or Questions)
        int loaded = adapter.loadMore();
        if (type == 0) {
            if (loaded == reviewList.size()) {
                loadMoreButton.setVisibility(View.GONE);
            }
        } else if (type == 1) {
            if (loaded == questionList.size()) {
                loadMoreButton.setVisibility(View.GONE);
            }
        }
    }

    private void tabLayoutListener() {
        // Responsible for calling the "retrieveReviewsQuestions" method and populating the select tab recycler
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        loadMoreButton.setVisibility(View.VISIBLE);
                        if (reviewList.size() == 0) {
                            retrieveReviewsQuestions(tab.getPosition());
                        } else {
                            type = 0;
                            populateWithReviews();
                        }
                        break;
                    case 1:
                        loadMoreButton.setVisibility(View.VISIBLE);
                        if (questionList.size() == 0) {
                            retrieveReviewsQuestions(tab.getPosition());
                        } else {
                            type = 1;
                            populateWithQuestions();
                        }
                        break;
                    default:
                        retrieveReviewsQuestions(0);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void populateWithReviews() {
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new ReviewsQuestionsAdapter(reviewList, questionList, 0, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
    }

    private void populateWithQuestions() {
        linearLayoutManager = new LinearLayoutManager(this);
        adapter = new ReviewsQuestionsAdapter(reviewList, questionList, 1, getApplicationContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter.notifyDataSetChanged();
    }

    public void ButtonAddToCartPressed(View view) {
        loadCartList();
        checkIfInCart();
        Gson gson = new Gson();
        String json = gson.toJson(cartList);
        SharedPreferences pref = getSharedPreferences("cart pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString("cartList", json);
        editor.apply();
    }

    public void ButtonAddToWishListPressed(View view) {
        SharedPreferences pref = getSharedPreferences("wishList", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = pref.getString("wishList", null);
        Type type = new TypeToken<ArrayList<Product>>() {
        }.getType();
        List<Product> myProductList = gson.fromJson(json, type);
        product.setIsSaved();
        boolean saved = false;
        if (myProductList != null) {
            for (Product p : myProductList
            ) {
                saved = product.getProductID() == p.getProductID();
            }
            if (!saved) {
                myProductList.add(product);
                Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Product already saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            myProductList = new ArrayList<>();
            myProductList.add(product);
        }
        json = gson.toJson(myProductList);
        editor.putString("wishList", json);
        editor.apply();
    }
}