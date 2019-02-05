package com.efunhub.starkio.pickpricedealer.Adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Activity.ProductDetailsActivity;
import com.efunhub.starkio.pickpricedealer.Modal.Highlights;
import com.efunhub.starkio.pickpricedealer.R;

import java.util.ArrayList;

/**
 * Created by Admin on 20-12-2018.
 */

public class HighLightsAdapter extends RecyclerView.Adapter<HighLightsAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Highlights> highlightsArrayList = new ArrayList<>();
    private String productId;

    private Highlights highlightsModel;

    View dialogView;

    AlertDialog alertDeleteHighLights;

    Button btnDeleteHighLights;

    ImageView ivClose;

    public HighLightsAdapter(Context mContext, ArrayList<Highlights> highlightsArrayList) {
        this.mContext = mContext;
        this.highlightsArrayList = highlightsArrayList;

    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_highlights_item_list, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {

        highlightsModel = highlightsArrayList.get(position);

        holder.tv_highLights.setText(highlightsModel.getHighlights());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id

                AlertDialog.Builder alertDeleteHighLightsDialog = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);

                alertDeleteHighLightsDialog.setView(dialogView);

                btnDeleteHighLights = dialogView.findViewById(R.id.btnDeleteSpecification);

                ivClose = dialogView.findViewById(R.id.icCloseDialog);

                btnDeleteHighLights.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDeleteHighLights.dismiss();

                        Highlights highlights = highlightsArrayList.get(position);

                        ProductDetailsActivity.productDetailsActivity.deleteHighLights(highlights.getHighlight_id());
                    }
                });


                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDeleteHighLights.dismiss();
                    }
                });

                alertDeleteHighLights = alertDeleteHighLightsDialog.create();
                alertDeleteHighLights.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDeleteHighLights.setCanceledOnTouchOutside(false);
                alertDeleteHighLights.show();



            }
        });

    }

    @Override
    public int getItemCount() {
        return highlightsArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_highLights;
        ImageView ivDelete;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_highLights = itemView.findViewById(R.id.tv_highLights);

            ivDelete = itemView.findViewById(R.id.iv_delete);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    *//*if(pos != RecyclerView.NO_POSITION)
                    {
                        Offers clickedDataItem = offersArrayList.get(pos);
                        Intent intent = new Intent(mContext, ProductListActivity.class);
                        intent.putExtra("category_id",clickedDataItem.offers_id);
                        mContext.startActivity(intent);

                    }*//*

                }
            });*/
        }
    }
}