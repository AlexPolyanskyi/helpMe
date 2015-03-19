package com.microbox.helpme;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microbox.helpme.adapter.NothingSelectedSpinnerAdapter;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 04.11.2014.
 */
public class SingUp extends AccountAuthenticatorActivity implements DataManager.Callback<List> {

    private ArrayList<String> dataCountry = new ArrayList<String>();
    private ArrayList<String> dataCity = new ArrayList<String>();
    private String dataJSON = null;

    private boolean isCountrySelect = false;

    private String city = "null";
    private String language = "null";
    private String country = "null";
    private String firstName;
    private String lastName;
    private String password;
    private String passwordR;
    private String phone;

    private Spinner spinnerLanguage, spinnerCity, spinnerCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);
        getActionBar().setIcon(R.drawable.ic_logo);
        spinnerLanguage = (Spinner) findViewById(R.id.langue);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, Const.dataLang);
        spinnerLanguage.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected_lang,
                        this));
        spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    language = Integer.toString(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        loadStart();
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add(new BasicNameValuePair(Const.ACT, "getCountries"));
        new DataManager().call(params, this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sing_up, menu);
        return true;
    }

    public void loadStart() {
        findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.layout).setVisibility(View.GONE);
    }

    public void loadEnd() {
        findViewById(android.R.id.progress).setVisibility(View.GONE);
        findViewById(R.id.layout).setVisibility(View.VISIBLE);
    }

    public boolean validData() {
        //НАТАША СДЕЛАЙ КОНСТАНТЫ В ТОСТЫ С НОРМАЛЬНЫМИ МЕССАДЖАМИ :)
        TextView t = (TextView) findViewById(R.id.first_name);
        firstName = t.getText().toString();
        if (firstName.length() < 2) {
            Toast.makeText(this, getString(R.string.not_found_login), Toast.LENGTH_LONG).show();
            t.requestFocus();
            return false;
        }
        t = (TextView) findViewById(R.id.last_name);
        lastName = t.getText().toString();
        if (lastName.length() < 2) {
            Toast.makeText(this, "Last name", Toast.LENGTH_LONG).show();
            t.requestFocus();
            return false;
        }
        t = (TextView) findViewById(R.id.password);
        password = t.getText().toString();
        if (password.length() < 6) {
            Toast.makeText(this, "Password", Toast.LENGTH_LONG).show();
            t.requestFocus();
            return false;
        }
        t = (TextView) findViewById(R.id.password_repit);
        passwordR = t.getText().toString();
        if (!passwordR.equals(password)) {
            Toast.makeText(this, "Password Password repeat", Toast.LENGTH_LONG).show();
            t.requestFocus();
            return false;
        }
        t = (TextView) findViewById(R.id.phone);
        phone = t.getText().toString();
        if (phone.length() < 12) {
            Toast.makeText(this, "Email", Toast.LENGTH_LONG).show();
            t.requestFocus();
            return false;
        }
        if (country.equals("null")) {
            Toast.makeText(this, "Country", Toast.LENGTH_LONG).show();
            spinnerCountry.setFocusableInTouchMode(true);
            spinnerCountry.setFocusable(true);
            spinnerCountry.requestFocus();
            return false;
        }
        if (city.equals("null")) {
            Toast.makeText(this, "City", Toast.LENGTH_LONG).show();
            spinnerCity.setFocusableInTouchMode(true);
            spinnerCity.setFocusable(true);
            spinnerCity.requestFocus();
            return false;
        }
        if (language.equals("null")) {
            Toast.makeText(this, "Language", Toast.LENGTH_LONG).show();
            spinnerLanguage.setFocusableInTouchMode(true);
            spinnerLanguage.setFocusable(true);
            spinnerLanguage.requestFocus();
            return false;
        }
        return true;
    }
    public boolean next(){
        if (!validData()) {
            return false;
        }
        loadStart();
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "Registration"));
        params.add(new BasicNameValuePair("step", "1"));
        params.add(new BasicNameValuePair(Const.FIRST_NAME, firstName));
        params.add(new BasicNameValuePair(Const.LAST_NAME, lastName));
        params.add(new BasicNameValuePair(Const.PASSWORD, password));
        params.add(new BasicNameValuePair(Const.PASSWORD_COMF, passwordR));
        params.add(new BasicNameValuePair(Const.COUNTRY, country));
        params.add(new BasicNameValuePair(Const.CITY, city));
        params.add(new BasicNameValuePair(Const.LANG, language));
        params.add(new BasicNameValuePair(Const.PHONE, phone));
        new DataManager().call(params, this, new DataManager.Callback<List>() {
            @Override
            public void done(List s) {
                List<String> list = s;
                String userID = list.get(0);
                String token = list.get(1);
                Context context = SingUp.this.getApplicationContext();
                AccountManager accountManager = AccountManager.get(context);
                Account account = new Account(phone, getString(R.string.accountType));
                Bundle data = new Bundle();
                data.putString(Const.USER_ID, userID);
                data.putString(Const.FIRST_NAME, firstName);
                data.putString(Const.LAST_NAME, lastName);
                data.putString(Const.LANG, language);
                data.putString(Const.CITY, city);
                data.putString(Const.COUNTRY, country);
                if (accountManager.addAccountExplicitly(account, password, data)) {
                    accountManager.setAuthToken(account, account.type, token);
                    SharedPreferences pref = getSharedPreferences(Const.FILE_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor ed = pref.edit();
                    ed.putBoolean(Const.FILE_NAME, true);
                    ed.putString(getString(R.string.Account_name), phone);
                    ed.commit();
                    setAccountAuthenticatorResult(data);
                    Intent intent = new Intent(SingUp.this, PhoneActivity.class);
                    intent.putExtra(Const.PHONE, phone);
                    startActivity(intent);
                    finish();
                } else {
                    loadEnd();
                    Toast.makeText(SingUp.this, "Данный аккаунт уже сущ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void error(Exception e) {
                loadEnd();
            }
        });
        return true;
    }
    public void onNextClick(View v){
        next();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_example) {
            next();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void done(List s) {
        loadEnd();
        List<ArrayList<String>> list = s;
        ArrayList<String> dataList = list.get(0);
        ArrayList<String> id = list.get(1);
        dataCity = dataList;
        fillCitySpinner(id);
        if (!isCountrySelect) {
            dataCountry = dataList;
            fillCountrySpinner();
        }
    }

    public void fillCountrySpinner() {
        spinnerCountry = (Spinner) findViewById(R.id.country);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, dataCountry);
        spinnerCountry.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected_country,
                        this));
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    country = Integer.toString(position);
                    isCountrySelect = true;
                    findViewById(R.id.city).setVisibility(View.VISIBLE);
                    ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(Const.ACT, "getCities"));
                    params.add(new BasicNameValuePair(Const.COUNTRY, position + ""));
                    new DataManager().call(params, SingUp.this, SingUp.this);
                    loadStart();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void fillCitySpinner(final ArrayList<String> idCity) {
        spinnerCity = (Spinner) findViewById(R.id.city);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.spinner_item, dataCity);
        spinnerCity.setAdapter(
                new NothingSelectedSpinnerAdapter(
                        adapter,
                        R.layout.contact_spinner_row_nothing_selected_city,
                        this));
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position > 0) {
                    city = idCity.get(position - 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    @Override
    public void error(Exception e) {
        loadEnd();
        Log.i("1", "1234" + e);
        //TODO write handler to error
    }
}
