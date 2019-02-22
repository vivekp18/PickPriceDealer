package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.ProductListAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Modal.Product;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETRIVE_PRODUCTS;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class ProductListActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView tvToolbar;

    private RecyclerView rvProductList;
    private ProductListAdapter productListAdapter;

    private ArrayList<String> arrayList;
    private ArrayList<Product> productsArrayList;
    private Product productModel;

    private String categoryId;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVE_PRODUCTS_URL="show_dealer_products.php";
    private TextView tvProductNotAvailable;

    //productListLayout
    //private CheckConnectivity checkConnectivity;
    //private boolean connectivityStatus = true;
    private RelativeLayout productListLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        init();

        setupToolbar();

        if(getIntent().hasExtra("category_id")){
            categoryId = getIntent().getStringExtra("category_id");
        }

        if(connectivityStatus){
            retriveAllProduct();
        }else {
            toastClass.makeToast(getApplicationContext(),"Please check internet connectivity.");
        }

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.product_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_add:
                Intent intent = new Intent(ProductListActivity.this,AddProductActivity.class);
                intent.putExtra("category_id",categoryId);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void init() {

        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        rvProductList = findViewById(R.id.rvProductList);
        arrayList = new ArrayList<>();

        productsArrayList = new ArrayList<>();

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());

        productListLayout=(RelativeLayout) findViewById(R.id.productListLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);

        tvProductNotAvailable = findViewById(R.id.tv_productsNotAvailable);
    }

    private void retriveAllProduct() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        HashMap<String,String> dealerInfo = sessionManager.getDealerDetails();

        String dealerId = dealerInfo.get(KEY_ID);

        HashMap<String,String> params = new HashMap<>();
        params.put("dealer_id",dealerId);
        params.put("category_id",categoryId);

        mVollyService = new VolleyService(mResultCallback, ProductListActivity.this);

        mVollyService.postDataVolleyParameters(RETRIVE_PRODUCTS,
                this.getResources().getString(R.string.base_url) + RETRIVE_PRODUCTS_URL,params);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETRIVE_PRODUCTS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!productsArrayList.isEmpty() ){
                                    productsArrayList.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();


                                    if (jsonObject.has("allproducts")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("allproducts");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            productModel = new Product();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("product_id")){
                                                productModel.setProductId(jsonObj.getString("product_id"));
                                            }else{
                                                productModel.setProductId("");
                                            }

                                            if(jsonObj.has("product_name")){
                                                productModel.setProductName(jsonObj.getString("product_name"));
                                            }else{
                                                productModel.setProductName("");
                                            }

                                            if(jsonObj.has("product_price")){
                                                productModel.setProductPrice(jsonObj.getString("product_price"));
                                            }else{
                                                productModel.setProductPrice("");
                                            }

                                            if(jsonObj.has("product_description")){
                                                productModel.setProductDescription(jsonObj.getString("product_description"));
                                            }else{
                                                productModel.setProductDescription("");
                                            }

                                            if(jsonObj.has("brand_id")){
                                                productModel.setProdductBrandId(jsonObj.getString("brand_id"));
                                            }else{
                                                productModel.setProdductBrandId("");
                                            }

                                            if(jsonObj.has("brand_name")){
                                                productModel.setProductBrand(jsonObj.getString("brand_name"));
                                            }else{
                                                productModel.setProductBrand("");
                                            }

                                            if(jsonObj.has("color")){
                                                productModel.setProductColor(jsonObj.getString("color"));
                                            }else{
                                                productModel.setProductColor("");
                                            }

                                            if(jsonObj.has("productimgae")){
                                                productModel.setProductImage(jsonObj.getString("productimgae"));
                                            }else{
                                                productModel.setProductImage("");
                                            }

                                            if(jsonObj.has("status")){
                                                productModel.setStatus(jsonObj.getString("status"));
                                            }else{
                                                productModel.setStatus("");
                                            }

                                            productsArrayList.add(productModel);

                                        }

                                        initProductList();
                                    }


                                } else {
                                    progressDialog.cancel();

                                    initProductList();

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;
                    }

                }

                @Override
                public void notifyError(int requestId, VolleyError error) {
                    Log.v("Volley requestid ", String.valueOf(requestId));
                    Log.v("Volley Error", String.valueOf(error));
                }
            };
        }catch (Exception ex){

            Log.d("CategoryListActivity", "initVolleyCallback: " +ex);
        }

    }

    public void initProductList(){

        if(!productsArrayList.isEmpty()){
            tvProductNotAvailable.setVisibility(View.GONE);
            rvProductList.setVisibility(View.VISIBLE);
            productListAdapter = new ProductListAdapter(this, productsArrayList);
            rvProductList.setHasFixedSize(true);
            rvProductList.setNestedScrollingEnabled(false);
            rvProductList.setLayoutManager(new GridLayoutManager(this, 1));
            rvProductList.setItemAnimator(new DefaultItemAnimator());
            rvProductList.setAdapter(productListAdapter);

        }else{
            tvProductNotAvailable.setVisibility(View.VISIBLE);
            rvProductList.setVisibility(View.GONE);
        }



    }


    @Override
    public void onResume() {
        super.onResume();
        registerInternetCheckReceiver();

    }

    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(broadcastReceiver);

    }

    //to check internet connectivity
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = getConnectivityStatusString(context);
            setSnackbarMessage(status,false);
        }
    };


    /**
     *  Method to register runtime broadcast receiver to show snackbar alert for internet connection..
     */
    private void registerInternetCheckReceiver() {
        IntentFilter internetFilter = new IntentFilter();
        internetFilter.addAction("android.net.wifi.STATE_CHANGE");
        internetFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(broadcastReceiver, internetFilter);
    }

    public static String getConnectivityStatusString(Context context) {

        int conn = getConnectivityStatus(context);

        String status = null;
        if (conn == TYPE_WIFI) {
            status = "Wifi enabled";

        } else if (conn == TYPE_MOBILE) {
            status = "Mobile data enabled";
        }

        else if (conn == TYPE_NOT_CONNECTED) {
            status = "Not connected to Internet";
        }
        return status;
    }

    public static int getConnectivityStatus(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            if(activeNetwork.getType() == TYPE_WIFI)
                return TYPE_WIFI;
            if(activeNetwork.getType() == TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }
    private void setSnackbarMessage(String status,boolean showBar) {

        String internetStatus="";

        if(status.equalsIgnoreCase("Wifi enabled")){
            internetStatus="Internet Connected";
        }
        if(status.equalsIgnoreCase("Mobile data enabled")){
            internetStatus="Internet Connected";
        }
        if(status.equalsIgnoreCase("Not connected to Internet")){
            internetStatus="Please check internet connection";
        }
        snackbar = Snackbar
                .make(productListLayout, internetStatus, Snackbar.LENGTH_LONG)
                .setAction("X", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        // Changing action button text color
        View sbView = snackbar.getView();

        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        if(internetStatus.equalsIgnoreCase("Please check internet connection")){
            if(connectivityStatus){
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorGreen));
                snackbar.show();
                connectivityStatus=false;
                noInternetConn.setVisibility(View.VISIBLE);
            }
        }else{
            if(!connectivityStatus){
                sbView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorRed));
                connectivityStatus=true;
                snackbar.show();
                noInternetConn.setVisibility(View.GONE);

            }
        }
    }

    /*@Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductListActivity.this, new NoInternetListener() {
            @Override
            public void availConnection(boolean connection) {
                if (connection) {
                    connectivityStatus = true;
                } else {
                    connectivityStatus = false;
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        ProductListActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        *//*UnRegister receiver for connectivity*//*
        this.unregisterReceiver(checkConnectivity);
    }*/
}
