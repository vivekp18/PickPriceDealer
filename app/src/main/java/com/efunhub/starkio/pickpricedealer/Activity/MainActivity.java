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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.CategoriesListAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Modal.Category;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETRIVE_CATEGORY;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView tvToolbar;
    private Button btnOffers_Claimed;
    private TextView tvCategoryNotAvaialble;

    private AlertDialog alertDialog;
    private ImageView ivClose, ivDialogSetting, ivDialogProfile;
    private Button btnAppExit;

    private RecyclerView rvCategoryList;
    private CategoriesListAdapter categoriesListAdapter;

    private ArrayList<Category> categoryArrayList;
    private Category categoryModel;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String RETRIVE_Categories="show_category.php";

    //private CheckConnectivity checkConnectivity;
    //private boolean connectivityStatus = true;
    private LinearLayout mainLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setupToolbar();

        if(connectivityStatus){
            retriveAllCategories();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check Internet Connection");
        }

        btnOffers_Claimed.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
           /* case R.id.item_add:
                startActivity(new Intent(MainActivity.this, AddProductActivity.class));
                break;*/

            case R.id.item_My_Account:
                addDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.account_dialog, null);
        dialogBuilder.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);
        ivDialogSetting = dialogView.findViewById(R.id.ivDialogSetting);
        ivDialogProfile = dialogView.findViewById(R.id.ivDialogProfile);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });

        ivDialogProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });

        ivDialogSetting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountSettingActivity.class));
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void retriveAllCategories() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, MainActivity.this);

        mVollyService.postDataVolley(RETRIVE_CATEGORY,
                this.getResources().getString(R.string.base_url) + RETRIVE_Categories);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETRIVE_CATEGORY:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!categoryArrayList.isEmpty() ){
                                    categoryArrayList.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();


                                    if (jsonObject.has("category")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("category");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            categoryModel = new Category();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("category_id")){
                                                categoryModel.setCategoryId(jsonObj.getString("category_id"));
                                            }else{
                                                categoryModel.setCategoryId("");
                                            }

                                            if(jsonObj.has("name")){
                                                categoryModel.setCategoryName(jsonObj.getString("name"));
                                            }else{
                                                categoryModel.setCategoryName("");
                                            }

                                            if(jsonObj.has("cimage")){
                                                categoryModel.setCategoryImage(jsonObj.getString("cimage"));
                                            }else{
                                                categoryModel.setCategoryImage("");
                                            }

                                            categoryArrayList.add(categoryModel);

                                        }

                                        initCategoryList();
                                    }


                                } else {
                                    progressDialog.cancel();
                                    tvCategoryNotAvaialble.setVisibility(View.VISIBLE);
                                    if(jsonObject.has("msg")){
                                        tvCategoryNotAvaialble.setText(jsonObject.getString("msg"));
                                    }else{
                                        tvCategoryNotAvaialble.setText(jsonObject.getString("msg"));
                                    }

                                    initCategoryList();

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

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("P I C K P R I C E");
        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

    private void init() {
        mToolbar = findViewById(R.id.toolbar_main);
        tvToolbar = mToolbar.findViewById(R.id.tvToolbarMain);
        btnOffers_Claimed = mToolbar.findViewById(R.id.btnOffersClamied);

        tvCategoryNotAvaialble = findViewById(R.id.tv_catAvailable);
        rvCategoryList = findViewById(R.id.rvCategoryList);
        categoryArrayList = new ArrayList<>();

        mainLayout=(LinearLayout) findViewById(R.id.mainLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);

        toastClass = new ToastClass();
        sessionManager = new SessionManager(getApplicationContext());
    }

    public void initCategoryList(){


        categoriesListAdapter = new CategoriesListAdapter(this, categoryArrayList);
        rvCategoryList.setHasFixedSize(true);
        rvCategoryList.setNestedScrollingEnabled(false);
        rvCategoryList.setLayoutManager(new GridLayoutManager(this, 1));
        rvCategoryList.setItemAnimator(new DefaultItemAnimator());
        rvCategoryList.setAdapter(categoriesListAdapter);
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
                .make(mainLayout, internetStatus, Snackbar.LENGTH_LONG)
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
                retriveAllCategories();
            }
        }
    }


    @Override
    public void onBackPressed() {
        exitDialog();
    }

    private void exitDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.app_exit_dialog, null);
        dialogBuilder.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);

        btnAppExit = dialogView.findViewById(R.id.btnExit);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnAppExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                finishAffinity();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btnOffersClamied:
                Intent intent = new Intent(getApplicationContext(),OffersClaimedActivity.class);
                startActivity(intent);
                break;
        }
    }
}
