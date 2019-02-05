package com.efunhub.starkio.pickpricedealer.Activity;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.Adapter.ProductSpecificationAdapter;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
import com.efunhub.starkio.pickpricedealer.Modal.ProductMainSpecification;
import com.efunhub.starkio.pickpricedealer.Modal.ProductSubSpecification;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.ADD_PRODUCT_MAIN_SPECIFICATION;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.ADD_PRODUCT_SUB_SPECIFICATION;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DELETE_PRODUCT_SUB_SPECIFICATION;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_PRODUCT_MAIN_SPECIFICATION;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETREIVE_PRODUCT_SPECIFICATION;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class ProductSpecificationActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private TextView tvToolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tv_SpecificationStatus;

    private String productId;
    private LinearLayout parentSpecifictionLayout;

    private ArrayList<ProductMainSpecification>  productMainSpecificationArrayList;
    private ArrayList<ProductSubSpecification>  productSubSpecificationArrayList;

    private ProductMainSpecification productMainSpecificationModel;
    private ProductSubSpecification productSubSpecificationModel;

    private RecyclerView rvProductSpecification;

    private ProductSpecificationAdapter productSpecificationAdapter;

    private HashMap<String,List<HashMap<String,String>>> subSpecificationDataHashMap;

    private ArrayList<String> mainSpecificationDataList;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ADD_OFFER_URL="add_product_offer.php";
    private String ADD_HighLights_URL="add_product_highlight.php";
    private String Retreive_Product_Specification_URL="show_specifications.php";
    private String Retreive_Product_Main_Specification_URL="show_main_specification.php";
    private String Add_Product_Sub_Specification_URL="add_product_specification.php";
    private String Add_Product_Main_Specification_URL="add_product_main_specification.php";
    private String Delete_Product_Sub_Specification_URL="delete_product_specification.php";

    private HashMap<String,String> mainSpecification;
    private ArrayList<String> mainSpecificationNameList;

    private HashMap<String,String> subSpecificationHashMapData;

     private ArrayAdapter<String> mainSpecificationOptionArrayAdapter ;

     private String mainSpecificationId;
     private  String mainSpecificationName;
     private  EditText editTextsubSpecificationKey;
     private  EditText editTextsubSpecificationValue;
     private  AlertDialog alertMainSpecification;
     private  AlertDialog alertAddMainSpecification;
     private  AlertDialog alertSubSpecification;
     private  TextView tv_MainSpecificationName;
     private  EditText editTextMainSpecificationValue;

     public static ProductSpecificationActivity productSpecificationActivity;


    private AlertDialog alertDialog;
    private ImageView ivClose;

    private TextView tvSubSpecification,tvMainSpecification;
    private EditText edtAddOffers,edtAddHighLights;
    private Button btnAddSubSpecification,btnAddMainSpecification;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_specification);

        init();

        setupToolbar();

        if(connectivityStatus){
            retreiveProductMainSpecifications();

        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvToolbar.setText("Product Specification");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {

        productSpecificationActivity = this;

        toolbar = findViewById(R.id.toolbar);

        tvToolbar = toolbar.findViewById(R.id.tvToolbar);

        rvProductSpecification = findViewById(R.id.rvProductSpecification);

        tv_SpecificationStatus = findViewById(R.id.tv_SpecificationStatus);

        subSpecificationDataHashMap = new HashMap<>();

        mainSpecificationDataList = new ArrayList<>();

        mainSpecification = new HashMap<>();

        mainSpecificationNameList = new ArrayList<>();

        subSpecificationHashMapData = new HashMap<>();

        if(getIntent().hasExtra("ProductId")){
            productId = getIntent().getStringExtra("ProductId");
        }

        parentSpecifictionLayout = findViewById(R.id.parent_specification_layout);

        productMainSpecificationArrayList = new ArrayList<>();

        productSubSpecificationArrayList = new ArrayList<>();

        toastClass = new ToastClass();

        sessionManager = new SessionManager(this);

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

                    showSpecificationOptionDilog();


                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void  showSpecificationOptionDilog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductSpecificationActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.specification_option_dialog, null);
        dialogBuilder.setView(dialogView);

        tvSubSpecification = dialogView.findViewById(R.id.tvAddSubSpecification);

        tvSubSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProductMianSpecificationOptionDilog();
                alertMainSpecification.dismiss();
            }
        });

        tvMainSpecification = dialogView.findViewById(R.id.tvAddMainSpecification);

        tvMainSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addProductMainSpecificationDialog();
                alertMainSpecification.dismiss();
            }
        });


        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertMainSpecification.dismiss();
            }
        });



        alertMainSpecification = dialogBuilder.create();
        alertMainSpecification.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertMainSpecification.setCanceledOnTouchOutside(false);
        alertMainSpecification.show();



    }

    private void  showProductMianSpecificationOptionDilog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductSpecificationActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.main_specification_option_dialog, null);
        dialogBuilder.setView(dialogView);

        ListView lstListView = dialogView.findViewById(R.id.lstMainSpecification);

        mainSpecificationOptionArrayAdapter = new ArrayAdapter<String>(ProductSpecificationActivity.this,
                R.layout.main_specification_list_layout, R.id.list_content, mainSpecificationNameList);

        lstListView.setAdapter(mainSpecificationOptionArrayAdapter);


        lstListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int which, long l) {
                mainSpecificationName =mainSpecificationOptionArrayAdapter.getItem(which);
                mainSpecificationId = mainSpecification.get(mainSpecificationName);
                addProductSubSpecificationDialog();
                alertDialog.dismiss();
            }
        });


        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

    public void addProductSubSpecificationDialog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProductSpecificationActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_subspecification_layout, null);
        dialogBuilder.setView(dialogView);

        editTextsubSpecificationKey = (EditText) dialogView.findViewById(R.id.editTextsubSpecificatioKey);

        editTextsubSpecificationValue = (EditText) dialogView.findViewById(R.id.editTextsubSpecificationValue);

        tv_MainSpecificationName = (TextView) dialogView.findViewById(R.id.textViewMainSpecificationName);

        tv_MainSpecificationName.setText(mainSpecificationName);

        btnAddSubSpecification = dialogView.findViewById(R.id.btnAddSubSpecification);

        btnAddSubSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkSubSpecificationValidation()){
                    alertSubSpecification.dismiss();
                    addProductSubSpecification(editTextsubSpecificationKey.getText().toString(),
                            editTextsubSpecificationValue.getText().toString());

                    alertSubSpecification.dismiss();
                }
            }
        });


        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertSubSpecification.dismiss();
            }
        });


        alertSubSpecification = dialogBuilder.create();
        alertSubSpecification.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertSubSpecification.setCanceledOnTouchOutside(false);
        alertSubSpecification.show();

    }

    public void addProductMainSpecificationDialog(){

        AlertDialog.Builder alertMainSpecificationDialog = new AlertDialog.Builder(ProductSpecificationActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_main_specification, null);
        alertMainSpecificationDialog.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);

        editTextMainSpecificationValue = (EditText) dialogView.findViewById(R.id.editTextmainSpecificatioName);

        btnAddMainSpecification = dialogView.findViewById(R.id.btnAddMainSpecification);

        btnAddMainSpecification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkMainSpecificationValidation()){
                    alertAddMainSpecification.dismiss();
                    addProductMainSpecification(editTextMainSpecificationValue.getText().toString());
                }
            }
        });


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertAddMainSpecification.dismiss();
            }
        });

        alertAddMainSpecification = alertMainSpecificationDialog.create();
        alertAddMainSpecification.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertAddMainSpecification.setCanceledOnTouchOutside(false);
        alertAddMainSpecification.show();

    }

    private void retreiveProductMainSpecifications(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);

        mVollyService.postDataVolleyParameters(RETREIVE_PRODUCT_MAIN_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Retreive_Product_Main_Specification_URL,params);

    }

    private void retreiveProductSpecifications(){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);

        mVollyService.postDataVolleyParameters(RETREIVE_PRODUCT_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Retreive_Product_Specification_URL,params);

    }

    private void addProductSubSpecification(String productSubSpecificationKey,String productSubSpecificati0nValue){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);
        params.put("main_specification_id",mainSpecificationId);
        params.put("product_specification_key",productSubSpecificationKey);
        params.put("product_specification_value",productSubSpecificati0nValue);

        mVollyService.postDataVolleyParameters(ADD_PRODUCT_SUB_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Add_Product_Sub_Specification_URL,params);

    }

    private void addProductMainSpecification(String productMainSpecificationValue){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);
        params.put("name",productMainSpecificationValue);

        mVollyService.postDataVolleyParameters(ADD_PRODUCT_MAIN_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Add_Product_Main_Specification_URL,params);

    }


    public void deleteSubSpecification(String sunSpecificationKey){

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProductSpecificationActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        String subSpecificationId = subSpecificationHashMapData.get(sunSpecificationKey);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);
        params.put("product_specification_id",subSpecificationId);

        mVollyService.postDataVolleyParameters(DELETE_PRODUCT_SUB_SPECIFICATION,
                this.getResources().getString(R.string.base_url) + Delete_Product_Sub_Specification_URL,params);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {
                    switch (requestId) {

                        case RETREIVE_PRODUCT_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if(jsonObject.has("allproductspecification")){

                                        if(!productMainSpecificationArrayList.isEmpty()){
                                            productMainSpecificationArrayList.clear();
                                        }
                                        JSONArray jsonArrayProductSpecificationdata = jsonObject.getJSONArray("allproductspecification");

                                        for (int i=0;i<jsonArrayProductSpecificationdata.length();i++){

                                             productMainSpecificationModel = new ProductMainSpecification();

                                             JSONObject jsonObjectMainSpecification= jsonArrayProductSpecificationdata.getJSONObject(i);

                                            if(jsonObjectMainSpecification.has("main_specification_id")){

                                                productMainSpecificationModel.setMain_specification_id(jsonObjectMainSpecification.getString("main_specification_id"));

                                            }

                                            if(jsonObjectMainSpecification.has("name")){

                                                productMainSpecificationModel.setMain_specification_name(jsonObjectMainSpecification.getString("name"));
                                            }

                                            if(jsonObjectMainSpecification.has("pspecification")){

                                                JSONArray jsonArraySubSpecicfication = jsonObjectMainSpecification.getJSONArray("pspecification");

                                                HashMap<String,String> hashMap;

                                                HashMap<String,HashMap<String,String>> hashMapData;

                                                List<HashMap<String,String>> hashMapsList = new ArrayList<>() ;

                                                for(int j=0;j<jsonArraySubSpecicfication.length();j++){

                                                    productSubSpecificationModel = new ProductSubSpecification();

                                                    hashMap= new HashMap<>();

                                                    hashMapData= new HashMap<>();

                                                    JSONObject jsonObjectSubSpecificationData = jsonArraySubSpecicfication.getJSONObject(j);

                                                    if(jsonObjectSubSpecificationData.has("product_specification_id")){

                                                        productSubSpecificationModel.setSubSpecificationId(jsonObjectSubSpecificationData.getString(
                                                                "product_specification_id"));
                                                    }

                                                    if(jsonObjectSubSpecificationData.has("product_specification_key")
                                                            &&jsonObjectSubSpecificationData.has("product_specification_value")){

                                                        String key = jsonObjectSubSpecificationData.getString("product_specification_key");

                                                        String value = jsonObjectSubSpecificationData.getString("product_specification_value");

                                                        if(!hashMap.containsKey(jsonObjectSubSpecificationData.getString("product_specification_key"))){

                                                            hashMap.put(key,value);
                                                        }

                                                        hashMapData.put(productMainSpecificationModel.getMain_specification_id(),hashMap);

                                                        hashMapsList.add(hashMap);

                                                        subSpecificationDataHashMap.put(productMainSpecificationModel.getMain_specification_id(),hashMapsList);

                                                        productSubSpecificationModel.setMainSpecificationId(productMainSpecificationModel.getMain_specification_id());

                                                        productSubSpecificationModel.setProductSubSpecificationDataFinal(hashMapData);

                                                        subSpecificationHashMapData.put(key,productSubSpecificationModel.getSubSpecificationId());

                                                    }
                                                    productSubSpecificationArrayList.add(productSubSpecificationModel);

                                                }
                                            }
                                            productMainSpecificationArrayList.add(productMainSpecificationModel);

                                        }
                                    }
                                    productSubSpecificationArrayList.size();

                                    initProductSpecificationLayout();

                                } else {
                                    progressDialog.cancel();
                                    initProductSpecificationLayout();
                                    /*tv_SpecificationStatus.setVisibility(View.VISIBLE);
                                    rvProductSpecification.setVisibility(View.GONE);
                                    tv_SpecificationStatus.setText("Product Specification not available");*/
                                    //toastClass.makeToast(getApplicationContext(),"Product Specifications not available.");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case RETREIVE_PRODUCT_MAIN_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();

                                    if(!mainSpecificationNameList.isEmpty()){
                                        mainSpecificationNameList.clear();
                                    }

                                    if(!mainSpecification.isEmpty()){
                                        mainSpecification.clear();
                                    }

                                    if(jsonObject.has("allmainspecification")) {

                                        if (!productMainSpecificationArrayList.isEmpty()) {
                                            productMainSpecificationArrayList.clear();
                                        }
                                        JSONArray jsonArrayProductSpecificationdata = jsonObject.getJSONArray("allmainspecification");

                                        for (int i = 0; i < jsonArrayProductSpecificationdata.length(); i++) {

                                            productMainSpecificationModel = new ProductMainSpecification();

                                            JSONObject jsonObjectMainSpecification = jsonArrayProductSpecificationdata.getJSONObject(i);

                                            mainSpecification.put(jsonObjectMainSpecification.getString("name"),
                                                    jsonObjectMainSpecification.getString("main_specification_id"));
                                            mainSpecificationNameList.add(jsonObjectMainSpecification.getString("name"));

                                        }
                                    }

                                    retreiveProductSpecifications();

                                } else {
                                    progressDialog.cancel();
                                    retreiveProductSpecifications();
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;


                        case ADD_PRODUCT_SUB_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Added Successfully.");

                                    retreiveProductSpecifications();

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Please try again.");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case ADD_PRODUCT_MAIN_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Added Successfully.");
                                    retreiveProductSpecifications();
                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Please try again.");

                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case DELETE_PRODUCT_SUB_SPECIFICATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Deleted Successfully.");
                                    retreiveProductSpecifications();
                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),"Please try again.");

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

            Log.d("SpecificationActivity", "initVolleyCallback: " +ex);
        }

    }


    private boolean checkSubSpecificationValidation(){

        if(TextUtils.isEmpty(editTextsubSpecificationKey.getText().toString())){

            editTextsubSpecificationKey.setError("Please enter specification name");
            return false;
        }
        if(TextUtils.isEmpty(editTextsubSpecificationValue.getText().toString())){

            editTextsubSpecificationValue.setError("Please enter specification");
            return false;
        }

        return true;
    }

    private boolean checkMainSpecificationValidation(){

        if(TextUtils.isEmpty(editTextMainSpecificationValue.getText().toString())){

            editTextMainSpecificationValue.setError("Please enter specification");
            return false;
        }
        return true;
    }



    private void initProductSpecificationLayout(){

        if(!productSubSpecificationArrayList.isEmpty()&&!productMainSpecificationArrayList.isEmpty()){
            tv_SpecificationStatus.setVisibility(View.GONE);
            rvProductSpecification.setVisibility(View.VISIBLE);

            productSpecificationAdapter = new ProductSpecificationAdapter(this, productMainSpecificationArrayList,
                    subSpecificationDataHashMap,productSubSpecificationArrayList);
            rvProductSpecification.setHasFixedSize(true);
            rvProductSpecification.setNestedScrollingEnabled(false);
            rvProductSpecification.setLayoutManager(new GridLayoutManager(this, 1));
            rvProductSpecification.setItemAnimator(new DefaultItemAnimator());
            rvProductSpecification.setAdapter(productSpecificationAdapter);
        }else{
            tv_SpecificationStatus.setVisibility(View.VISIBLE);
            rvProductSpecification.setVisibility(View.GONE);
            tv_SpecificationStatus.setText("Product Specifications not available");
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        productSpecificationActivity = this;
        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProductSpecificationActivity.this, new NoInternetListener() {
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
        ProductSpecificationActivity.this.registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        this.unregisterReceiver(checkConnectivity);
    }
}
