package com.example.myapplicationfirst;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;
    private int userId;

    public CatalogAdapter(List<Product> productList, Context context, int userId) {
        this.productList = productList;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(CatalogActivity.formatPrice(product.getPrice()));

        Glide.with(context)
                .load(product.getImage_url())
                .into(holder.productImage);


        holder.addToCartButton.setOnClickListener(v -> addToCart(product.getId()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("image_url", product.getImage_url());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void addToCart(int productId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/smartphone_shop/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ApiResponse> call = apiService.addToCart(userId, productId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ошибка добавления в корзину", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productPrice;
        Button addToCartButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCartButton = itemView.findViewById(R.id.add_to_cart_button);
        }
    }


    public interface ApiService {
        @FormUrlEncoded
        @POST("add_to_cart.php")
        Call<ApiResponse> addToCart(@Field("user_id") int userId, @Field("product_id") int productId);
    }


    public static class ApiResponse {
        private String status;
        private String message;

        public String getMessage() {
            return message;
        }
    }
}