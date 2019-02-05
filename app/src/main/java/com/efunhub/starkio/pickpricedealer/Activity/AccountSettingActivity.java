package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.CHANGE_PASSWORD;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class AccountSettingActivity extends AppCompatActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private EditText edt_oldPassword;
    private EditText edt_newPassword;

    private Button btn_ChangePassword;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ChangePasswordURL="change_password_dealer.php ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);

        init();

        setupToolbar();

        btn_ChangePassword.setOnClickListener(this);
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Reset Password");
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

        edt_oldPassword = findViewById(R.id.edtOldPassword);
        edt_newPassword = findViewById(R.id.edtNewPassword);

        btn_ChangePassword = findViewById(R.id.btnChangePassword);

        sessionManager = new SessionManager(getApplicationContext());

        toastClass = new ToastClass();
    }


    private boolean checkValid() {
        //validations
        if (edt_oldPassword.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(getApplicationContext(), "Please enter old password");
            return false;
        } else if (edt_newPassword.getText().toString().equalsIgnoreCase("")) {
            toastClass.makeToast(getApplicationContext(), "Please enter new password");
            return false;
        }else if(!TextUtils.isEmpty(edt_newPassword.getText().toString())||
                !edt_newPassword.getText().toString().equalsIgnoreCase("")){
            Pattern pattern;
            Matcher matcher;

            final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";

            pattern = Pattern.compile(PASSWORD_PATTERN);//^(?=.{8,})(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])$
            matcher = pattern.matcher(edt_newPassword.getText().toString());

            if(!matcher.matches()){
                toastClass.makeToast(getApplicationContext(), "Password must be at least 8 characters " +
                        "and must contain at least one lower case letter " +
                        "one upper case letter and one digit");

            }

            return matcher.matches();
        }
        return true;
    }


    private void updateDealerPasswords() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, AccountSettingActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("oldp",edt_oldPassword.getText().toString());
        params.put("newp",edt_newPassword.getText().toString());

        mVollyService.postDataVolleyParameters(CHANGE_PASSWORD,
                this.getResources().getString(R.string.base_url) + ChangePasswordURL,params);
    }



    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int status = jsonObject.getInt("status");

                        if (status == 0){
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), "sNot updated");
                        } else if (status == 1) {
                            progressDialog.cancel();

                            toastClass.makeToast(getApplicationContext(), "Successfully Updated");

                            //start login activity after updating password just to validate new password
                            sessionManager.logout();



                        } else if(status == 2) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), "Old password not match.");
                        }
                        else if(status == 3) {
                            progressDialog.cancel();
                            toastClass.makeToast(getApplicationContext(), " Password must be at least 8 characters and must contain at\n" +
                                    "least one lower case letter, one upper case letter and one digit.");
                        }

                    } catch (JSONException e) {
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

            Log.d("LoginActivity", "initVolleyCallback: " +ex);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(AccountSettingActivity.this, new NoInternetListener() {
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
        AccountSettingActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnChangePassword :

                if(connectivityStatus){
                    if(checkValid()){
                        progressDialog = ProgressDialog.show(AccountSettingActivity.this, "Pick Price",
                                "Please wait while updating password",
                                false, false);
                        updateDealerPasswords();
                    }
                }
            break;
        }
    }
}
