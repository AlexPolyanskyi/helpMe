package com.microbox.helpme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.microbox.helpme.utils.AuthUtils;


public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AuthUtils.isLogged(this.getApplicationContext())) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
