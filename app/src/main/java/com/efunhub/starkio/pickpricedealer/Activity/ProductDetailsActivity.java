package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.HighLightsAdapter;
import com.efunhub.starkio.pickpricedealer.Adapter.OffersListAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
import com.efunhub.starkio.pickpricedealer.Modal.Highlights;
import com.efunhub.starkio.pickpricedealer.Modal.Offers;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.PicassoTrustAll;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.ADD_PRODUCT_HIGHLIGHTS;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.ADD_PRODUCT_OFFER;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DELETE_PRODUCT_HIGHLIGHTS;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DELETE_PRODUCT_OFFERS;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_PRODUCT_OFFERS_HIGHLIGHTS;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private ImageView ivArrowNext;
    private ImageView ivProductImage;
    private ArrayList<String> optionSetArrayList;
    private String productid;
    private String productImage;
    private String productOffer;
    private String productHighlights;


    private ArrayList<Offers> offersArrayList;
    private ArrayList<Highlights> highlightsArrayList;
    private ArrayList<String> offersArrayListData;
    private ArrayList<String> highLightsArrayListData;

    private Offers offersModel;
    private Highlights highlightsModel;

    private LinearLayout offersLinearlayout;
    private LinearLayout highLightssLinearlayout;

    private RecyclerView rv_OffersView;
    private RecyclerView rv_HighLightsView;
    private OffersListAdapter offersListAdapter;
    private HighLightsAdapter highLightsAdapter;

    private TextView tv_NoOffers;
    private TextView tv_NoHighLights;


    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ADD_OFFER_URL="add_product_offer.php";
    private String ADD_HighLights_URL="add_product_highlight.php";
    private String Retreive_Offers_HighLights_URL="show_dealer_ohs.php";
    private String Delete_HighLights_URL="delete_product_highlight.php";
    private String Delete_Offers__URL="delete_product_offer.php";


    public static ProductDetailsActivity  productDetailsActivity;

    private AlertDialog alertDialog;
    private ImageView ivClose;

    private TextView tvAddOffers,tvAddHighlights;
    private EditText edtAddOffers,edtAddHighLights;
    private Button btnAddOffer,btnAddHighLights;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        init();

        setupToolbar();

        if(connectivityStatus){
            retreiveProductOfferAndHighlights();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }
        ivArrowNext.setOnClickListener(this);
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
                showAddProductDetailsOptionDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void showAddProductDetailsOptionDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_offers_highlight_option_dialog, null);
        dialogBuilder.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tvAddOffers = dialogView.findViewById(R.id.tvAddOffers);

        tvAddOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                addOfferDialog();
                }
        });

        tvAddHighlights = dialogView.findViewById(R.id.tvAddHighlights);

        tvAddHighlights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                addHighLightsDialog();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void addOfferDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_offer_dialog, null);
        dialogBuilder.setView(dialogView);

        edtAddOffers = dialogView.findViewById(R.id.edtAddOffer);

        btnAddOffer = dialogView.findViewById(R.id.btnAddOffer);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnAddOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productOffer = edtAddOffers.getText().toString();

                if(checkOffersValidation()){


                    addOffer();

                    alertDialog.cancel();
                }


            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    public boolean checkOffersValidation(){

        if(productOffer.equalsIgnoreCase("")|| TextUtils.isEmpty(productOffer)||productOffer.isEmpty()||productOffer==null){

            edtAddOffers.setError("Please enter offers.");
            return false;
        }

        return true;
    }



    public void addHighLightsDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductDetailsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_highlight_dialog, null);
        dialogBuilder.setView(dialogView);

        edtAddHighLights = dialogView.findViewById(R.id.edtAddHighLights);

        btnAddHighLights = dialogView.findViewById(R.id.btnAddHighLights);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnAddHighLights.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                productHighlights = edtAddHighLights.getText().toString();

                if(checkHighLightsValidation()){


                    addHighLights();

                    alertDialog.cancel();
                }


            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();


    }

    public boolean checkHighLightsValidation(){

        if(productHighlights.equalsIgnoreCase("")|| TextUtils.isEmpty(productHighlights)||productHighlights.isEmpty()||productHighlights==null){

            edtAddHighLights.setError("Please enter highlights.");
            return false;
        }

        return true;
    }

    private void retreiveProductOfferAndHighlights(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productid);

        mVollyService.postDataVolleyParameters(RETREIVE_PRODUCT_OFFERS_HIGHLIGHTS,
                this.getResources().getString(R.string.base_url) + Retreive_Offers_HighLights_URL,params);

    }

    private void addOffer(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productid);
        params.put("offer",productOffer);

        mVollyService.postDataVolleyParameters(ADD_PRODUCT_OFFER,
                this.getResources().getString(R.string.base_url) + ADD_OFFER_URL,params);

    }

    private void addHighLights(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productid);
        params.put("highlight",productHighlights);

        mVollyService.postDataVolleyParameters(ADD_PRODUCT_HIGHLIGHTS,
                this.getResources().getString(R.string.base_url) + ADD_HighLights_URL,params);

    }

    public void deleteHighLights(String productHighlightsId){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productid);
        params.put("highlight_id",productHighlightsId);

        mVollyService.postDataVolleyParameters(DELETE_PRODUCT_HIGHLIGHTS,
                this.getResources().getString(R.string.base_url) + Delete_HighLights_URL,params);

    }

    public void deleteOffers(String productOffersId){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductDetailsActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productid);
        params.put("offer_id",productOffersId);

        mVollyService.postDataVolleyParameters(DELETE_PRODUCT_OFFERS,
                this.getResources().getString(R.string.base_url) + Delete_Offers__URL,params);

    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case ADD_PRODUCT_OFFER:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),"Added Successfully.");

                                    retreiveProductOfferAndHighlights();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Something went wrong, Please try again");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case ADD_PRODUCT_HIGHLIGHTS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),"Added Successfully.");

                                    retreiveProductOfferAndHighlights();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Something went wrong, Please try again");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;//

                        case RETREIVE_PRODUCT_OFFERS_HIGHLIGHTS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if(jsonObject.has("alloffers")){

                                        if(!offersArrayList.isEmpty()){
                                            offersArrayList.clear();
                                        }

                                        JSONArray jsonArrayOffersdata = jsonObject.getJSONArray("alloffers");

                                        for (int i=0;i<jsonArrayOffersdata.length();i++){

                                            offersModel = new Offers();

                                            JSONObject jsonObjectOffer = jsonArrayOffersdata.getJSONObject(i);

                                            if(jsonObjectOffer.has("offer_id")){

                                                offersModel.setOffers_id(jsonObjectOffer.getString("offer_id"));

                                            }

                                            if(jsonObjectOffer.has("offer_name")){

                                                offersModel.setOffer(jsonObjectOffer.getString("offer_name"));
                                                offersArrayListData.add(jsonObjectOffer.getString("offer_name"));
                                            }

                                            offersArrayList.add(offersModel);
                                        }



                                    }

                                    if(jsonObject.has("allhighlights")){

                                        if(!highlightsArrayList.isEmpty()){
                                            highlightsArrayList.clear();
                                        }

                                        JSONArray jsonArrayHighlightsdata = jsonObject.getJSONArray("allhighlights");

                                        for (int i=0;i<jsonArrayHighlightsdata.length();i++){

                                            highlightsModel = new Highlights();

                                            JSONObject jsonObjectHighLights = jsonArrayHighlightsdata.getJSONObject(i);

                                            if(jsonObjectHighLights.has("highlight_id")){

                                                highlightsModel.setHighlight_id(jsonObjectHighLights.getString("highlight_id"));

                                            }

                                            if(jsonObjectHighLights.has("highlight_name")){

                                                highlightsModel.setHighlights(jsonObjectHighLights.getString("highlight_name"));
                                                highLightsArrayListData.add(jsonObjectHighLights.getString("highlight_name"));
                                            }
                                            highlightsArrayList.add(highlightsModel);
                                        }


                                    }

                                    initOffersAndHighlightsLayout();

                                } else {
                                    progressDialog.cancel();
                                    initOffersAndHighlightsLayout();
                                    //toastClass.makeToast(getApplicationContext(),"Offers and Highlights are not available. ");

                                }




                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case DELETE_PRODUCT_HIGHLIGHTS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),"Deleted Successfully.");

                                    retreiveProductOfferAndHighlights();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Something went wrong, Please try again");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case DELETE_PRODUCT_OFFERS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getApplicationContext(),"Deleted Successfully.");

                                    retreiveProductOfferAndHighlights();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Something went wrong, Please try again");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;//
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

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Product Details");
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

    private void init() {

        productDetailsActivity = this;

        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        ivArrowNext = findViewById(R.id.iv_arrow_next);

        ivProductImage = findViewById(R.id.ivProductDeatilsImage);

        offersLinearlayout = findViewById(R.id.offers_parent_layout);

        highLightssLinearlayout = findViewById(R.id.highlights_parent_layout);

        rv_OffersView = findViewById(R.id.rvOffers);
        rv_HighLightsView = findViewById(R.id.rvHighLights);

        tv_NoHighLights = findViewById(R.id.tv_HighLightsNotAvailable);
        tv_NoOffers = findViewById(R.id.tv_offersNotAvailable);

        if(getIntent().hasExtra("ProductId")){
            productid = getIntent().getStringExtra("ProductId");
        }else{
            productid="";
        }

        if(getIntent().hasExtra("ProductImage")){
            productImage = getIntent().getStringExtra("ProductImage");
        }else{
            productImage="";
        }

        if (!productImage.equalsIgnoreCase("")
                || !productImage.isEmpty()) {

            try{

                PicassoTrustAll.getInstance(this)
                        .load(productImage)
                        .placeholder(R.drawable.ic_electronics_placeholder)
                        .into(ivProductImage);


            }catch(Exception ex){

                Log.e("ProductDetailsActivity", "onBindViewHolder: ",ex );
            }


        } else {
            ivProductImage.setImageResource(R.drawable.ic_electronics_placeholder);
        }

        optionSetArrayList = new ArrayList<>();

        offersArrayList = new ArrayList<>();
        highlightsArrayList = new ArrayList<>();

        offersArrayListData = new ArrayList<>();
        highLightsArrayListData = new ArrayList<>();

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    public void initOffersAndHighlightsLayout(){


        if(!offersArrayList.isEmpty()){
            rv_OffersView.setVisibility(View.VISIBLE);
            tv_NoOffers.setVisibility(View.GONE);
            offersListAdapter = new OffersListAdapter(this, offersArrayList);
            rv_OffersView.setHasFixedSize(true);
            rv_OffersView.setNestedScrollingEnabled(false);
            rv_OffersView.setLayoutManager(new GridLayoutManager(this, 1));
            rv_OffersView.setItemAnimator(new DefaultItemAnimator());
            rv_OffersView.setAdapter(offersListAdapter);
        }else{
            rv_OffersView.setVisibility(View.GONE);
            tv_NoOffers.setVisibility(View.VISIBLE);
        }

        if(!highlightsArrayList.isEmpty()){
            rv_HighLightsView.setVisibility(View.VISIBLE);
            tv_NoHighLights.setVisibility(View.GONE);
            highLightsAdapter = new HighLightsAdapter(this, highlightsArrayList);
            rv_HighLightsView.setHasFixedSize(true);
            rv_HighLightsView.setNestedScrollingEnabled(false);
            rv_HighLightsView.setLayoutManager(new GridLayoutManager(this, 1));
            rv_HighLightsView.setItemAnimator(new DefaultItemAnimator());
            rv_HighLightsView.setAdapter(highLightsAdapter);
        }else {
            rv_HighLightsView.setVisibility(View.GONE);
            tv_NoHighLights.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.iv_arrow_next:

                Intent intent = new Intent(getApplicationContext(),ProductSpecificationActivity.class);
                intent.putExtra("ProductId",productid);
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        productDetailsActivity = this;

        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductDetailsActivity.this, new NoInternetListener() {
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
        ProductDetailsActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
