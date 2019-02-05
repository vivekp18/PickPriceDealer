package com.efunhub.starkio.pickpricedealer.Activity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.efunhub.starkio.pickpricedealer.Fragment.RegistrationFirstFragment;
import com.efunhub.starkio.pickpricedealer.R;

public class RegistrationActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private Toolbar mToolbar;
    private TextView tvToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
        setupToolbar();

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.flRegistration, new RegistrationFirstFragment(), "RegisterFirstFragment");
        fragmentTransaction.commit();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        tvToolbar.setText("Create Account");
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
    }
}
