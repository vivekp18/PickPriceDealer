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
import android.widget.ListView;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Activity.ProductSpecificationActivity;
import com.efunhub.starkio.pickpricedealer.Modal.ProductSubSpecification;
import com.efunhub.starkio.pickpricedealer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 21-12-2018.
 */

public class SubspecificationAdapter  extends RecyclerView.Adapter<SubspecificationAdapter.ItemViewHolder> {

    private Context mContext;

    private ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList = new ArrayList<>();

    private HashMap<String,List<HashMap<String,String>>> productSubSpecificationArrayList = new HashMap<>();

    HashMap<String,String> subSpecification = new HashMap<>();

    Object[] subSpecificationkeys;

    View dialogView;

    AlertDialog alertDeleteSpecification;

    Button btnDeleteSpecification;

    ImageView ivClose;

    public SubspecificationAdapter(Context mContext, HashMap<String,String> subSpecification,
                                   ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList)
    {
        this.mContext = mContext;
        this.subSpecification = subSpecification;
        this.productSubSpecificationDataArrayList =productSubSpecificationDataArrayList;
        subSpecificationkeys = subSpecification.keySet().toArray();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subspecification_item_list, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder,final int position) {

        String productSpecificationKey = String.valueOf(subSpecificationkeys[position]);

        String productSpecificationValue = subSpecification.get(productSpecificationKey);

        holder.tv_ProductSubSpecificationKey.setText(productSpecificationKey);

        holder.tv_ProductSubSpecificationValue.setText(productSpecificationValue);



        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertMainSpecificationDialog = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                dialogView = inflater.inflate(R.layout.delete_alert_dialog, null);

                alertMainSpecificationDialog.setView(dialogView);

                btnDeleteSpecification = dialogView.findViewById(R.id.btnDeleteSpecification);

                ivClose = dialogView.findViewById(R.id.icCloseDialog);

                btnDeleteSpecification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        alertDeleteSpecification.dismiss();
                        ProductSpecificationActivity.productSpecificationActivity.deleteSubSpecification(holder.tv_ProductSubSpecificationKey.getText().toString());

                    }
                });


                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDeleteSpecification.dismiss();
                    }
                });

                alertDeleteSpecification = alertMainSpecificationDialog.create();
                alertDeleteSpecification.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDeleteSpecification.setCanceledOnTouchOutside(false);
                alertDeleteSpecification.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return subSpecification.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_ProductMainSpecification;
        TextView tv_ProductSubSpecificationValue;
        TextView tv_ProductSubSpecificationKey;
        ImageView ivDelete;
        ListView listView;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_ProductMainSpecification = itemView.findViewById(R.id.tv_MainSpecificationName);

            tv_ProductSubSpecificationValue = itemView.findViewById(R.id.tv_SubSpecificationValue);

            tv_ProductSubSpecificationKey = itemView.findViewById(R.id.tv_SubSpecificationKey);

            ivDelete = itemView.findViewById(R.id.iv_delete);


        }
    }


}
