package com.example.myapplicationfirst;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextEmail, editTextUser, editTextPassword;
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextUser = findViewById(R.id.editTextUser);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String username = editTextUser.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();


                if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(RegisterActivity.this, "Введите корректный email", Toast.LENGTH_SHORT).show();
                    return;
                }


                registerUser(email, username, password);
            }
        });
    }

    private void registerUser(String email, String username, String password) {
        String url = "http://10.0.2.2/smartphone_shop/register.php";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", email);
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                Toast.makeText(RegisterActivity.this, "Регистрация успешна", Toast.LENGTH_SHORT).show();
                                // Переход на страницу входа
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Ошибка: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Ошибка ответа сервера", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "Ошибка сети: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}