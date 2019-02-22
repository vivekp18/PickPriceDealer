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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.OffersClaimedAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Modal.OffersClaimed;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_CLAIMED_OFFERS;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

/**
 * Created by Admin on 17-01-2019.
 */

public class OffersClaimedActivity extends AppCompatActivity {


    private Toolbar mToolbar;
    private TextView tvToolbar;
    private Button btnOffers_Claimed;
    private TextView tvOffersClaimedNotAvaialble;

    private AlertDialog alertDialog;
    private ImageView ivClose, ivDialogSetting, ivDialogProfile;
    private Button btnAppExit;

    private RecyclerView rvOffersClaimedList;
    private OffersClaimedAdapter offersClaimedAdapter;

    private ArrayList<OffersClaimed> offersClaimedArrayList;
    private OffersClaimed offersClaimed;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String Offers_Claimed_URL="show_claim_offer.php";

    //offersClaimedLayout

    //private CheckConnectivity checkConnectivity;
    //private boolean connectivityStatus = true;
    private RelativeLayout offersClaimedLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_claimed);

        init();

        setupToolbar();

        if(connectivityStatus){
            retriveAllCustomersClaimedOffers();
        }else {
            toastClass.makeToast(getApplicationContext(),"Please check internet connection");
        }

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Offers Claimed");
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
        mToolbar = findViewById(R.id.toolbar);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbar);
        btnOffers_Claimed = mToolbar.findViewById(R.id.btnOffersClamied);

        tvOffersClaimedNotAvaialble = findViewById(R.id.tv_offersClaimedAvailable);
        rvOffersClaimedList = findViewById(R.id.rvOffersClaimedList);
        offersClaimedArrayList = new ArrayList<>();

        offersClaimedLayout=(RelativeLayout) findViewById(R.id.offersClaimedLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    /*@Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(OffersClaimedActivity.this, new NoInternetListener() {
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
        OffersClaimedActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        *//*UnRegister receiver for connectivity*//*
        this.unregisterReceiver(checkConnectivity);
    }*/

    private void retriveAllCustomersClaimedOffers() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        HashMap<String,String> hashMapParams = sessionManager.getDealerDetails();

        String dealerId = hashMapParams.get(KEY_ID);

        HashMap<String,String> params = new HashMap<>();

        params.put("dealer_id",dealerId);

        mVollyService = new VolleyService(mResultCallback, OffersClaimedActivity.this);

        mVollyService.postDataVolleyParameters(RETREIVE_CLAIMED_OFFERS,
                this.getResources().getString(R.string.base_url) + Offers_Claimed_URL,params);
    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETREIVE_CLAIMED_OFFERS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!offersClaimedArrayList.isEmpty() ){
                                    offersClaimedArrayList.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    tvOffersClaimedNotAvaialble.setVisibility(View.GONE);
                                    rvOffersClaimedList.setVisibility(View.VISIBLE);

                                    if (jsonObject.has("alloffers")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("alloffers");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            offersClaimed = new OffersClaimed();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("product_id")){
                                                offersClaimed.setProductId(jsonObj.getString("product_id"));
                                            }else{
                                                offersClaimed.setProductId("");
                                            }

                                            if(jsonObj.has("product_name")){
                                                offersClaimed.setProductName(jsonObj.getString("product_name"));
                                            }else{
                                                offersClaimed.setProductName("");
                                            }

                                            if(jsonObj.has("customer_name")){
                                                offersClaimed.setCustomerName(jsonObj.getString("customer_name"));
                                            }else{
                                                offersClaimed.setCustomerName("");
                                            }

                                            if(jsonObj.has("customer_id")){
                                                offersClaimed.setCustomerId(jsonObj.getString("customer_id"));
                                            }else{
                                                offersClaimed.setCustomerId("");
                                            }

                                            offersClaimedArrayList.add(offersClaimed);

                                        }

                                        initOfferClaimedCustomersList();
                                    }


                                } else {
                                    progressDialog.cancel();
                                    tvOffersClaimedNotAvaialble.setVisibility(View.VISIBLE);
                                    rvOffersClaimedList.setVisibility(View.GONE);
                                    tvOffersClaimedNotAvaialble.setText("Not Available");
                                    //initOfferClaimedCustomersList();

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


    public void initOfferClaimedCustomersList(){
        offersClaimedAdapter = new OffersClaimedAdapter(this, offersClaimedArrayList);
        rvOffersClaimedList.setHasFixedSize(true);
        rvOffersClaimedList.setNestedScrollingEnabled(false);
        rvOffersClaimedList.setLayoutManager(new GridLayoutManager(this, 1));
        rvOffersClaimedList.setItemAnimator(new DefaultItemAnimator());
        rvOffersClaimedList.setAdapter(offersClaimedAdapter);
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
                .make(offersClaimedLayout, internetStatus, Snackbar.LENGTH_LONG)
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

}
