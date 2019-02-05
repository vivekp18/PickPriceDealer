package com.efunhub.starkio.pickpricedealer.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Modal.OffersClaimed;
import com.efunhub.starkio.pickpricedealer.R;

import java.util.ArrayList;

/**
 * Created by Admin on 17-01-2019.
 */

public class OffersClaimedAdapter extends RecyclerView.Adapter<OffersClaimedAdapter.ItemViewHolder> {

    private Context mContext;
    private ArrayList<OffersClaimed> offersClaimedArrayList = new ArrayList<>();

    private OffersClaimed offersClaimed;

    public OffersClaimedAdapter(Context mContext, ArrayList<OffersClaimed> offersClaimedArrayList) {
        this.mContext = mContext;
        this.offersClaimedArrayList = offersClaimedArrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_list_layout, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        offersClaimed = offersClaimedArrayList.get(position);

        holder.tvCustomerName.setText(offersClaimed.getCustomerName());

        holder.tvProductName.setText(offersClaimed.getProductName());

    }

    @Override
    public int getItemCount() {
        return offersClaimedArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductName;
        TextView tvCustomerName;


        ItemViewHolder(View itemView) {
            super(itemView);

            tvProductName = itemView.findViewById(R.id.tvProductName);

            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);

           /* itemView.setOnClickListener(new View.OnClickListener() {
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
            });*/
        }
    }
}
