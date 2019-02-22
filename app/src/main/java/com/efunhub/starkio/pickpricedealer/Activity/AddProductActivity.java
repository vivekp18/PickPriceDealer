package com.efunhub.starkio.pickpricedealer.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Modal.Brand;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.AssetDatabaseOpenHelper;
import com.efunhub.starkio.pickpricedealer.Utility.DataBaseHelper;
import com.efunhub.starkio.pickpricedealer.Utility.ImageFilePath;
import com.efunhub.starkio.pickpricedealer.Utility.PicassoTrustAll;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.ADD_PRODUCT;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.CAMERA_PROFILE_PIC_REQUEST;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.PICK_POFILE_IMAGE_REQUEST;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.RETRIVE_BRANDS;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.STORAGE_PERMISSION_CODE;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.SUBMIT_PRODUCT_PICTURE;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.UPDATE_PRODUCTS;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

public class AddProductActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView tvToolbar;

    String DB_NAME = "pickpricedb.db";
    String TABLE_NAME = "color";
    DataBaseHelper myDBHelper;
    ArrayList<String> ColorArrayList;
    ArrayList<String> BrandArrayLists;

    private ArrayAdapter<String> colorAdapter;
    private ArrayAdapter<String> brandAdapter;
    private ArrayList<Brand> lstBrandArrayList;
    private Brand brandModel;
    private HashMap<String,String> brandHashMap;

    private EditText edt_ProductName;
    private EditText edt_ProductPrice;
    private EditText edt_ProductDescription;
    private EditText edt_ProductAvailability;
    private EditText edt_Brand;
    private EditText edt_Color;
    private ImageView iv_product_image;
    private Button btn_addProduct;
    private Button btn_Updateroduct;

    private String productPicture;
    private String categroryId;

    private String productName, productPrice,productId,productDescription,productImage,productStatus;

    private String productColor,productBrand,prodductBrandId;


    private Uri filePath;
    private Bitmap bitmap;
    private AlertDialog alertDialog;
    private ImageView ivClose;

    private TextView tvTakePicture,tvPickFromGallery;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String AddProductUrl="add_product.php";
    private String UpdateProductUrl="update_product.php";
    private String UpdateProductPictureURL="upload_product_image.php";
    private String RetriveBrandUrl="show_brands.php";
    private String LocationRetriveUrl="locations.php";

    //addProductLayout

    //private CheckConnectivity checkConnectivity;
    //private boolean connectivityStatus = true;
    private RelativeLayout addProductLayout;
    private LinearLayout noInternetConn;
    private TextView tvRetry;

    private Snackbar snackbar;
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 0;
    public static int TYPE_NOT_CONNECTED = 2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        init();

        requestpermission();

        setupToolbar();

        if(connectivityStatus){
            retriveAllBrands();
        }else{
            toastClass.makeToast(getApplicationContext(),"Please check internet connection.");
        }

        iv_product_image.setOnClickListener(this);
        edt_Color.setOnClickListener(this);
        edt_Brand.setOnClickListener(this);
        edt_ProductAvailability.setOnClickListener(this);
        btn_addProduct.setOnClickListener(this);
        btn_Updateroduct.setOnClickListener(this);
    }


    private void showColorDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddProductActivity.this);
        builderSingle.setTitle("Select Color");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(colorAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_Color.setText(colorAdapter.getItem(which));
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builderSingle.create();
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.GRAY);
    }

    private void showBrandDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddProductActivity.this);
        builderSingle.setTitle("Select Brand");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(brandAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_Brand.setText(brandAdapter.getItem(which));
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builderSingle.create();
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.GRAY);
    }


    private void showAvailabilityDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddProductActivity.this);
        builderSingle.setTitle("Pick Price");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddProductActivity.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Available");
        arrayAdapter.add("Not Available");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                edt_ProductAvailability.setText(arrayAdapter.getItem(which));
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builderSingle.create();
        alertDialog.show();

        Button negativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.GRAY);
    }


    private void setupToolbar() {
        setSupportActionBar(mToolbar);

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

        edt_Brand = findViewById(R.id.edtProductBrand);

        edt_Color = findViewById(R.id.edtProductColor);

        edt_ProductName = findViewById(R.id.edtProductName);

        edt_ProductDescription = findViewById(R.id.edtProductDescription);

        edt_ProductAvailability = findViewById(R.id.edtProductAvailability);

        edt_ProductPrice = findViewById(R.id.edtProductPrice);

        iv_product_image = findViewById(R.id.ivProductImage);

        btn_addProduct = findViewById(R.id.btnAddProduct);

        btn_Updateroduct = findViewById(R.id.btnUpdateProduct);

        ColorArrayList = new ArrayList<>();

        BrandArrayLists = new ArrayList<>();

        lstBrandArrayList = new ArrayList<>();

        brandHashMap = new HashMap<>();

        addProductLayout=(RelativeLayout) findViewById(R.id.addProductLayout);
        noInternetConn=(LinearLayout) findViewById(R.id.llNoInternetHomeFrag);
        tvRetry=(TextView) findViewById(R.id.tvRetryHomeFrag);

        if(getIntent().hasExtra("category_id")){

            tvToolbar.setText("Add Product");

            categroryId = getIntent().getStringExtra("category_id");

            edt_ProductAvailability.setVisibility(View.GONE);
        }else{
            tvToolbar.setText("Update Product Details");

            btn_Updateroduct.setVisibility(View.VISIBLE);

            btn_addProduct.setVisibility(View.GONE);

            edt_ProductAvailability.setVisibility(View.VISIBLE);

            productId  = getIntent().getStringExtra("ProductId");

            productName  = getIntent().getStringExtra("ProductName");
            edt_ProductName.setText(productName);

            productPrice  = getIntent().getStringExtra("ProductPrice");
            edt_ProductPrice.setText(productPrice);

            productDescription  = getIntent().getStringExtra("ProductDescription");
            edt_ProductDescription.setText(productDescription);

            productBrand = getIntent().getStringExtra("ProductBrand");
            edt_Brand.setText(productBrand);

            productColor  = getIntent().getStringExtra("ProductColor");

            edt_Color.setText(productColor);

            productStatus = getIntent().getStringExtra("ProductStatus");

            if(productStatus.equalsIgnoreCase("0")){
                edt_ProductAvailability.setText("Available");
            }else{
                edt_ProductAvailability.setText("Not Available");
            }



            productImage  = getIntent().getStringExtra("ProductImage");


            if (!productImage.equalsIgnoreCase("")
                    || !productImage.isEmpty()) {

                try{

                    PicassoTrustAll.getInstance(this)
                            .load(productImage)
                            .placeholder(R.drawable.ic_camera)
                            .into(iv_product_image);


                }catch(Exception ex){

                    Log.e("ProductListAdapter", "onBindViewHolder: ",ex );
                }


            } else {
                iv_product_image.setImageResource(R.drawable.ic_camera);
            }


        }

        sessionManager = new SessionManager(this);

        toastClass = new ToastClass();

        //Copy database
        AssetDatabaseOpenHelper assetDatabaseOpenHelper = new AssetDatabaseOpenHelper(this, DB_NAME);
        assetDatabaseOpenHelper.saveDatabase();

        myDBHelper = new DataBaseHelper(this, DB_NAME);

        Cursor res = myDBHelper.getAllData(TABLE_NAME);

        if (res.getCount() == 0) {
            return;
        } else {
            while (res.moveToNext()) {
                ColorArrayList.add(res.getString(1));
            }
            colorAdapter= new ArrayAdapter<String>(AddProductActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, ColorArrayList);

        }
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){

            case R.id.edtProductBrand:
                if(!BrandArrayLists.isEmpty()){
                    showBrandDialog();
                }else{
                    toastClass.makeToast(getApplicationContext(),"Brands not available");
                }
                break;

            case R.id.edtProductColor:
                showColorDialog();
                break;

            case R.id.ivProductImage:
                showProductPictureOptionDilog();
                break;

            case R.id.btnAddProduct:
                if(connectivityStatus){
                    if(checkValid()){
                        addProduct();
                    }
                }
                break;

            case R.id.btnUpdateProduct:
                if(connectivityStatus){
                    if(checkValid()){
                        updateProduct();
                    }
                }
                break;

            case R.id.edtProductAvailability:
                if(connectivityStatus){
                    showAvailabilityDialog();
                }
                break;

        }

    }

    private boolean checkValid() {
        //validations

        if(edt_ProductName.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_ProductName.getText().toString())){
            edt_ProductName.setError("Please enter product name");
            return false;
        }
        if(edt_ProductPrice.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_ProductPrice.getText().toString())){
            edt_ProductPrice.setError("Please enter product price");
            return false;
        }
        if(edt_ProductDescription.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_ProductDescription.getText().toString())){
            edt_ProductDescription.setError("Please enter product description");
            return false;
        }
        if (edt_Brand.getText().toString().equalsIgnoreCase("")||
                TextUtils.isEmpty(edt_Brand.getText().toString())) {
            edt_Brand.setError("Please select brand");
            return false;
        } else if (edt_Color.getText().toString().equalsIgnoreCase("")||
                TextUtils.isEmpty(edt_Color.getText().toString())) {
            edt_Color.setError( "Please select  color");
            return false;
        }/*else if (edt_ProductAvailability.getText().toString().equalsIgnoreCase("")||
                TextUtils.isEmpty(edt_ProductAvailability.getText().toString())) {
            edt_ProductAvailability.setError( "Please select status");
            return false;
        }*/

        return true;
    }


    public void showProductPictureOptionDilog(){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddProductActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_product_image_dialog, null);
        dialogBuilder.setView(dialogView);

        ivClose = dialogView.findViewById(R.id.icCloseDialog);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        tvTakePicture = dialogView.findViewById(R.id.tvTakePicture);

        tvTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, CAMERA_PROFILE_PIC_REQUEST);//zero can be replaced with any action code
            }
        });

        tvPickFromGallery = dialogView.findViewById(R.id.tvPickFromGallery);

        tvPickFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showFileChooser(PICK_POFILE_IMAGE_REQUEST);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }




    private void showFileChooser(int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageRequest);
    }

    private void requestpermission() {
        if (ContextCompat.checkSelfPermission(AddProductActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(AddProductActivity.this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_PROFILE_PIC_REQUEST:
                if (resultCode == RESULT_OK) {

                    Bitmap idProofBitmap = (Bitmap) data.getExtras().get("data");

                    iv_product_image.setImageBitmap(idProofBitmap);
                    //String profilepic = getResizedBitmap(bitmap, 400);
                    productPicture = getStringImage(idProofBitmap);
                }
                break;

            case  PICK_POFILE_IMAGE_REQUEST:

                if(data!=null){
                filePath = data.getData();

                    try {
                        Bitmap docBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);

                        //realPath = ImageFilePath.getPath(this, data.getData());

                        //Convert System Path to string and Check SDK version is Greater then KITKAT
                        String picturePath = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            picturePath = ImageFilePath.getPath(this, filePath);
                        }

                        bitmap = exifInterface(picturePath, docBitmap);
                        //String profilepic = getResizedBitmap(bitmap, 400);
                        productPicture = getStringImage(bitmap);

                        iv_product_image.setImageBitmap(bitmap);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                break;

        }
    }

    private Bitmap exifInterface(String filePath, Bitmap bitmap) {
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert exif != null;
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);

        return rotateBitmap(bitmap, orientation);
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    //converting image to base64 string
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    //retrive all brands
    private void retriveAllBrands() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, AddProductActivity.this);

        mVollyService.postDataVolley(RETRIVE_BRANDS,
                this.getResources().getString(R.string.base_url) + RetriveBrandUrl);
    }


    //add product
    private void addProduct() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, AddProductActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        Object brandId = getKeyFromValue(brandHashMap,edt_Brand.getText().toString());

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("name",edt_ProductName.getText().toString());
        params.put("price",edt_ProductPrice.getText().toString());
        params.put("descriptions",edt_ProductDescription.getText().toString());
        params.put("brand_id",String.valueOf(brandId));
        params.put("color",edt_Color.getText().toString());
        params.put("category_id",categroryId);

        mVollyService.postDataVolleyParameters(ADD_PRODUCT,
                this.getResources().getString(R.string.base_url) + AddProductUrl,params);
    }

    //update product details
     private void updateProduct() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, AddProductActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        Object brandId = getKeyFromValue(brandHashMap,edt_Brand.getText().toString());

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("product_id",productId);
        params.put("name",edt_ProductName.getText().toString());
        params.put("price",edt_ProductPrice.getText().toString());
        params.put("descriptions",edt_ProductDescription.getText().toString());
        params.put("brand_id",String.valueOf(brandId));
        params.put("color",edt_Color.getText().toString());

        if(edt_ProductAvailability.getText().toString().equalsIgnoreCase("Available")){
            params.put("status","0");
        }else{
            params.put("status","1");
        }

        mVollyService.postDataVolleyParameters(UPDATE_PRODUCTS,
                this.getResources().getString(R.string.base_url) + UpdateProductUrl,params);
    }

    public static Object getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o;
            }
        }
        return null;
    }

    //add product image
    private void uploadImage(String image, String product_id,int i) {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, AddProductActivity.this);

        HashMap<String,String> dealerInfo = sessionManager.getDealerDetails();

        String dealerId = dealerInfo.get(KEY_ID);

        HashMap<String, String> param = new HashMap<>();
        param.put("dealer_id", dealerId);
        param.put("product_id", product_id);
        param.put("productimage", image);
        mVollyService.postDataVolleyParameters(SUBMIT_PRODUCT_PICTURE,
                this.getResources().getString(R.string.base_url) + UpdateProductPictureURL, param);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId) {

                        case RETRIVE_BRANDS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                if(!lstBrandArrayList.isEmpty() ){
                                    lstBrandArrayList.clear();
                                }

                                if(!brandHashMap.isEmpty()){
                                    brandHashMap.clear();
                                }

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    if (jsonObject.has("brands")) {

                                        JSONArray jsonArrayData = jsonObject.getJSONArray("brands");

                                        for(int i=0; i<jsonArrayData.length(); i++) {

                                            brandModel = new Brand();

                                            JSONObject jsonObj = jsonArrayData.getJSONObject(i);

                                            if(jsonObj.has("brand_id")){
                                                brandModel.setBrand_id(jsonObj.getString("brand_id"));
                                            }else{
                                                brandModel.setBrand_id("");
                                            }

                                            if(jsonObj.has("name")){
                                                brandModel.setBrand_name(jsonObj.getString("name"));
                                            }else{
                                                brandModel.setBrand_name("");
                                            }

                                            if(!brandHashMap.containsKey(brandModel.getBrand_id())){
                                                brandHashMap.put(brandModel.getBrand_id(),brandModel.getBrand_name());
                                            }
                                            lstBrandArrayList.add(brandModel);
                                        }


                                        for(Brand brand: lstBrandArrayList){

                                            BrandArrayLists.add(brand.getBrand_name());
                                        }

                                        //init brand adapter for brand dropdown
                                        brandAdapter= new ArrayAdapter<String>(AddProductActivity.this,
                                                android.R.layout.simple_spinner_dropdown_item, BrandArrayLists);
                                    }

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(),
                                            "Something went wrong, please try again.");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;

                        case ADD_PRODUCT:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");

                                if (status == 1) {
                                    progressDialog.cancel();
                                    if(productPicture!=null) {
                                        if (jsonObject.has("product_id")) {
                                            uploadImage(productPicture, jsonObject.getString("product_id"), 1);
                                        }
                                    } else {
                                        progressDialog.cancel();
                                        toastClass.makeToast(getApplicationContext(), "Something went wrong, please try again.");
                                    }
                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Successfully product added.");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;


                        case UPDATE_PRODUCTS:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {

                                    if(productPicture!=null) {
                                        progressDialog.cancel();
                                        uploadImage(productPicture,productId,2);
                                    }else{
                                        progressDialog.cancel();
                                        toastClass.makeToast(getApplicationContext(), "Successfully product updated");
                                    }

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to update product");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }

                            break;

                        case SUBMIT_PRODUCT_PICTURE :
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Successfully product added.");

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to add product");
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
                .make(addProductLayout, internetStatus, Snackbar.LENGTH_LONG)
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
