package com.efunhub.starkio.pickpricedealer.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.efunhub.starkio.pickpricedealer.R;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin, btnRegister;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);



        init();

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnWelcomeLogin:
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                break;

            case R.id.btnWelcomeRegister:
                startActivity(new Intent(WelcomeActivity.this, RegistrationActivity.class));
                break;
        }
    }

    private void init() {
        btnLogin = findViewById(R.id.btnWelcomeLogin);
        btnRegister = findViewById(R.id.btnWelcomeRegister);
    }

}
