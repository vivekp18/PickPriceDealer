package com.efunhub.starkio.pickpricedealer.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.efunhub.starkio.pickpricedealer.Activity.LoginActivity;
import com.efunhub.starkio.pickpricedealer.Activity.TermsAndPrivacyActivity;
import com.efunhub.starkio.pickpricedealer.BroadCastReciver.CheckConnectivity;
import com.efunhub.starkio.pickpricedealer.Interface.IResult;
import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;
import com.efunhub.starkio.pickpricedealer.Modal.City;
import com.efunhub.starkio.pickpricedealer.Modal.Country;
import com.efunhub.starkio.pickpricedealer.Modal.State;
import com.efunhub.starkio.pickpricedealer.R;
import com.efunhub.starkio.pickpricedealer.Utility.SessionManager;
import com.efunhub.starkio.pickpricedealer.Utility.ToastClass;
import com.efunhub.starkio.pickpricedealer.Utility.VolleyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.text.method.LinkMovementMethod.getInstance;
import static com.efunhub.starkio.pickpricedealer.Utility.ContantVariables.DEALER_REGISTRATION;

public class RegistrationSecondFragment extends Fragment implements View.OnClickListener{

    private View view;
    private Spinner spinnerCountry, spinnerState, spinnerCity;
    private Button btnRegister;
    private EditText edt_area,edt_address,edt_pinCode,edt_shopName,edt_shopActNo,edt_shopGSTNo;
    private CheckBox checkBoxTermsAndConditions;
    private TextView tvTermsAndPrivacy;
    private TextView tvPrivacyPolicy;

    ArrayList<String> countryList = new ArrayList<>();
    ArrayList<String> stateList = new ArrayList<>();
    ArrayList<String> cityList = new ArrayList<>();

    private Country countryModel;
    private State stateModel;
    private City cityModel;


    private String country, state, city;

    private String name,email,contact,password;

    private ToastClass toastClass;
    private ProgressDialog progressDialog;
    private CheckConnectivity checkConnectivity;
    private boolean connectivityStatus = true;
    private SessionManager sessionManager;
    private IResult mResultCallback;
    private VolleyService mVollyService;

    private String LocationRetriveUrl="locations.php";
    private String DealerRegistration="registration_dealer.php";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registration_second, container, false);

        init();

        //Retrieve from 1st fragment
        name = getArguments().getString("Name");
        email = getArguments().getString("Email");
        contact = getArguments().getString("Contact No");
        password = getArguments().getString("Password");


        //retrieve all countries
        retriveAllCountry();
        //retriveAllStates("India");

        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                country = adapterView.getSelectedItem().toString();

              if (!country.equals("Select Country"))  {

                    retriveAllStates(country);

                    cityList.clear();
                    spinnerCity.setEnabled(false);
                    spinnerState.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state = adapterView.getSelectedItem().toString();

               if (!state.equals("Select State")) {
                    retriveAllCity(state);
                    spinnerCity.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                city = adapterView.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnRegister.setOnClickListener(this);

        SpannableString ss = new SpannableString("By clicking on 'Register', you confirm\n" +
                "that you accept the Terms of Use and Privacy Policy.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intentTerms = new Intent(getActivity(),TermsAndPrivacyActivity.class);
                startActivity(intentTerms);
            }
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, 58, 91, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //TextView textView = (TextView) findViewById(R.id.hello);
        tvTermsAndPrivacy.setText(ss);
        tvTermsAndPrivacy.setMovementMethod(getInstance());
        tvTermsAndPrivacy.setHighlightColor(Color.TRANSPARENT);
        //tvTermsAndPrivacy.setOnClickListener(this);


        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setRetainInstance(true);

    }

    private void init() {
        spinnerCountry = view.findViewById(R.id.spnCountry);
        spinnerState = view.findViewById(R.id.spnState);
        spinnerCity = view.findViewById(R.id.spnCity);
        btnRegister = view.findViewById(R.id.btnRegister);
        edt_area = view.findViewById(R.id.edtRegistrationArea);
        edt_address = view.findViewById(R.id.edtRegistrationAddress);
        edt_pinCode = view.findViewById(R.id.edtRegistrationPinCode);
        edt_shopName = view.findViewById(R.id.edtRegistrationSopNAme);

        edt_shopActNo = view.findViewById(R.id.edtRegistrationShopActNumber);
        edt_shopGSTNo = view.findViewById(R.id.edtRegistrationGstNo);

        tvTermsAndPrivacy = view.findViewById(R.id.tvPrivacyAndTermsPolicies);


        toastClass = new ToastClass();
    }




    private boolean checkValid() {
        //validations

         if(edt_shopName.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_shopName.getText().toString())){
            edt_shopName.setError("Please enter  shop name");
            return false;
        }else if(edt_shopActNo.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_shopName.getText().toString())){
             edt_shopActNo.setError("Please enter  shop act number");
            return false;
        }else if(edt_shopGSTNo.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_shopName.getText().toString())){
             edt_shopGSTNo.setError("Please enter  shop GST number");
            return false;
        }
        else if (country.equals("Select Country")) {
            toastClass.makeToast(getActivity(), "Please select country");
            return false;
        } else if (state.equals("Select State")) {
            toastClass.makeToast(getActivity(), "Please select  state");
            return false;
        }else if(city.equals("Select City")){
            toastClass.makeToast(getActivity(), "Please select  city");
            return false;
        }else if(edt_area.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_area.getText().toString())){
             edt_area.setError("Please enter  area");
            return false;
        }else if(edt_address.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_address.getText().toString())){
             edt_address.setError("Please enter  address");
            return false;
        }else if(edt_pinCode.getText().toString().equalsIgnoreCase("") ||
                TextUtils.isEmpty(edt_pinCode.getText().toString())){
             edt_pinCode.setError("Please enter  pincode");
            return false;
        }

        return true;
    }




    private void dealerRegistration() {

        initVolleyCallback();

        mVollyService = new VolleyService(mResultCallback, getActivity());

        HashMap<String, String> params = new HashMap<>();
        params.put("name",name);
        params.put("email",email);
        params.put("contact",contact);
        params.put("password",password);
        params.put("country",country);
        params.put("state",state);
        params.put("city",city);
        params.put("area",edt_area.getText().toString());
        params.put("address",edt_address.getText().toString());
        params.put("pincode",edt_pinCode.getText().toString());
        params.put("shop_name",edt_shopName.getText().toString());
        params.put("shop_act_no",edt_shopActNo.getText().toString());
        params.put("gst_no",edt_shopGSTNo.getText().toString());

        mVollyService.postDataVolleyParameters(DEALER_REGISTRATION,
                this.getResources().getString(R.string.base_url) + DealerRegistration,params);
    }


    private void initVolleyCallback() {
        try{
            mResultCallback = new IResult() {
                @Override
                public void notifySuccess(int requestId, String response) {

                    switch (requestId){

                        case DEALER_REGISTRATION:
                            try {
                                JSONObject jsonObject = new JSONObject(response);


                                int status = jsonObject.getInt("status");
                                if (status == 0) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getActivity(), "Something went to wrong, Please try again.");
                                }
                                else if (status == 1) {
                                    progressDialog.cancel();

                                    toastClass.makeToast(getActivity(), "Successfully register.");

                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else if(status == 2) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), "Check Email Format.");
                                }

                                else if(status == 3) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), "Enter Valid Contact.");
                                }

                                else if(status == 4) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), "Password must be at least 8 characters and must contain at least one lower case letter," +
                                            "one upper case letter and one digit.");
                                }
                                else if(status == 5) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), "Contact and Email Already Present.");
                                }
                                else if(status == 6) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), "Email Already Present.");
                                }
                                else if(status == 7) {
                                    progressDialog.cancel();
                                    toastClass.makeToast(getActivity(), " Contact Already Present.");
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

    private void retriveAllCountry() {

        progressDialog = new ProgressDialog(getActivity(),R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getContext().getResources().getString(R.string.base_url) + LocationRetriveUrl,
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

                            spinnerCountry.setAdapter(new ArrayAdapter<String>(getContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    countryList));
                        }

                    } else {
                        progressDialog.cancel();
                        toastClass.makeToast(getActivity(), "Unable to load countries.");
                    }

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
                params.put("location", "");

                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }

    private void retriveAllStates(final String countryname) {

        //String countyr = countryname;

        Log.d("", "retriveAllStates: "+countryname);
        progressDialog = new ProgressDialog(getActivity(),R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getContext().getResources().getString(R.string.base_url) + LocationRetriveUrl,
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

                    spinnerState.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            stateList));

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
                params.put("location", "Country");
                params.put("countryname", countryname);

                return params;
            }
        };

        Volley.newRequestQueue(getContext()).add(stringRequest);

    }


    private void retriveAllCity(final String statename) {

        progressDialog = new ProgressDialog(getActivity(),R.style.AlertDialogStyle);
        progressDialog.setTitle("Pick Price");
        progressDialog.setMessage("Please wait.");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                getContext().getResources().getString(R.string.base_url) + LocationRetriveUrl,
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

                    spinnerCity.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, cityList));

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

        Volley.newRequestQueue(getContext()).add(stringRequest);
    }



    @Override
    public void onResume() {
        super.onResume();

        //Check connectivity
        checkConnectivity = new CheckConnectivity(getActivity(), new NoInternetListener() {
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
        getActivity().registerReceiver(checkConnectivity, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        /*UnRegister receiver for connectivity*/
        getActivity().unregisterReceiver(checkConnectivity);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btnRegister :

                if(connectivityStatus){
                    if(checkValid()){
                        progressDialog = new ProgressDialog(getActivity(),R.style.AlertDialogStyle);
                        progressDialog.setTitle("Pick Price");
                        progressDialog.setMessage("Please wait.");
                        progressDialog.setIndeterminate(true);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        dealerRegistration();
                    }
                }
                break;


        }
    }
}
