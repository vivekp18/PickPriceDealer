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
import com.efunhub.starkio.pickpricedealer.Modal.Offers;
import com.efunhub.starkio.pickpricedealer.R;

import java.util.ArrayList;

/**
 * Created by Admin on 20-12-2018.
 */

public class OffersListAdapter extends RecyclerView.Adapter<OffersListAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<Offers> offersArrayList = new ArrayList<>();
    private String productId;

    private Offers offersModel;

    View dialogView;

    AlertDialog alertDeleteOffers;

    Button btnDeleteOffers;

    ImageView ivClose;

    public OffersListAdapter(Context mContext, ArrayList<Offers> offersArrayList) {
        this.mContext = mContext;
        this.offersArrayList = offersArrayList;

    }

    @Override
    public OffersListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_offers_list_item, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        offersModel = offersArrayList.get(position);

        holder.tvOffers.setText(offersModel.getOffer());

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //v.getId() will give you the image id

                AlertDialog.Builder alertDeleteOffersDialog = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);

                alertDeleteOffersDialog.setView(dialogView);

                btnDeleteOffers = dialogView.findViewById(R.id.btnDeleteSpecification);

                ivClose = dialogView.findViewById(R.id.icCloseDialog);

                btnDeleteOffers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDeleteOffers.dismiss();

                        Offers offers = offersArrayList.get(position);

                        ProductDetailsActivity.productDetailsActivity.deleteOffers(offers.getOffers_id());
                    }
                });


                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDeleteOffers.dismiss();
                    }
                });

                alertDeleteOffers = alertDeleteOffersDialog.create();
                alertDeleteOffers.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDeleteOffers.setCanceledOnTouchOutside(false);
                alertDeleteOffers.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return offersArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvOffers;
        ImageView ivDelete;

        ItemViewHolder(View itemView) {
            super(itemView);

            tvOffers = itemView.findViewById(R.id.tv_offers);

            ivDelete = itemView.findViewById(R.id.iv_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get position
                    int pos = getAdapterPosition();

                    // check if item still exists
                    /*if(pos != RecyclerView.NO_POSITION)
                    {
                        Offers clickedDataItem = offersArrayList.get(pos);
                        Intent intent = new Intent(mContext, ProductListActivity.class);
                        intent.putExtra("category_id",clickedDataItem.offers_id);
                        mContext.startActivity(intent);

                    }*/

                }
            });
        }
    }
}