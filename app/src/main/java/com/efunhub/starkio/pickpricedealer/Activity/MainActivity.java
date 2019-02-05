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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.CategoriesListAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
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
            }
        });

        ivDialogProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                alertDialog.dismiss();
            }
        });

        ivDialogSetting.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AccountSettingActivity.class));
                alertDialog.dismiss();
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

        //Check connectivity
        checkConnectivity = new CheckConnectivity(MainActivity.this, new NoInternetListener() {
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
        MainActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
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
