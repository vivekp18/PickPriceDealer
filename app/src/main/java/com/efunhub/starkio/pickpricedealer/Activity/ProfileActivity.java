package com.efunhub.starkio.pickpricedealer.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.media.ExifInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.CircularImageView;
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
import java.util.regex.Pattern;

import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.CAMERA_PROFILE_PIC_REQUEST;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.PICK_POFILE_IMAGE_REQUEST;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.PROFILE;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.STORAGE_PERMISSION_CODE;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.UPADTE_PROFILE;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.UPADTE_PROFILE_PICTURE;
import static com.efunhub.starkio.pickpricedealer.Utility.SessionManager.KEY_ID;

//import android.media.ExifInterface;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TextView tvToolbar;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;
    private String ProfileURL="profile_dealer.php";
    private String UpdateProfileURL="update_profile_dealer.php";
    private String LocationRetriveUrl="locations.php";

    private EditText edt_name;
    private EditText edt_email;
    private EditText edt_phone_number;
    private EditText edt_shop_name;
    private EditText edt_country;
    private EditText edt_state;
    private EditText edt_city;
    private EditText edt_area;
    private EditText edt_address;
    private EditText edt_pincode;
    private Button btn_SaveProfile;
    private ImageButton iv_profilePicture;
    private ImageView iv_profilePictureEditOption;
    private CircularImageView circularImageView;

    ArrayList<String> countryList ;
    ArrayList<String> stateList ;
    ArrayList<String> cityList ;

    private Spinner spinnerCountry, spinnerState, spinnerCity;

    private String country, state, city;

    ArrayAdapter<String> countryAdapter;
    ArrayAdapter<String> stateAdapter;
    ArrayAdapter<String> cityAdapter;

    private String profilePicture = null;
    private String UploadProfilePictureUrl = "upload_dealer_profile.php";

    private Uri filePath;
    private Bitmap bitmap;

    private String profileImageTag =null;
    private AlertDialog alertDialog;
    String picturePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
        setupToolbar();
        requestpermission();



        if(connectivityStatus){
            //dealerLoadProfile();
            retriveAllCountry();
        }else{
            toastClass.makeToast(getApplicationContext(), "Please Check internet connection.");
        }

        btn_SaveProfile.setOnClickListener(this);

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    showProfilePictureOptionDilog();
            }
        });

        edt_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCountryDialog();
            }
        });

        edt_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!TextUtils.isEmpty(edt_country.getText().toString())||
                        !edt_country.getText().toString().equalsIgnoreCase("")||
                        !edt_country.getText().toString().equalsIgnoreCase("Select country")){
                    retriveAllStates(edt_country.getText().toString());
                }

            }
        });

        edt_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edt_state.getText().toString())||
                        !edt_state.getText().toString().equalsIgnoreCase("")||
                        !edt_state.getText().toString().equalsIgnoreCase("Select state")){
                    retriveAllCity(edt_state.getText().toString());
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_Logout:
                logoutDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Profile");
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


        edt_name = findViewById(R.id.edtProfileName);
        edt_email = findViewById(R.id.edtProfileEmail);
        edt_phone_number = findViewById(R.id.edtProfileContact);
        edt_shop_name = findViewById(R.id.edtProfileShopName);
        edt_country = findViewById(R.id.edtProfileCountry);
        edt_state = findViewById(R.id.edtProfileState);
        edt_city = findViewById(R.id.edtProfileCity);
        edt_area = findViewById(R.id.edtProfileArea);
        edt_address = findViewById(R.id.edtProfileAddress);
        edt_pincode = findViewById(R.id.edtProfilePincode);
        //iv_profilePicture = findViewById(R.id.ivProfilePic);
        circularImageView = findViewById(R.id.ivProfilePic);

        btn_SaveProfile = findViewById(R.id.btnProfileSave);

        countryList = new ArrayList<>();
        stateList = new ArrayList<>();
        cityList = new ArrayList<>();



        sessionManager = new SessionManager(this);


        toastClass = new ToastClass();
    }
    private void logoutDialog() {


        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.app_logut_dialog, null);
        dialogBuilder.setView(dialogView);

         ImageView ivClose = dialogView.findViewById(R.id.icCloseDialog);

        Button btnAppExit = dialogView.findViewById(R.id.btnExit);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btnAppExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logout();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void  showProfilePictureOptionDilog(){

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
            builderSingle.setTitle("");

        final ArrayAdapter<String> optionArrayAdapter = new ArrayAdapter<String>(ProfileActivity.this,
                android.R.layout.simple_list_item_1);
        optionArrayAdapter.add("Take Picture");
        optionArrayAdapter.add("Pick From Gallery");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builderSingle.setAdapter(optionArrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   if(which==0){
                       Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(takePicture, CAMERA_PROFILE_PIC_REQUEST);//zero can be replaced with any action code
                       dialog.dismiss();

                   }else if(which==1){
                       showFileChooser(PICK_POFILE_IMAGE_REQUEST);
                       dialog.dismiss();
                   }

                }
            });
           AlertDialog alert = builderSingle.create();
        alert.show();

        Button negativeButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        //nbutton.setBackgroundColor(Color.GRAY);
        negativeButton.setTextColor(Color.GRAY);

    }

    private void showFileChooser(int pickImageRequest) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), pickImageRequest);
    }

    private void showCountryDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
        builderSingle.setTitle("Select Country");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(countryAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_country.setText(countryAdapter.getItem(which));

                edt_state.setText("Select state");
                edt_city.setText("Select city");
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }
    private void showStateDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
        builderSingle.setTitle("Select State");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(stateAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_state.setText(stateAdapter.getItem(which));
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void showCityDialog(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ProfileActivity.this);
        builderSingle.setTitle("Select City");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builderSingle.setAdapter(cityAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                edt_city.setText(cityAdapter.getItem(which));
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void dealerLoadProfile() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait while loading profile..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProfileActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);

        mVollyService.postDataVolleyParameters(PROFILE,
                this.getResources().getString(R.string.base_url) + ProfileURL,params);
    }

    private void updateDealerProfile() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait while updating profile..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProfileActivity.this);

        HashMap<String, String> dealerProfileInfo = sessionManager.getDealerDetails();

        String dealer_id = dealerProfileInfo.get(KEY_ID);

        HashMap<String, String> params = new HashMap<>();
        params.put("dealer_id",dealer_id);
        params.put("name",edt_name.getText().toString());
        params.put("email",edt_email.getText().toString());
        params.put("contact",edt_phone_number.getText().toString());
        params.put("country",edt_country.getText().toString());
        params.put("state",edt_state.getText().toString());
        params.put("city",edt_city.getText().toString());
        params.put("area",edt_area.getText().toString());
        params.put("address",edt_address.getText().toString());
        params.put("pincode",edt_pincode.getText().toString());
        params.put("shop_name",edt_shop_name.getText().toString());

        mVollyService.postDataVolleyParameters(UPADTE_PROFILE,
                this.getResources().getString(R.string.base_url) + UpdateProfileURL,params);
    }

    private void uploadImage(String image, int i) {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait while updating profile..");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, ProfileActivity.this);

        HashMap<String,String> dealerInfo = sessionManager.getDealerDetails();

        String dealerId = dealerInfo.get(KEY_ID);

        HashMap<String, String> param = new HashMap<>();
        param.put("dealer_id", dealerId);
        param.put("profileimage", image);
            mVollyService.postDataVolleyParameters(UPADTE_PROFILE_PICTURE,
                    this.getResources().getString(R.string.base_url) + UploadProfilePictureUrl, param);
    }

    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId) {

                        case PROFILE:
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {

                                    progressDialog.cancel();

                                    JSONObject json_results = jsonObject.getJSONObject("myprofile");

                                    if(json_results.has("profileimage")){
                                        try{

                                            PicassoTrustAll.getInstance(ProfileActivity.this)
                                                    .load(json_results.getString("profileimage"))
                                                    .placeholder(R.drawable.placeholder_profile_image_dark)
                                                    .into(circularImageView);

                                            //sessionManager.createProfileImageSession(json_results.getString("profileimage"));

                                        }catch(Exception ex){
                                            Log.e("ProfileActivity", "notifySuccess: ",ex );
                                        }
                                    }else {
                                        iv_profilePicture.setImageResource(R.drawable.placeholder_profile_image_dark);
                                    }

                                    if (json_results.has("name")) {

                                        edt_name.setText(json_results.getString("name"));
                                    }else{
                                        edt_name.setText("Not available");
                                    }
                                    if (json_results.has("email")) {

                                        edt_email.setText(json_results.getString("email"));

                                    }else{
                                        edt_email.setText("Not available");
                                    }
                                    if (json_results.has("contact")) {
                                        edt_phone_number.setText(json_results.getString("contact"));
                                    }else{
                                        edt_phone_number.setText("Not available");
                                    }

                                    if (json_results.has("shop_name")) {
                                        edt_shop_name.setText(json_results.getString("shop_name"));
                                    }else{
                                        edt_shop_name.setText("Not available");
                                    }

                                    if (json_results.has("country")) {
                                        edt_country.setText(json_results.getString("country"));
                                    }else{
                                        edt_country.setText("Select country");
                                    }

                                    if (json_results.has("state")) {
                                        edt_state.setText(json_results.getString("state"));
                                    }else{
                                        edt_state.setText("Select state");
                                    }

                                    if (json_results.has("city")) {
                                        edt_city.setText(json_results.getString("city"));
                                    }else{
                                        edt_city.setText("Select city");
                                    }

                                    if (json_results.has("area")) {
                                        edt_area.setText(json_results.getString("area"));
                                    }else{
                                        edt_area.setText("Not available");
                                    }

                                    if (json_results.has("address")) {
                                        edt_address.setText(json_results.getString("address"));
                                    }else{
                                        edt_address.setText("Not available");
                                    }

                                    if (json_results.has("pincode")) {
                                        edt_pincode.setText(json_results.getString("pincode"));
                                    }else{
                                        edt_pincode.setText("Not available");
                                    }



                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to load data");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }
                            break;

                        case UPADTE_PROFILE :
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    if(profileImageTag!=null){
                                        uploadImage(profilePicture, 2);
                                    }else{
                                        toastClass.makeToast(getApplicationContext(), "Successfully Updated");
                                    }



                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to update profile");
                                }

                            } catch (JSONException e) {
                                progressDialog.cancel();
                                e.printStackTrace();
                            }

                            break;


                        case UPADTE_PROFILE_PICTURE :
                            try {
                                JSONObject jsonObject = new JSONObject(response);

                                int status = jsonObject.getInt("status");
                                if (status == 1) {
                                    progressDialog.cancel();
                                    dealerLoadProfile();
                                    toastClass.makeToast(getApplicationContext(), "Successfully Updated");

                                } else {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getApplicationContext(), "Unable to update profile");
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

    private void requestpermission() {
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]
                {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    //handling the image chooser activity result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA_PROFILE_PIC_REQUEST:
                if (resultCode == RESULT_OK) {

                    Bitmap profilePictureBitmap = (Bitmap) data.getExtras().get("data");
                    circularImageView.setImageBitmap(profilePictureBitmap);
                    //String profilepic = getResizedBitmap(bitmap, 400);
                    profilePicture = getStringImage(profilePictureBitmap);

                    profileImageTag = profilePictureBitmap.toString();
                }
                break;

            case  PICK_POFILE_IMAGE_REQUEST:

                if(data!=null){
                    filePath = data.getData();

                    try {
                       Bitmap docBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);

                        //realPath = ImageFilePath.getPath(this, data.getData());

                        //Convert System Path to string and Check SDK version is Greater then KITKAT

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            picturePath = ImageFilePath.getPath(this, filePath);
                        }

                        if(!picturePath.endsWith("png")){
                            bitmap = exifInterface(picturePath, docBitmap);
                        }

                        if(!picturePath.endsWith("jpg")){
                            bitmap = exifInterface(picturePath, docBitmap);
                        }

                        profilePicture = getStringImage(bitmap);
                        circularImageView.setImageBitmap(bitmap);
                        profileImageTag = bitmap.toString();

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

       // Bitmap bm = BitmapFactory.decodeStream(fis);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    private void retriveAllCountry() {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProfileActivity.this.getResources().getString(R.string.base_url) + LocationRetriveUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();

                        countryList.clear();

                        try {
                            JSONObject jsonObject = new JSONObject(response);


                            int status = jsonObject.getInt("status");

                            if (status == 1) {
                                progressDialog.cancel();
                                if(jsonObject.has("allcountry")){

                                    JSONArray jsonArray = jsonObject.getJSONArray("allcountry");

                                    for(int i=0;i<jsonArray.length();i++){

                                        JSONObject jsonObjectdata = jsonArray.getJSONObject(i);

                                        countryList.add(jsonObjectdata.getString("country"));
                                    }

                                    countryAdapter= new ArrayAdapter<String>(ProfileActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, countryList);

                                    //spinnerCountry.setAdapter(spinnercCountryadapter);

                                    dealerLoadProfile();

                                    /*spinnerCountry.setAdapter(new ArrayAdapter<String>(ProfileActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, countryList));*/
                                }

                            } else {
                                progressDialog.cancel();
                                toastClass.makeToast(ProfileActivity.this, "Unable to load countries.");
                            }

                        } catch (JSONException e) {
                            progressDialog.cancel();
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("location", "");

                return params;
            }
        };

        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);
    }

    private void retriveAllStates(final String countryname) {

        //String countyr = countryname;

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.d("", "retriveAllStates: "+countryname);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProfileActivity.this.getResources().getString(R.string.base_url) + LocationRetriveUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();

                        //converting response to json object
                        stateList.clear();
                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            JSONArray country = jsonObj.getJSONArray("allstate");

                            stateList.add(0,"Select State");

                            for (int i = 0; i < country.length(); i++) {

                                JSONObject jsonObject = country.getJSONObject(i);

                                stateList.add(jsonObject.getString("state"));

                            }

                            stateAdapter= new ArrayAdapter<String>(ProfileActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, stateList);

                        } catch (JSONException e) {
                            progressDialog.cancel();
                            e.printStackTrace();
                        }
                        progressDialog.cancel();

                        showStateDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("location", "Country");
                params.put("countryname", countryname);

                return params;
            }
        };

        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);

    }


    private void retriveAllCity(final String statename) {

        progressDialog = new ProgressDialog(this,R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProfileActivity.this.getResources().getString(R.string.base_url) + LocationRetriveUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressDialog.dismiss();

                        //converting response to json object
                        cityList.clear();
                        try {
                            JSONObject jsonObj = new JSONObject(response);

                            JSONArray country = jsonObj.getJSONArray("allcity");

                            cityList.add(0,"Select City");
                            for (int i = 0; i < country.length(); i++) {

                                JSONObject jsonObject = country.getJSONObject(i);

                                cityList.add(jsonObject.getString("city"));

                            }

                            cityAdapter= new ArrayAdapter<String>(ProfileActivity.this,
                                    android.R.layout.simple_spinner_dropdown_item, cityList);

                            showCityDialog();

                        } catch (JSONException e) {
                            progressDialog.cancel();
                            e.printStackTrace();
                        }
                        progressDialog.cancel();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("location", "State");
                params.put("statename", statename);

                return params;
            }
        };

        Volley.newRequestQueue(ProfileActivity.this).add(stringRequest);
    }

    private boolean checkValid() {
        //validations

        if(edt_name.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_name.getText().toString())){
            edt_name.setError("Please enter Full name");
            return false;
        }
        if(edt_email.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_email.getText().toString())){
            edt_email.setError("Please enter email address");
            return false;
        }

        if(edt_phone_number.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_phone_number.getText().toString())){
            edt_phone_number.setError("Please enter Phone number");
            return false;
        }

        if(edt_shop_name.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_shop_name.getText().toString())){
            edt_shop_name.setError("Please enter Shop name");
            return false;
        }

        if (edt_country.getText().toString().equalsIgnoreCase("Select country")) {
            toastClass.makeToast(ProfileActivity.this, "Please select country");
            return false;
        } else if (edt_state.getText().toString().equalsIgnoreCase("Select state")) {
            toastClass.makeToast(ProfileActivity.this, "Please select  state");
            return false;
        }else if(edt_city.getText().toString().equalsIgnoreCase("Select city")){
            toastClass.makeToast(ProfileActivity.this, "Please select  city");
            return false;
        }else if(edt_area.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_area.getText().toString())){
            edt_area.setError("Please enter Area");
            return false;
        }else if(edt_address.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_address.getText().toString())){
            edt_address.setError("Please enter Address");
            return false;
        }else if(edt_pincode.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_pincode.getText().toString())){
            edt_pincode.setError("Please enter Pincode");
            return false;
        }else if (!TextUtils.isEmpty(edt_email.getText().toString()) ||
                !edt_email.getText().toString().equalsIgnoreCase("")) {

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (!pat.matcher(edt_email.getText().toString()).matches()) {
                edt_email.setError("Please enter valid email address");
                return pat.matcher(edt_email.getText().toString()).matches();
            } else {
                if (!TextUtils.isEmpty(edt_phone_number.getText().toString()) &&
                        !edt_phone_number.getText().toString().equalsIgnoreCase("")) {

                    String phoneRegex = "^[6-9][0-9]{9}$";

                    Pattern pattern = Pattern.compile(phoneRegex);

                    if (!pattern.matcher(edt_phone_number.getText().toString()).matches()) {
                        edt_phone_number.setError("Please enter valid phone number");
                        return pattern.matcher(edt_phone_number.getText().toString()).matches();
                    }
                }
            }
        }

        return true;
    }



    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(ProfileActivity.this, new NoInternetListener() {
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
        ProfileActivity.this.registerReceiver(checkConnectivity, intentFilter);
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
            case R.id.btnProfileSave :

                if(connectivityStatus){
                    if(checkValid()){
                        updateDealerProfile();
                    }

                }else{
                    toastClass.makeToast(getApplicationContext(), "Please Check internet connection.");
                }
                break;
        }
    }
}
