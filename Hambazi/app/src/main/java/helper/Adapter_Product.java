package helper;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ir.hatamiarash.hambazidemo.R;

public class Adapter_Product extends RecyclerView.Adapter<Adapter_Product.ProductViewHolder> {
    private List<Product>products;

    public Adapter_Product(List<Product> products){
        this.products = products;
        Log.d("Adapter", "Size : " + this.products.size());
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        CardView product;
        TextView product_id;
        TextView product_name;
        TextView product_price;
        TextView product_price_off;
        ImageView product_photo;

        ProductViewHolder(View itemView) {
            super(itemView);
            product = (CardView) itemView.findViewById(R.id.product);
            product_id = (TextView) itemView.findViewById(R.id.product_id);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_price = (TextView) itemView.findViewById(R.id.product_price);
            product_price_off = (TextView) itemView.findViewById(R.id.product_price_off);
            product_photo = (ImageView) itemView.findViewById(R.id.product_photo);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_product, viewGroup, false);
        return new ProductViewHolder(v);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void onBindViewHolder(ProductViewHolder productViewHolder, int i) {
        productViewHolder.product_id.setText(products.get(i).uid);
        productViewHolder.product_name.setText(products.get(i).name);
        productViewHolder.product_price.setText(products.get(i).price);
        productViewHolder.product_price_off.setText(products.get(i).price_off);
        //productViewHolder.product_photo.setImageResource(products.get(i).image);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}