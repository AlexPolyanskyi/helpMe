package com.microbox.helpme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public class PhoneActivity extends Activity implements DataManager.Callback<List>{
    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        Intent intent = getIntent();
        phone = intent.getStringExtra(Const.PHONE);
    }
    public void onNextClick(View v){
        EditText code = (EditText) findViewById(R.id.code);
        String verificationCode = code.getText().toString();
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "Registration"));
        params.add(new BasicNameValuePair("step", "2"));
        params.add(new BasicNameValuePair(Const.PHONE, phone));
        params.add(new BasicNameValuePair(Const.CODE, verificationCode));
        DataManager.call(params, this, this);
    }

    @Override
    public void done(List result) {
        startActivity(new Intent (PhoneActivity.this, MainActivity.class));
    }

    @Override
    public void error(Exception e) {
        Toast.makeText(this, "Неверный код", Toast.LENGTH_LONG).show();
    }
}
