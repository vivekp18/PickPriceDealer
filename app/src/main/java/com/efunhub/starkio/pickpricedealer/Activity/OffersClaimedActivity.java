package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.OffersClaimedAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
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

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    @Override
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
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

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

}
