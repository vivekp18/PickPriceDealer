package com.efunhub.starkio.pickpricedealer.Adapter;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Modal.ProductMainSpecification;
import com.efunhub.starkio.pickpricedealer.Modal.ProductSubSpecification;
import com.efunhub.starkio.pickpricedealer.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 20-12-2018.
 */

public class ProductSpecificationAdapter  extends RecyclerView.Adapter<ProductSpecificationAdapter.ItemViewHolder> {

    private Context mContext;

    private ArrayList<ProductMainSpecification> productMainSpecificationArrayList = new ArrayList<>();

    private ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList = new ArrayList<>();

    private HashMap<String,List<HashMap<String,String>>> productSubSpecificationArrayList = new HashMap<>();

    private ProductMainSpecification productMainSpecification;

    private ProductSubSpecification productsubSpecification;

    private List<HashMap<String,String>> hashMapList = new ArrayList<>();

    private SubspecificationAdapter  productSpecificationAdapter;



    public ProductSpecificationAdapter(Context mContext, ArrayList<ProductMainSpecification>
            productMainSpecificationArrayList,HashMap<String,List<HashMap<String,String>>> productSubSpecificationArrayList,
                                       ArrayList<ProductSubSpecification> productSubSpecificationDataArrayList) {
        this.mContext = mContext;
        this.productMainSpecificationArrayList = productMainSpecificationArrayList;
        this.productSubSpecificationArrayList = productSubSpecificationArrayList;
        this.productSubSpecificationDataArrayList = productSubSpecificationDataArrayList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_specification_items_list, null);


        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {

        productMainSpecification = productMainSpecificationArrayList.get(position);

        holder.tv_ProductMainSpecification.setText(productMainSpecification.getMain_specification_name());


        ArrayList<HashMap<String,String>> data =new ArrayList<>();

        HashMap<String,String> hashMapSpecData = new HashMap<>();

        for(int i=0;i<productSubSpecificationArrayList.size();i++){

            if(productSubSpecificationArrayList != null){

                Iterator iterator = productSubSpecificationArrayList.entrySet().iterator();

                while (iterator.hasNext()) {

                    Map.Entry pair = (Map.Entry)iterator.next();

                    if(String.valueOf(pair.getKey()).equals(productMainSpecification.getMain_specification_id()))
                    {
                        hashMapList =  productSubSpecificationArrayList.get(productMainSpecification.getMain_specification_id());

                        if(hashMapList != null){
                            for (HashMap<String, String> map : hashMapList)
                                for (Map.Entry<String, String> mapEntry : map.entrySet())
                                {
                                    String key = mapEntry.getKey();
                                    String value = mapEntry.getValue();
                                    hashMapSpecData.put(key,value);

                                }
                            data.add(hashMapSpecData);
                        }
                    }
                }
            }

        }

        if(!data.isEmpty()&&!data.isEmpty()){
            productSpecificationAdapter = new SubspecificationAdapter(mContext, hashMapSpecData,
                    productSubSpecificationDataArrayList);
            holder.recyclerViewSubSpecification.setHasFixedSize(true);
            holder.recyclerViewSubSpecification.setNestedScrollingEnabled(false);
            holder.recyclerViewSubSpecification.setLayoutManager(new GridLayoutManager(mContext, 1));
            holder.recyclerViewSubSpecification.setItemAnimator(new DefaultItemAnimator());
            holder.recyclerViewSubSpecification.setAdapter(productSpecificationAdapter);
        }

    }

    @Override
    public int getItemCount() {
        return productMainSpecificationArrayList.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tv_highLights;
        TextView tv_ProductMainSpecification;
        TextView tv_ProductSubSpecificationValue;
        TextView tv_ProductSubSpecificationKey;
        ImageView ivDelete;
        ListView listView;
        RecyclerView recyclerViewSubSpecification;

        //rvSubSpecification

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_ProductMainSpecification = itemView.findViewById(R.id.tv_MainSpecificationName);

            recyclerViewSubSpecification =itemView.findViewById(R.id.rvSubSpecification);
            //listView = itemView.findViewById(R.id.lvSubSpecification);

            //tv_ProductSubSpecificationValue = itemView.findViewById(R.id.tv_SubSpecificationValue);

            //tv_ProductSubSpecificationKey = itemView.findViewById(R.id.tv_SubSpecificationKey);

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

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }
}
