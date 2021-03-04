package com.example.mobilestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.start.StartActivity;
import com.example.mobilestore.users.RegisterActivity;
import com.example.mobilestore.users.Users;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    public EditText userName;
    public EditText password;
    public MaterialButton guestButton;
    public Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setUpViews(themeID);
    }

    private void setUpViews(int themeID) {
        userName = findViewById(R.id.editTextUserName);
        password = findViewById(R.id.editTextPasswordLogin);
        guestButton = findViewById(R.id.ButtonGuest);
        if (themeID == R.style.WhiteTheme) {
            guestButton.setTextColor(getResources().getColor(R.color.customBlack));
        } else {
            guestButton.setTextColor(getResources().getColor(R.color.customWhite));
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoginPressed(View view) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String connection = getString(R.string.connectionString);
        String url = connection + "users/" + "login";
        HashMap<String, String> params = new HashMap<>();
        params.put("Username", userName.getText().toString().trim());
        params.put("password", password.getText().toString().trim());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            users = new Users(
                                    response.getString("userName"), response.getString("password"),
                                    response.getString("firstName"), response.getString("lastName"),
                                    response.getString("email"), response.getString("phoneNumber"));
                            if (response.isNull("userName") || response.isNull("password")) {
                                Toast.makeText(LoginActivity.this, "Username or Password incorrect", Toast.LENGTH_SHORT).show();
                            } else {
                                saveUserData(users);
                                successfulLogin();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "An error occurred, pleas try again later!", Toast.LENGTH_SHORT).show();;
            }
        });
        queue.add(objectRequest);
    }

    private void successfulLogin() {
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(intent);
        finish();
    }

    private void saveUserData(Users user) {
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putBoolean("isUserLoggedIn", true);
        editor.putString("user data", json);
        editor.apply();
    }

    public void GuestPressed(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
        finish();
    }

    public void registerPressed(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }
}