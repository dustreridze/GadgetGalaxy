package com.example.myapplicationfirst;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<Product> cartProducts;
    private Context context;
    private int userId;

    public CartAdapter(List<Product> cartProducts, Context context, int userId) {
        this.cartProducts = cartProducts;
        this.context = context;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = cartProducts.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText(CatalogActivity.formatPrice(product.getPrice()));

        Glide.with(context)
                .load(product.getImage_url())
                .into(holder.productImage);

        holder.removeButton.setOnClickListener(v -> removeFromCart(product.getId(), position));
    }

    @Override
    public int getItemCount() {
        return cartProducts.size();
    }

    private void removeFromCart(int productId, int position) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2/smartphone_shop/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<ApiResponse> call = apiService.removeFromCart(userId, productId);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cartProducts.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();


                    ((CartActivity) context).onProductRemoved();
                } else {
                    Toast.makeText(context, "Ошибка удаления из корзины", Toast.LENGTH_SHORT).show();
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
        Button removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            removeButton = itemView.findViewById(R.id.remove_button);
        }
    }

    public interface ApiService {
        @FormUrlEncoded
        @POST("remove_from_cart.php")
        Call<ApiResponse> removeFromCart(@Field("user_id") int userId, @Field("product_id") int productId);
    }

    public static class ApiResponse {
        private String status;
        private String message;

        public String getMessage() {
            return message;
        }
    }
}
