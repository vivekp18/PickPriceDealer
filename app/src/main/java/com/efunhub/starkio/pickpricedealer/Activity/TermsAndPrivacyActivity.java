package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_PRIVACY;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_TERMS;

/**
 * Created by Admin on 03-01-2019.
 */

public class TermsAndPrivacyActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView tvToolbar;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVE_Terms="terms.php";
    private String RETRIVE_Privacy="privacy.php";

    private String termsDescription=null;
    private String privaciesDescription=null;
    private TextView tvPrivacyContent,tv_Content;
    private int toolBarTextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy);

        init();

        setupToolbar();

        if(connectivityStatus){

                retreiveTermsCondition();

        }else{
            toastClass.makeToast(getApplicationContext(),"Please check Internet Connection");
        }

    }


    private void init() {
        mToolbar = findViewById(R.id.toolbar_main);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbarMain);
        tvPrivacyContent = findViewById(R.id.tvPrivacyContent);
        tv_Content = findViewById(R.id.tvTermsContent);

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Terms And Privacy Policies");

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
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(TermsAndPrivacyActivity.this, new NoInternetListener() {
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
        TermsAndPrivacyActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    private void retreiveTermsCondition(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, TermsAndPrivacyActivity.this);

        mVollyService.postDataVolley(RETREIVE_TERMS,
                this.getResources().getString(R.string.base_url) + RETRIVE_Terms);
    }

    private void retreivePrivacyPolicies(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, TermsAndPrivacyActivity.this);

        mVollyService.postDataVolley(RETREIVE_PRIVACY,
                this.getResources().getString(R.string.base_url) + RETRIVE_Privacy);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETREIVE_TERMS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if (jsonObject.has("termsdetails")) {

                                        JSONObject jsonObjectData = jsonObject.getJSONObject("termsdetails");

                                        termsDescription = jsonObjectData.getString("description");

                                        initView();


                                    }

                                } else {
                                    progressDialog.cancel();
                                    //tvNotAvaialble.setVisibility(View.VISIBLE);
                                    initView();

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case RETREIVE_PRIVACY:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if (jsonObject.has("privacydetails")) {

                                        JSONObject jsonObjectData = jsonObject.getJSONObject("privacydetails");

                                        privaciesDescription = jsonObjectData.getString("description");

                                        initPrivacyView();

                                    }

                                } else {
                                    progressDialog.cancel();
                                    //tvNotAvaialble.setVisibility(View.VISIBLE);
                                    initPrivacyView();

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

            Log.d("TermsAndPrivacyActivity", "initVolleyCallback: " +ex);
        }

    }


    public void initView(){


        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }

        if(!termsDescription.isEmpty()|| termsDescription!=null){
            Spanned htmlAsSpanned = Html.fromHtml(termsDescription);

            tv_Content.setText(htmlAsSpanned);
        }else{

            tv_Content.setText("Terms and Conditions Not Available");
        }

        retreivePrivacyPolicies();

    }


    public void initPrivacyView(){

        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }

        if(!privaciesDescription.isEmpty()|| privaciesDescription!=null){
            Spanned htmlAsSpanned = Html.fromHtml(privaciesDescription);

            tvPrivacyContent.setText(htmlAsSpanned);
        }else{

            tvPrivacyContent.setText("Privacy Policies Not Available");
        }

    }
}
