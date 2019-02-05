package com.efunhub.starkio.pickpricedealer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Activity.AddProductActivity;
import com.efunhub.starkio.pickpricedealer.Activity.ProductDetailsActivity;
import com.efunhub.starkio.pickpricedealer.Modal.Product;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.PicassoTrustAll;

import java.util.ArrayList;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Product> arrayList;

    public ProductListAdapter(Context mContext, ArrayList<Product> arrayList) {
        this.mContext = mContext;
        this.arrayList = arrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item_list, null);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        Product productModel = arrayList.get(position);

        holder.tvProductName.setText(productModel.getProductName());

        holder.tvProductPrice.setText(mContext.getResources().getString(R.string.currency)+" " + productModel.getProductPrice());

        if (!productModel.getProductImage().equalsIgnoreCase("")
                || !productModel.getProductImage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(productModel.getProductImage())
                        .placeholder(R.drawable.ic_electronics_placeholder)
                        .into(holder.ivProductImage);


            }catch(Exception ex){

                Log.e("ProductListAdapter", "onBindViewHolder: ",ex );
            }


        } else {
            holder.ivProductImage.setImageResource(R.drawable.ic_electronics_placeholder);
        }

        holder.tvViewProductDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Product product = arrayList.get(position);

                Intent intent = new Intent(mContext, ProductDetailsActivity.class);
                intent.putExtra("ProductId",product.getProductId());
                intent.putExtra("ProductImage",product.getProductImage());
                mContext.startActivity(intent);
            }
        });

        holder.ivEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Product product = arrayList.get(position);
                Intent intent = new Intent(mContext, AddProductActivity.class);
                intent.putExtra("EditProductId",2);
                intent.putExtra("ProductName",product.getProductName());
                intent.putExtra("ProductStatus",product.getStatus());
                intent.putExtra("ProductId",product.getProductId());
                intent.putExtra("ProductBrand",product.getProductBrand());
                intent.putExtra("ProductColor",product.getProductColor());
                intent.putExtra("ProductPrice",product.getProductPrice());
                intent.putExtra("ProductImage",product.getProductImage());
                intent.putExtra("ProductDescription",product.getProductDescription());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivEditProduct;
        private ImageView ivProductImage;
        private TextView tvViewProductDetails;
        private TextView tvProductName;
        private TextView tvProductPrice;

        ItemViewHolder(View itemView) {
            super(itemView);

            tvViewProductDetails = itemView.findViewById(R.id.tv_ViewProductDetails);

            ivEditProduct = itemView.findViewById(R.id.ivEditProduct);

            ivProductImage = itemView.findViewById(R.id.ivProductImage);

            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);

            tvProductName = itemView.findViewById(R.id.tvProductName);

        }
    }
}
