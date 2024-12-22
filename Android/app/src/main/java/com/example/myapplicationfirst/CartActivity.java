package com.example.myapplicationfirst;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartProducts;
    private TextView totalPriceView;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cartRecyclerView = findViewById(R.id.cart_recycler_view);
        totalPriceView = findViewById(R.id.cart_total_price);

        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartProducts = new ArrayList<>();


        userId = getSharedPreferences("UserPrefs", MODE_PRIVATE).getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show();
            finish(); // Закрываем активность, если userId не найден
            return;
        }


        cartAdapter = new CartAdapter(cartProducts, this, userId);
        cartRecyclerView.setAdapter(cartAdapter);


        loadCartItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_catalog) {
            finish();
            return true;
        } else if (id == R.id.action_account) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCartItems() {
        String url = "http://10.0.2.2/smartphone_shop/get_cart.php?user_id=" + userId;
        Log.d("CartActivity", "Loading cart items from: " + url); // Логируем URL запроса

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("CartActivity", "Response: " + response.toString()); // Логируем полный ответ

                        try {

                            cartProducts.clear();
                            double totalPrice = 0;


                            if (response.length() == 0) {
                                Toast.makeText(CartActivity.this, "Корзина пуста", Toast.LENGTH_SHORT).show();
                            }


                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = response.getJSONObject(i);


                                if (!item.has("product_id") || !item.has("name") || !item.has("price") || !item.has("image_url")) {
                                    Log.e("CartActivity", "Не все данные присутствуют в ответе JSON.");
                                    continue; // Пропускаем этот элемент, если данных недостаточно
                                }


                                int id = item.getInt("product_id");
                                String name = item.getString("name");
                                double price = item.getDouble("price");
                                String imageUrl = item.getString("image_url"); // Используем правильное имя поля

                                Log.d("CartActivity", "Item: " + name + ", Price: " + price + ", Image URL: " + imageUrl); // Логируем товар


                                cartProducts.add(new Product(id, name, price, imageUrl));
                                totalPrice += price;
                            }


                            totalPriceView.setText(CatalogActivity.formatPrice(totalPrice));
                            cartAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e("CartActivity", "Ошибка обработки данных: " + e.getMessage());
                            Toast.makeText(CartActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CartActivity", "Ошибка загрузки: " + error.getMessage());
                        Toast.makeText(CartActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    public void onProductRemoved() {
        loadCartItems();
    }
}
