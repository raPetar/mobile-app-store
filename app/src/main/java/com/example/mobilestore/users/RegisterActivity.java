package com.example.mobilestore.users;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobilestore.R;
import com.example.mobilestore.start.StartActivity;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText userName;
    private EditText password;
    private EditText confirmPassword;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText email;
    private Users user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences pref = getSharedPreferences("savedThemeID", MODE_PRIVATE);
        int themeID = pref.getInt("savedThemeID", 0);
        setTheme(themeID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        userName = findViewById(R.id.editTextUserNameRegister);
        password = findViewById(R.id.editTextRegisterPassword);
        confirmPassword = findViewById(R.id.editTextRegisterConfirmPassword);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerUser() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String connection = getString(R.string.connectionString);
        String url = connection + "users/" + "register";
        HashMap<String, String> params = new HashMap<>();
        params.put("username", userName.getText().toString().trim());
        params.put("password", password.getText().toString().trim());
        params.put("firstName", firstName.getText().toString().trim());
        params.put("lastName", lastName.getText().toString().trim());
        params.put("phoneNumber", phoneNumber.getText().toString().trim());
        params.put("email", email.getText().toString().trim());

        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userName = response.getString("userName");
                            String firstName = response.getString("firstName");
                            String lastName = response.getString("lastName");
                            String email = response.getString("email");
                            String phoneNumber = response.getString("phoneNumber");
                            user = new Users(userName, null, firstName, lastName, email, phoneNumber);
                            saveUserData(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RegisterActivity.this, "An error has occurred, please try again", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(objectRequest);
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

    public void NextButtonClicked(View view) {
        if (userName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a username", Toast.LENGTH_LONG).show();
        } else if (password.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_LONG).show();
        } else if (!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show();
        } else {
            @SuppressLint("InflateParams") final View v = getLayoutInflater().inflate(R.layout.activity_register_additional_information, null);
            MaterialButton nextButton = v.findViewById(R.id.ButtonSendRegisterInfo);
            firstName = v.findViewById(R.id.editTextRegisterFirsName);
            lastName = v.findViewById(R.id.editTextRegisterLastName);
            phoneNumber = v.findViewById(R.id.editTextRegisterPhoneNumber);
            email = v.findViewById(R.id.editTextRegisterEmail);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    registerUser();
                    Intent intent1 = new Intent(getApplicationContext(), StartActivity.class);
                    startActivity(intent1);
                    finish();
                }
            });
            setContentView(v);
        }
    }
}