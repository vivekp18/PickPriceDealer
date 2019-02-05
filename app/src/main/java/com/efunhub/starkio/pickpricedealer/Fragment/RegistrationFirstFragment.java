package com.efunhub.starkio.pickpricedealer.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.efunhub.starkio.pickpricedealer.R;

import java.util.regex.Pattern;

public class RegistrationFirstFragment extends Fragment {

    private View view;
    private FragmentTransaction fragmentTransaction;
    private Button btnNext;

    private EditText edtName, edtEmail, edtContact, edtPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_registration_first, container, false);

        init();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(checkValid()){
                    RegistrationSecondFragment registrationSecondFragment = new RegistrationSecondFragment();

                    Bundle bundle = new Bundle();

                    //starting the BasicDetails fragment
                    fragmentTransaction = getFragmentManager().beginTransaction();

                    String pass =  edtPassword.getText().toString();

                    bundle.putString("Name", edtName.getText().toString());
                    bundle.putString("Email", edtEmail.getText().toString());
                    bundle.putString("Contact No", edtContact.getText().toString());
                    bundle.putString("Password", edtPassword.getText().toString());
                    registrationSecondFragment.setArguments(bundle);

                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.flRegistration, registrationSecondFragment);
                    fragmentTransaction.commit();
                }
            }
        });

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setRetainInstance(true);

    }

    private boolean checkValid() {
        //validations
        if (TextUtils.isEmpty(edtName.getText().toString()) || edtName.getText().toString().equalsIgnoreCase("")) {
            edtName.setError("Please enter full name");
            return false;
        } else if (TextUtils.isEmpty(edtEmail.getText().toString()) || edtEmail.getText().toString().equalsIgnoreCase("")) {
            edtEmail.setError("Please enter email");
            return false;
        } else if (TextUtils.isEmpty(edtContact.getText().toString()) ||
                edtContact.getText().toString().equalsIgnoreCase("")) {
            edtContact.setError("Please enter contact number");
            return false;
        } else if (edtContact.getText().toString().length() < 10) {
            edtContact.setError("Please enter correct contact number");
            return false;
        } else if (TextUtils.isEmpty(edtPassword.getText().toString()) ||
                edtPassword.getText().toString().equalsIgnoreCase("")) {
            edtPassword.setError("Please enter password");
            return false;
        } else if (!TextUtils.isEmpty(edtEmail.getText().toString()) ||
                !edtEmail.getText().toString().equalsIgnoreCase("")) {

            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (!pat.matcher(edtEmail.getText().toString()).matches()) {
                edtEmail.setError("Please enter valid email address");
                return false;
            } else {
                if (!TextUtils.isEmpty(edtContact.getText().toString()) &&
                        !edtContact.getText().toString().equalsIgnoreCase("")) {

                    String phoneRegex = "^[6-9][0-9]{9}$";

                    Pattern pattern = Pattern.compile(phoneRegex);

                    if (!pattern.matcher(edtContact.getText().toString()).matches()) {
                        edtContact.setError("Please enter valid phone number");
                        return false;
                    } else {
                        if (!TextUtils.isEmpty(edtPassword.getText().toString()) ||
                                !edtPassword.getText().toString().equalsIgnoreCase("")) {
                            Pattern patternPass;
                            final String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";//^[a-zA-Z0-9]{8,}$
                            patternPass = Pattern.compile(passwordRegex);

                            if (!patternPass.matcher(edtPassword.getText().toString()).matches()) {
                                edtPassword.setError(" Password must be at least 8 characters and must contain at least one lower case letter," +
                                        "one upper case letter and one digit");
                                return patternPass.matcher(edtPassword.getText().toString()).matches();
                            }
                        }
                    }
                }
            }
        }
        return true;

    }

    private void init() {
        edtName = view.findViewById(R.id.edtRegistrationName);
        edtEmail = view.findViewById(R.id.edtRegistrationEmail);
        edtContact = view.findViewById(R.id.edtRegistrationContact);
        edtPassword = view.findViewById(R.id.edtRegistrationPassword);
        btnNext = view.findViewById(R.id.btnRegistrationNext);
    }
}
