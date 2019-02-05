package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

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
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }


}
