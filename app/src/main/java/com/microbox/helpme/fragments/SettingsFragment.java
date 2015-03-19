package com.microbox.helpme.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microbox.helpme.Localization;
import com.microbox.helpme.MainActivity;
import com.microbox.helpme.R;
import com.microbox.helpme.StartActivity;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;
import com.microbox.helpme.manager.MyAccountManager;

import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 03.11.2014.
 */
public class SettingsFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private View rootView;
    private String language = "nan";
    private String city = "nan", country = "nan", cityID;
    private Context context;
    static final int GALLERY_REQUEST = 1;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(int sectionNumber) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings_activity, container, false);
        loadStart();
        try {
            context = getActivity().getApplicationContext();
            final AccountManager accountManager = AccountManager.get(context);
            Account[] a = accountManager.getAccountsByType(getString(R.string.accountType));
            final Account account = MyAccountManager.getAccount(a, context);
            final String lan = Localization.language(context);
            city = accountManager.getUserData(account, Const.CITY);
            country = accountManager.getUserData(account, Const.COUNTRY);
            language = accountManager.getUserData(account, Const.LANG);
            getCountry();
            String data = accountManager.getUserData(account, Const.FIRST_NAME);
            ((TextView) rootView.findViewById(R.id.first)).setText(data);
            data = accountManager.getUserData(account, Const.LAST_NAME);
            //TODO NATASHE!!!!
            TextView textView = ((TextView) rootView.findViewById(R.id.last));
            textView.setText(data);
            //textView.setHint("gasgas");
            ((TextView) rootView.findViewById(R.id.email)).setText(account.name);
            Spinner spinnerLanguage = (Spinner) rootView.findViewById(R.id.langue);
            ArrayAdapter adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.spinner_item, Const.dataLang);
            spinnerLanguage.setAdapter(adapter);
            spinnerLanguage.setSelection(Integer.parseInt(language));
            spinnerLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    if (position >= 0) {
                        language = Integer.toString(position);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
            Button mButtonSend = (Button) rootView.findViewById(R.id.set_photo);
            mButtonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                }
            });

            Button mButtonSave = (Button) rootView.findViewById(R.id.save);
            mButtonSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText fName = (EditText) rootView.findViewById(R.id.first);
                    final EditText lName = (EditText) rootView.findViewById(R.id.last);
                    final EditText password = (EditText) rootView.findViewById(R.id.password);
                    final EditText comfPassword = (EditText) rootView.findViewById(R.id.password_repit);
                    String first = fName.getText().toString();
                    String last = lName.getText().toString();
                    if ( (first.length() >= 3 && !(first.indexOf(" ") > 0 )) && (last.length() >= 3 && !(last.indexOf(" ") > 0 )) ) {
                        loadStart();
                        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(7);
                        params.add(new BasicNameValuePair(Const.ACT, "Settings"));
                        params.add(new BasicNameValuePair(Const.FIRST_NAME, fName.getText().toString()));
                        params.add(new BasicNameValuePair(Const.LAST_NAME, lName.getText().toString()));
                        params.add(new BasicNameValuePair(Const.COUNTRY, country));
                        params.add(new BasicNameValuePair(Const.CITY, cityID));
                        params.add(new BasicNameValuePair(Const.LANG, language));
                        params.add(new BasicNameValuePair(Const.SAVE, "1"));
                        final String pass = password.getText().toString();
                        final String comfPass = comfPassword.getText().toString();
                        if (!pass.equals(new String()) && pass.equals(comfPass) && pass.length() >= 6) {
                            params.add(new BasicNameValuePair(Const.PASSWORD, pass));
                            params.add(new BasicNameValuePair(Const.PASSWORD_COMF, comfPass));
                        }

                        new DataManager().call(params, context, new DataManager.Callback() {
                            @Override
                            public void done(Object result) {
                                if (!pass.equals(new String()) && pass.equals(comfPass) && pass.length() >= 6) {
                                    accountManager.clearPassword(account);
                                    accountManager.setPassword(account, password.getText().toString());
                                }
                                accountManager.setUserData(account, Const.FIRST_NAME, fName.getText().toString());
                                accountManager.setUserData(account, Const.LAST_NAME, lName.getText().toString());
                                accountManager.setUserData(account, Const.COUNTRY, country);
                                accountManager.setUserData(account, Const.CITY, cityID);
                                accountManager.setUserData(account, Const.LANG, language);
                                loadEnd();
                                if (lan.equals("Беларускі"))
                                    Toast.makeText(getActivity(), getString(R.string.setting_bel), Toast.LENGTH_LONG).show();
                                else if (lan.equals("Русский"))
                                    Toast.makeText(getActivity(), getString(R.string.setting_rus), Toast.LENGTH_LONG).show();
                                else Toast.makeText(getActivity(), getString(R.string.settings_en), Toast.LENGTH_LONG).show();
                                getActivity().recreate();
                            }

                            @Override
                            public void error(Exception e) {
                                loadEnd();
                                Toast.makeText(context, getString(R.string.error_en) + e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            SharedPreferences pref = getActivity().getSharedPreferences(Const.FILE_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean(Const.FILE_NAME, false);
            ed.commit();
            getActivity().finish();
            startActivity(new Intent(getActivity(), StartActivity.class));
        }
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap galleryPic = null;
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == getActivity().RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        galleryPic = MediaStore.Images.Media.getBitmap(context.getContentResolver(), selectedImage);
                        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(5);
                        params.add(new BasicNameValuePair(Const.ACT, "Settings"));
                        params.add(new BasicNameValuePair(Const.SAVE, "1"));
                        ByteArrayOutputStream bao = new ByteArrayOutputStream();
                        Bitmap bitmap = galleryPic;
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, bao);
                        byte[] ba = bao.toByteArray();
                        String ba1 = Base64.encodeToString(ba, Base64.DEFAULT);
                        params.add(new BasicNameValuePair(Const.AVATAR, ba1));
                        new DataManager().call(params, context, new DataManager.Callback() {

                            @Override
                            public void done(Object result) {

                            }

                            @Override
                            public void error(Exception e) {

                            }
                        });

                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
        }
    }

    public void loadStart() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.layout).setVisibility(View.GONE);
    }

    public void loadEnd() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.layout).setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }

    public void fillCountrySpinner(ArrayList<String> dataCountry) {
        Spinner spinnerCountry = (Spinner) rootView.findViewById(R.id.country);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, dataCountry);
        spinnerCountry.setAdapter(adapter);
        int position = Integer.parseInt(country) - 1;
        spinnerCountry.setSelection(position);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position >= 0) {
                    country = Integer.toString(position + 1);
                    rootView.findViewById(R.id.city).setVisibility(View.VISIBLE);
                    getCity(position + 1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void getCountry() {
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(1);
        params.add(new BasicNameValuePair(Const.ACT, "getCountries"));
        new DataManager().call(params, context, new DataManager.Callback<List>() {
            @Override
            public void done(List result) {
                List<ArrayList<String>> list = result;
                ArrayList<String> data = list.get(0);
                fillCountrySpinner(data);
                int position = Integer.parseInt(country);
                getCity(position + 1);
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(context, "Error" + e, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void getCity(int position) {
        loadStart();
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "getCities"));
        params.add(new BasicNameValuePair(Const.COUNTRY, Integer.toString(position)));
        new DataManager().call(params, context, new DataManager.Callback<List>() {
            @Override
            public void done(List result) {
                List<ArrayList<String>> list = result;
                ArrayList<String> data = list.get(0);
                ArrayList<String> id = list.get(1);
                fillCitySpinner(data, id);
                loadEnd();
            }

            @Override
            public void error(Exception e) {
                Toast.makeText(context, getString(R.string.error_en), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void fillCitySpinner(ArrayList<String> dataCity, final ArrayList<String> idCity) {
        Spinner spinnerCity = (Spinner) rootView.findViewById(R.id.city);
        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.spinner_item, dataCity);
        spinnerCity.setAdapter(adapter);
        int position = 0;
        for (String i : idCity) {
            position++;
            if (i.equals(city)) {
                spinnerCity.setSelection(position - 1);
                break;
            }
        }
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if (position >= 0) {
                    cityID = idCity.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }
}