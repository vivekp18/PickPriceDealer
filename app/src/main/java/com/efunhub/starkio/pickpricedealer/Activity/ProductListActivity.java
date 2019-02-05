package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.ProductListAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
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
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
