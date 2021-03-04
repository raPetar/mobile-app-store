package com.example.mobilestore.start;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.FrequentlyAskedQuestionsActivity;
import com.example.mobilestore.LoginActivity;
import com.example.mobilestore.SearchActivity;
import com.example.mobilestore.list.WishListActivity;
import com.example.mobilestore.cart.CartActivity;
import com.example.mobilestore.product.OpenProductActivity;
import com.example.mobilestore.product.Product;
import com.example.mobilestore.product.ProductCategory;
import com.example.mobilestore.users.UserProfile;
import com.example.mobilestore.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//Launcher activity, containing the ActionBar as well as TopPicks and BrowseAll sections
public class StartActivity extends AppCompatActivity {
    private ActionBarDrawerToggle drawerToggle;
    public SwitchMaterial aSwitch;
    public Parcelable mListState;
    public String STATE_KEY = "listKey";
    private ProductCategory category;
    private LinearLayoutManager layoutManager;
    private final List<ProductCategory> categoryList = new ArrayList<>();
    private final List<Product> productList = new ArrayList<>();
    private final List<Product> topPickList = new ArrayList<>();
    private final List<Product> browseProductList = new ArrayList<>();
    private final List<String> urlList = new ArrayList<>();
    private boolean hasInternetAccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeChanger());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        NukeSSLCerts.nuke();

        //Checking if there is a viable internet connection to fetch the data
        if (checkIfConnected()) {
            retrieveCategories();
            retrieveTopPick();
            retrieveBrowseAll(0);
            setupNavView();
        } else {

            setTitle(getString(R.string.no_internet_title));
            @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.no_internet_connection, null);
            MaterialButton retryButton = v.findViewById(R.id.buttonRetry);
            retryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recreate();
                }
            });
            setContentView(v);
        }
    }

    private boolean checkIfConnected() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) {
            hasInternetAccess = false;
            return false;
        } else {
            hasInternetAccess = true;
            return true;
        }
    }

    private void retrieveBrowseAll(int getNext) {
        //Responsible for retrieving all Products, calls the "setupBrowseAll" method
        String connection = getString(R.string.connectionString);
        String url = connection + "products/browse/" + getNext;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("productList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int productID = jsonObject.getInt("productID");
                                int categoryID = jsonObject.getInt("categoryID");
                                String mainImage = jsonObject.getString("mainImage");
                                String productName = jsonObject.getString("name");
                                int price = jsonObject.getInt("price");

                                Product product = new Product(productID, categoryID, mainImage, null, productName, null, price, false);
                                browseProductList.add(product);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setupBrowseAll();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(StartActivity.this, "An error has occurred, please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
    }

    private void setupBrowseAll() {
        //Populates the recycler for BrowseAll section of the startup activity
        //Configuration for proper scrolling with the GridLayout
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBrowseAll);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        BrowseAllAdapter adapter = new BrowseAllAdapter(browseProductList);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        adapter.setOnClickListener(new BrowseAllAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int forwardProductID;
                forwardProductID = browseProductList.get(position).getProductID();
                Intent intent = new Intent(getApplicationContext(), OpenProductActivity.class);
                intent.putExtra("MainActivityExtraData", forwardProductID);
                onPause();
                startActivity(intent);
            }
        });
        NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                            scrollY > oldScrollY) {
                        retrieveBrowseAll(browseProductList.get(browseProductList.size() - 1).ProductID);

                    }
                }
            }
        });
    }

    private void retrieveTopPick() {
        //Retrieves the TopPicks from the API and populating the urlList as well as the topPickList
        RequestQueue queue = Volley.newRequestQueue(this);
        String connection = getString(R.string.connectionString);
        String url = connection + "products";
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("productList");
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
                                JSONArray array = jsonObject.getJSONArray("productImages");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject object = array.getJSONObject(j);
                                    String url = object.getString("url");
                                    urlList.add(url);
                                }
                                Product product = new Product(productID, categoryID, mainImage, urlList, productName, productDescription, price, false);
                                topPickList.add(product);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setupTopPick();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                setTitle(getString(R.string.error));
                @SuppressLint("InflateParams") View v = getLayoutInflater().inflate(R.layout.server_error, null);
                MaterialButton retryButton = v.findViewById(R.id.buttonRetry);
                retryButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        recreate();
                    }
                });
                setContentView(v);
                Toast.makeText(StartActivity.this, "An Error occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    private void setupTopPick() {
        //Responsible for setting up the TopPick viewPager2 as well as loading the images and handling
        //the slide animations
        ViewPager2 viewPager2 = findViewById(R.id.viewPager2TopPicks);
        TopPickAdapter adapter = new TopPickAdapter(topPickList);
        viewPager2.setAdapter(adapter);

        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(80));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.75f + r * 0.25f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);

        adapter.setOnClickListener(new TopPickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                int forwardProductID;
                forwardProductID = topPickList.get(position).getProductID();
                Intent intent = new Intent(getApplicationContext(), OpenProductActivity.class);
                intent.putExtra("MainActivityExtraData", forwardProductID);
                startActivity(intent);
            }
        });
    }

    private void retrieveCategories() {
        //Retrieves the current categories and sets their respective icons
        //Which are hardcoded inside the Drawable folder
        RequestQueue queue = Volley.newRequestQueue(this);
        String connection = getString(R.string.connectionString);
        String url = connection + "category";
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("categoryList");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int CategoryID = jsonObject.getInt("categoryID");
                                String CategoryName = jsonObject.getString("name");
                                ProductCategory category = null;
                                if (CategoryID == 1) {
                                    category = new ProductCategory(CategoryID, R.drawable.technology, CategoryName);
                                } else if (CategoryID == 2) {
                                    category = new ProductCategory(CategoryID, R.drawable.furniture, CategoryName);
                                } else if (CategoryID == 3) {
                                    category = new ProductCategory(CategoryID, R.drawable.clothing, CategoryName);
                                } else if (CategoryID == 4) {
                                    category = new ProductCategory(CategoryID, R.drawable.other, CategoryName);
                                }
                                categoryList.add(category);
                            }
                            setupCategories();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StartActivity.this, "An Error occurred, please try again later", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(objectRequest);
    }

    private void setupNavView() {
        if (hasInternetAccess) {
            //Initializing drawer and the variable Toggle to check if it is opened or not
            DrawerLayout drawerLayout = findViewById(R.id.DrawerLayout);
            drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
            drawerToggle.setDrawerIndicatorEnabled(true);
            //Adds a listener to check when it is opened and when it is not
            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            final NavigationView navigationView = findViewById(R.id.nav_View);
            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //Listens to what item is clicked from the drawer and runs the specific method
                    int id = item.getItemId();
                    if (id == R.id.Home) {
                        refreshSavedList();
                        setupCategories();
                    } else if (id == R.id.navProfile) {
                        //Opens Profile information
                        Intent intent = new Intent(getApplicationContext(), UserProfile.class);
                        startActivity(intent);
                        onPause();
                    } else if (id == R.id.navMyList) {
                        Intent intent = new Intent(getApplicationContext(), WishListActivity.class);
                        startActivity(intent);
                    } else if (id == R.id.navLogin) {
                        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        if (pref.getBoolean("isUserLoggedIn", false)) {
                            editor.clear();
                            editor.apply();
                            recreate();
                        } else {
                            Intent intent = new Intent(navigationView.getContext(), LoginActivity.class);
                            startActivity(intent);
                            onPause();
                        }
                    } else if (id == R.id.navFAQ) {
                        Intent intent = new Intent(navigationView.getContext(), FrequentlyAskedQuestionsActivity.class);
                        startActivity(intent);
                        onPause();
                    } else if (id == R.id.SwitchThemes) {
                        aSwitch = findViewById(R.id.switchThemesButton);

                        SharedPreferences saveCheck = getSharedPreferences("saveCheck", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor saveEditor = saveCheck.edit();
                        if (aSwitch.isChecked()) {
                            aSwitch.setChecked(false);
                            saveEditor.putBoolean("saved", false);
                            saveEditor.apply();
                            SharedPreferences pref = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("whiteThemeID", R.style.WhiteTheme);
                            editor.apply();
                        } else {
                            aSwitch.setChecked(true);
                            saveEditor.putBoolean("saved", true);
                            saveEditor.apply();
                            SharedPreferences pref = getSharedPreferences("myPrefs", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("darkThemeID", R.style.DarkTheme);
                            editor.apply();
                        }
                        recreate();
                    }
                    return true;
                }
            });
        }
    }

    private void setupCategories() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewCategoriesStart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        CategoryAdapter adapter = new CategoryAdapter(categoryList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                category = categoryList.get(position);
                int categoryID = category.getCategoryID();
                retrieveProductByCategory(categoryID);
            }
        });
    }

    private void retrieveProductByCategory(int categoryID) {
        //Retrieves all the Products by the category they belong to
        RequestQueue queue = Volley.newRequestQueue(this);
        String connection = getString(R.string.connectionString);
        String url = connection + "products/category/" + categoryID;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("productList");
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
                                JSONArray array = jsonObject.getJSONArray("productImages");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject object = array.getJSONObject(j);
                                    String url = object.getString("url");
                                    urlList.add(url);
                                }
                                Product product = new Product(productID, categoryID, mainImage, urlList, productName, productDescription, price, false);
                                productList.add(product);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showSearchResult();
                        productList.clear();
                        Log.e("Category Response", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(objectRequest);
    }

    private void refreshSavedList() {
        SharedPreferences pref = getSharedPreferences("save productList", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
        recreate();
    }

    @Override
    public Resources.Theme getTheme() {
        Resources.Theme theme = super.getTheme();
        theme.applyStyle(getThemeChanger(), true);
        return theme;
    }

    private int getThemeChanger() {
        //Loads  the custom theme that user had selected
        int themeID = pickThemeID();
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("savedThemeID", themeID);
        editor.apply();
        return themeID;
    }

    private int pickThemeID() {
        //Sets the custom theme, depending on which one the user has selected
        SharedPreferences pref = getSharedPreferences("saveCheck", MODE_PRIVATE);
        SharedPreferences pref2 = getSharedPreferences("myPrefs", MODE_PRIVATE);
        if (pref.getBoolean("saved", false)) {
            return pref2.getInt("darkThemeID", R.style.DarkTheme);
        } else {
            return pref2.getInt("whiteThemeID", R.style.WhiteTheme);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflates the actionBar menu
        //Responsible for the search bar as well
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        NavigationView nv = findViewById(R.id.nav_View);
        Menu newMenu = nv.getMenu();
        MenuItem loginItem = newMenu.findItem(R.id.navLogin);
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);

        if (pref.getBoolean("isUserLoggedIn", false)) {
            loginItem.setTitle("Logout");
        } else {
            loginItem.setTitle("Login");
        }
        MenuItem searchItem = menu.findItem(R.id.search_bar);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by name");
        searchItem.setIcon(R.drawable.ic_key);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                productList.clear();
                getProductByName(s);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        MenuItem cartItem = menu.findItem(R.id.shoppingCart);
        cartItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(getApplicationContext(), CartActivity.class);
                startActivity(intent);
                return false;
            }
        });
        return true;
    }

    private void getProductByName(String s) {
        //Retrieve items that the user searched for and starts the SearchActivity by calling the "ShowSearchResult" method
        RequestQueue queue = Volley.newRequestQueue(this);
        String searchFor = s.toLowerCase().trim();
        String connection = getString(R.string.connectionString);
        String url = connection + "products/search/" + searchFor;
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("productList");
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
                                JSONArray array = jsonObject.getJSONArray("productImages");
                                for (int j = 0; j < array.length(); j++) {
                                    JSONObject object = array.getJSONObject(j);
                                    String url = object.getString("url");
                                    urlList.add(url);
                                }
                                Product product = new Product(productID, categoryID, mainImage, urlList, productName, productDescription, price, false);
                                productList.add(product);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        showSearchResult();
                        Log.e("Get Product By Name", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(StartActivity.this, "No products found" , Toast.LENGTH_LONG).show();
            }
        });
        queue.add(objectRequest);
    }

    private void showSearchResult() {
        Gson gson = new Gson();
        String json = gson.toJson(productList);
        Intent intent = new Intent(StartActivity.this, SearchActivity.class);
        intent.putExtra("searchProductList", json);
        startActivity(intent);
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

    private static class NukeSSLCerts {
        protected static final String TAG = "NukeSSLCerts";

        public static void nuke() {
            try {
                TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }

                            @Override
                            public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            }
                        }
                };
                SSLContext sc = SSLContext.getInstance("SSL");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String arg0, SSLSession arg1) {
                        return true;
                    }
                });
            } catch (Exception e) {
            }
        }
    }

}