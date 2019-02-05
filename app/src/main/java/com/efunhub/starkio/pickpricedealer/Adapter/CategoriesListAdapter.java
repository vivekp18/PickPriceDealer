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

import com.efunhub.starkio.pickpricedealer.Activity.ProductListActivity;
import com.efunhub.starkio.pickpricedealer.Modal.Category;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.PicassoTrustAll;

import java.util.ArrayList;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();

    private Category categoryModel;

    public CategoriesListAdapter(Context mContext, ArrayList<Category> categoryArrayList) {
        this.mContext = mContext;
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_category_list_itme, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        categoryModel = categoryArrayList.get(position);

        holder.tv_categoryName.setText(categoryModel.getCategoryName());

        if (!categoryModel.getCategoryImage().equalsIgnoreCase("")
                || !categoryModel.getCategoryImage().isEmpty()) {

            try{

                PicassoTrustAll.getInstance(mContext)
                        .load(categoryModel.getCategoryImage())
                        .placeholder(R.drawable.ic_electronics)
                        .into(holder.iv_categoryIcon);


            }catch(Exception ex){

                Log.e("CategoryListAdapter", "onBindViewHolder: ",ex );
            }


        } else {
            holder.iv_categoryIcon.setImageResource(R.drawable.ic_electronics);
        }

       /* holder.iv_deleteCategory.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id

                Category category = arrayList.get(position);

                CategoriesListActivity.categoriesListActivityCntx.deleteCategories(category.getCategoryId());


            }
        });*/

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView iv_categoryIcon;
        TextView tv_categoryName;
        ImageView ivMove;

        ItemViewHolder(View itemView) {
            super(itemView);

            iv_categoryIcon = itemView.findViewById(R.id.ivCategory);

            tv_categoryName = itemView.findViewById(R.id.tv_CategoryName);

            ivMove = itemView.findViewById(R.id.iv_Move);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    if(pos != RecyclerView.NO_POSITION)
                    {
                        Category clickedDataItem = categoryArrayList.get(pos);
                        Intent intent = new Intent(mContext, ProductListActivity.class);
                        intent.putExtra("category_id",clickedDataItem.categoryId);
                        mContext.startActivity(intent);

                    }

                }
            });
        }
    }
}
