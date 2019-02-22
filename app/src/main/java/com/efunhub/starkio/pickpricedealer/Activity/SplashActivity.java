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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DEALER_STATUS;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class SplashActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String DealerStatusURL="status_dealer.php";
    private ProgressBar progressBar;

    //private CheckConnectivity checkConnectivity;
   // private boolean connectivityStatus = true;
    private RelativeLayout splashLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();

        //sessionManager.checkLogin();
        if(connectivityStatus){
            if(sessionManager.checkDealerLoginStatus()){
                checkDealerStatus();
            }else{

                progressBar.setVisibility(View.GONE);
                Thread thread = new Thread() {
                    public void run() {
                        try {
                            sleep(1500);

                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

            }
        }

    }

    private void init() {

        progressBar = findViewById(R.id.splash_progressbar);
        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());

        splashLayout=(RelativeLayout) findViewById(R.id.splashLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);


  }

    private void checkDealerStatus() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, SplashActivity.this);

        HashMap<String, String> dealerProfile = sessionManager.getDealerDetails();
        String dealerId = dealerProfile.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id", dealerId);

        mVollyService.postDataVolleyParameters(DEALER_STATUS,
                this.getResources().getString(R.string.base_url) + DealerStatusURL, params);
    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int status = jsonObject.getInt("status");

                        if (status == 0) {
                            progressBar.setVisibility(View.GONE);
                            toastClass.makeToast(getApplicationContext(), "Your account not activated yet.");
                            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else  {
                            progressBar.setVisibility(View.GONE);
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }

                @Override
                public void notifyError(int requestId, VolleyError error) {
                    Log.v("Volley requestid ", String.valueOf(requestId));
                    Log.v("Volley Error", String.valueOf(error));
                }
            };
        }catch (Exception ex){

            Log.d("SplashScreen", "initVolleyCallback: " +ex);
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
                .make(splashLayout, internetStatus, Snackbar.LENGTH_LONG)
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
                checkDealerStatus();

            }
        }
    }

   /* @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(SplashActivity.this, new NoInternetListener() {
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
        SplashActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        *//*UnRegister receiver for connectivity*//*
        this.unregisterReceiver(checkConnectivity);
    }*/


}
