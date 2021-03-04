package com.example.mobilestore.users;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.LoginActivity;
import com.example.mobilestore.R;
import com.example.mobilestore.list.WishListActivity;
import com.example.mobilestore.orders.OrdersActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;

public class UserProfile extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText emailAddress;
    private Users user;
    private Users saveUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        firstName = findViewById(R.id.editTextProfileFirstName);
        lastName = findViewById(R.id.editTextProfileLastName);
        phoneNumber = findViewById(R.id.editTextProfilePhoneNumber);
        emailAddress = findViewById(R.id.editTextProfileEmailAddress);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        loadUser();
    }

    private void loadUser() {
        SharedPreferences pref = getSharedPreferences("user", MODE_PRIVATE);
        String json = pref.getString("user data", null);

        Gson gson = new Gson();
        Type type = new TypeToken<Users>() {
        }.getType();
        user = gson.fromJson(json, type);
        if (user != null) {
            setTitle(user.UserName + "'s" + " profile");
            firstName.setText(user.FirstName);
            lastName.setText(user.LastName);
            emailAddress.setText(user.email);
            phoneNumber.setText(user.PhoneNumber);
        } else {
            setContentView(R.layout.no_profile);
            Button registerUser = findViewById(R.id.buttonMakeProfile);
            registerUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void ApplyButtonClicked(View view) {
        if (phoneNumber.getText().length() > 15) {
            Toast.makeText(this, "Please note that maximum phone number length is 15 numbers", Toast.LENGTH_SHORT).show();
        } else {
            String connection = getString(R.string.connectionString);
            String url = connection + "users/" + "update";
            HashMap<String, String> params = new HashMap<>();
            if (!emailAddress.getText().toString().trim().isEmpty() && !phoneNumber.getText().toString().trim().isEmpty()) {
                params.put("username", user.UserName);
                params.put("email", emailAddress.getText().toString().trim());
                params.put("phoneNumber", phoneNumber.getText().toString().trim());
            }
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String userName = null;
                            try {
                                userName = response.getString("userName");
                                String firstName = response.getString("firstName");
                                String lastName = response.getString("lastName");
                                String email = response.getString("email");
                                String phoneNumber = response.getString("phoneNumber");
                                saveUser = new Users(userName, null, firstName, lastName, email, phoneNumber);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            saveUserData(saveUser);
                        }

                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(UserProfile.this, "An error has occurred, pleasy try again later!", Toast.LENGTH_SHORT).show();
                        }
                    });
            requestQueue.add(request);
        }
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

    public void OrdersPressed(View view) {
        Intent intent = new Intent(this, OrdersActivity.class);
        startActivity(intent);
    }

    public void WishListPressed(View view) {
        Intent intent = new Intent(this, WishListActivity.class);
        startActivity(intent);
    }
}