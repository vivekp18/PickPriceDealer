package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Pattern;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DEALER_LOGIN;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.FORGOT_PASSWORD;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private Button btn_Login;
    private EditText edtLoginPassword, edtLoginContact;
    private TextView tvForgotPassword;
    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String LoginURL="login_dealer.php";
    private String ForgotPssWordURL="forgot_password_dealer.php";

    private AlertDialog alertDialog;
    private ImageView ivClose;
    private Button btnSend;
    private EditText edt_ForgotPassEmailId;

    //loginLayout

    //private CheckConnectivity checkConnectivity;
    //private boolean connectivityStatus = true;
    private RelativeLayout loginLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        setupToolbar();

        btn_Login.setOnClickListener(this);

        tvForgotPassword.setOnClickListener(this);
    }


    private boolean checkValid() {
        //validations
        if (edtLoginContact.getText().toString().equalsIgnoreCase("")) {
            edtLoginContact.setError("Please enter contact number");
            return false;
        } else if (edtLoginPassword.getText().toString().equalsIgnoreCase("")) {
            edtLoginPassword.setError("Please enter password");
            return false;
        }else if(!TextUtils.isEmpty(edtLoginContact.getText().toString())){

            String phoneRegex = "^[6-9][0-9]{9}$";

            Pattern pattern = Pattern.compile(phoneRegex);

            if (!pattern.matcher(edtLoginContact.getText().toString()).matches()) {
                edtLoginContact.setError("Please enter valid phone number");
                return pattern.matcher(edtLoginContact.getText().toString()).matches();
            }else{
                if(!TextUtils.isEmpty(edtLoginPassword.getText().toString())){
                    Pattern patternPass;
                    final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";//^[a-zA-Z0-9]{8,}$
                    patternPass = Pattern.compile(passwordRegex);

                    if (!patternPass.matcher(edtLoginPassword.getText().toString()).matches()) {
                        edtLoginPassword.setError(" Password must be at least 8 characters and must contain at least one lower case letter," +
                                "one upper case letter and one digit");
                        return patternPass.matcher(edtLoginPassword.getText().toString()).matches();
                    }
                }
            }
        }
        return true;
    }

    public boolean checkEmailValdiation(){

        if (edt_ForgotPassEmailId.getText().toString().equalsIgnoreCase("")||
                TextUtils.isEmpty(edt_ForgotPassEmailId.getText().toString())) {
            edt_ForgotPassEmailId.setError("Please enter email id.");
            return false;
        }else if (!TextUtils.isEmpty(edt_ForgotPassEmailId.getText().toString()) ||
                !edt_ForgotPassEmailId.getText().toString().equalsIgnoreCase("")) {

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (!pat.matcher(edt_ForgotPassEmailId.getText().toString()).matches()) {
                edt_ForgotPassEmailId.setError("Please enter valid email address");
                return false;
            }
        }
        return  true;
    }

   /* @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(LoginActivity.this, new NoInternetListener() {
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
        LoginActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        *//*UnRegister receiver for connectivity*//*
        this.unregisterReceiver(checkConnectivity);
    }*/
    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Login");
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
        btn_Login = findViewById(R.id.btnLogin);
        edtLoginContact = findViewById(R.id.edtLoginContact);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassWord);

        loginLayout=(RelativeLayout) findViewById(R.id.loginLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);


        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (connectivityStatus) {
                    if (checkValid()) {
                        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
                        progressDialog.setTitle("Pick Price");
                        progressDialog.setMessage("Please wait.");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();

                        dealerLogin();

                    }
                } else {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.tvForgotPassWord:

                    openForgotPasswordDialog();

                break;
        }
    }

    public void openForgotPasswordDialog(){

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.forgot_password_dialog, null);
            dialogBuilder.setView(dialogView);

             ivClose = dialogView.findViewById(R.id.icCloseDialog);


             edt_ForgotPassEmailId = dialogView.findViewById(R.id.edtForgotPassEmailId);

             ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
              });

             btnSend = dialogView.findViewById(R.id.btnSend);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(connectivityStatus){
                    if(checkEmailValdiation()){

                        alertDialog.dismiss();

                        forgotPassword(edt_ForgotPassEmailId.getText().toString());

                    }
                }else{
                    toastClass.makeToast(getApplicationContext(),"Please check internet connection");
                }

            }
        });

             alertDialog = dialogBuilder.create();
             alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
             alertDialog.setCanceledOnTouchOutside(false);
             alertDialog.show();

    }

    private void dealerLogin() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, LoginActivity.this);

        HashMap<String, String> params = new HashMap<>();
        params.put("contact", edtLoginContact.getText().toString());
        params.put("password", edtLoginPassword.getText().toString());

        mVollyService.postDataVolleyParameters(DEALER_LOGIN,
                this.getResources().getString(R.string.base_url) + LoginURL, params);
    }

    private void forgotPassword(String email) {

        initVolleyCallback();

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        mVollyService = new VolleyService(mResultCallback, LoginActivity.this);

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);

        mVollyService.postDataVolleyParameters(FORGOT_PASSWORD,
                this.getResources().getString(R.string.base_url) + ForgotPssWordURL, params);
    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId){

                        case FORGOT_PASSWORD:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");//

                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Something went wrong,Please try again.");

                                }else if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Successfully Updated,Please check email.");

                                } else if (status == 2) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Email is not registered");
                                } else if (status == 3) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Check email format.");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;
                        case DEALER_LOGIN:

                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");//

                                if (status == 0) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Contact Not Exist");

                                }else if (status == 1) {
                                    progressDialog.cancel();
                                    if (jsonObject.has("dealer_id")) {
                                        String dealerId = jsonObject.getString("dealer_id");
                                        sessionManager.createLoginSession(dealerId);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                } else if (status == 2) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Please Check Contact & Password");
                                } else if (status == 3) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Enter Valid Contact");
                                } else if (status == 4) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Password must be at least 8 characters " +
                                            "and must contain at least one lower case letter, " +
                                            "one upper case letter and one digit");
                                }else if (status == 5) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Your account not activated yet.");
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

            Log.d("LoginActivity", "initVolleyCallback: " +ex);
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
                .make(loginLayout, internetStatus, Snackbar.LENGTH_LONG)
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
