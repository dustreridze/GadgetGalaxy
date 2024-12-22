package com.example.myapplicationfirst;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String url = "http://10.0.2.2/smartphone_shop/get_products.php";


        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("Request", "Ответ от сервера получен: " + response.toString());

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject product = response.getJSONObject(i);


                                int id = product.getInt("id");
                                String name = product.getString("name");
                                String description = product.getString("description");
                                double price = product.getDouble("price");
                                String imageUrl = product.getString("image_url");


                                Log.d("Product", "ID: " + id + ", Name: " + name + ", Price: " + price);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Обработка ошибок
                        error.printStackTrace();
                        Toast.makeText(MainActivity.this, "Ошибка запроса: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );


        requestQueue.add(jsonArrayRequest);
    }
}