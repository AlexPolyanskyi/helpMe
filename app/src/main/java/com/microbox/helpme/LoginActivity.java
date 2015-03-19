package com.microbox.helpme;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;
import com.microbox.helpme.manager.MyAccountManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 04.11.2014.
 */
public class LoginActivity extends AccountAuthenticatorActivity implements TextView.OnEditorActionListener {
    public static final String EXTRA_TOKEN_TYPE = "com.microbox.helpme.EXTRA_TOKEN_TYPE";
    private EditText ePass, eLogIn;
    private SharedPreferences pref;
    private AccountManager accountManager;
    private Context context;
    private String accName, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ePass = (EditText) findViewById(R.id.password_edt);
        eLogIn = (EditText) findViewById(R.id.login_edt);
        ePass.setOnEditorActionListener(this);
        context = this.getApplicationContext();
        pref = context.getSharedPreferences(Const.FILE_NAME, MODE_PRIVATE);
    }
    public void loadStart(){
        findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        ePass.setVisibility(View.INVISIBLE);
        eLogIn.setVisibility(View.INVISIBLE);
        findViewById(R.id.login_btn).setVisibility(View.INVISIBLE);
        findViewById(R.id.singup_btn).setVisibility(View.INVISIBLE);
    }
    public void loadEnd(){
        findViewById(android.R.id.progress).setVisibility(View.INVISIBLE);
        ePass.setVisibility(View.VISIBLE);
        eLogIn.setVisibility(View.VISIBLE);
        findViewById(R.id.login_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.singup_btn).setVisibility(View.VISIBLE);
    }
    public void onStart(View v) {
        loadStart();
        final Context context1= this.getApplicationContext();
        accountManager = AccountManager.get(context1);
        Account[] accounts = accountManager.getAccountsByType(getString(R.string.accountType));
        accName = eLogIn.getText().toString();
        password = ePass.getText().toString();
        boolean invilidAccount = false;
        for (Account a : accounts) {
            if ((accName.equals(a.name)) && accountManager.getPassword(a).equals(password)) {
                invilidAccount = false;
                SharedPreferences.Editor ed = pref.edit();
                ed.putBoolean(Const.FILE_NAME, true);
                ed.putString(getString(R.string.Account_name), accName);
                ed.commit();
                logIn();
                break;
            } else {
                invilidAccount = true;
            }
        }
        if (invilidAccount || (accounts.length == 0)) {
            ArrayList<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
            data.add(new BasicNameValuePair(Const.ACT, "Authentication"));
            data.add(new BasicNameValuePair(Const.PHONE, accName));
            data.add(new BasicNameValuePair(Const.PASSWORD, password));
            DataManager.call(data, context, new DataManager.Callback<List>() {
                @Override
                public void done(List s) {
                    List<Bundle> list = s;
                    Bundle data = list.get(0);
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putBoolean(Const.FILE_NAME, true);
                    ed.putString(getString(R.string.Account_name), accName);
                    ed.commit();
                    String token = data.getString(Const.TOKEN);
                    Account account = new Account(accName, getString(R.string.accountType));
                    accountManager.addAccountExplicitly(account, password, data);
                    accountManager.setAuthToken(account, account.type, token);
                    Log.i("qwer", token);
                    setAccountAuthenticatorResult(data);
                    logIn();
                }
                @Override
                public void error(Exception e) {
                    loadEnd();
                    String language = Localization.language(context1);
                    if(language.equals("Беларускі"))
                        Toast.makeText(LoginActivity.this, getString(R.string.error_bel_login), Toast.LENGTH_LONG).show();
                    else if (language.equals("Русский"))
                        Toast.makeText(LoginActivity.this, getString(R.string.error_rus_login), Toast.LENGTH_LONG).show();
                    else
                    Toast.makeText(LoginActivity.this, getString(R.string.error_en_login), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void logIn() {
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
    public void onSingUp(View v) {
        startActivity(new Intent(this, SingUp.class));
    }
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_GO) {
            onStart(v);
            return true;
        }
        return false;
    }
}
