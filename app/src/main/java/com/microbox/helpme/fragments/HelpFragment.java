package com.microbox.helpme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.microbox.helpme.Localization;
import com.microbox.helpme.MainActivity;
import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by user on 03.11.2014.
 */
public class HelpFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static View rootView;
    private LocationClient locationClient;
    private String longitude, latitude;
    private EditText title, message;
    private HelpCallback mCallbacks;

    public HelpFragment() {
    }

    public static HelpFragment newInstance(int sectionNumber) {
        HelpFragment fragment = new HelpFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static void loadStart() {
        rootView.findViewById(R.id.send_help).setVisibility(View.GONE);
    }

    public static void loadStop() {
        rootView.findViewById(R.id.send_help).setVisibility(View.VISIBLE);
    }

    public static void loadError() {
        rootView.findViewById(R.id.message_help).setVisibility(View.GONE);
        rootView.findViewById(R.id.title_help).setVisibility(View.GONE);
        rootView.findViewById(R.id.send_help).setVisibility(View.GONE);
        rootView.findViewById(R.id.error_message_help).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_help_activity, container, false);
        final int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        loadStart();
        Context context = this.getActivity().getApplicationContext();
        final String language = Localization.language(context);
        if (result != ConnectionResult.SUCCESS) {
            if(language.equals("Беларускі"))
            Toast.makeText(getActivity(), getString(R.string.help_be), Toast.LENGTH_LONG).show();
            else if (language.equals("Русский"))
            Toast.makeText(getActivity(), getString(R.string.help_rus), Toast.LENGTH_LONG).show();
            else Toast.makeText(getActivity(), getString(R.string.help_eng), Toast.LENGTH_LONG).show();
            loadError();
        }
        locationClient = new LocationClient(getActivity(), this, this);
        title = (EditText) rootView.findViewById(R.id.title_help);
        message = (EditText) rootView.findViewById(R.id.message_help);
        rootView.findViewById(R.id.send_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadStart();
                String data = title.getText().toString();
                if (data.length() < 5) {
                    if (language.equals("Беларускі"))
                    Toast.makeText(getActivity(), getString(R.string.help_bel_header), Toast.LENGTH_LONG).show();
                    else if (language.equals("Русский"))
                    Toast.makeText(getActivity(), getString(R.string.help_rus_header), Toast.LENGTH_LONG).show();
                    else Toast.makeText(getActivity(), getString(R.string.help_en_header), Toast.LENGTH_LONG).show();
                    title.requestFocus();
                } else {
                    ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(Const.ACT, "NewTicket"));
                    params.add(new BasicNameValuePair(Const.TITLE, title.getText().toString()));
                    params.add(new BasicNameValuePair(Const.MESSAGE, message.getText().toString()));
                    params.add(new BasicNameValuePair(Const.GEO_X, latitude));
                    params.add(new BasicNameValuePair(Const.GEO_Y, longitude));
                    DataManager.call(params, getActivity().getApplicationContext(), new DataManager.Callback() {
                        @Override
                        public void done(Object result) {
                            mCallbacks.onHelpSend();
                        }

                        @Override
                        public void error(Exception e) {
                            Toast.makeText(getActivity(), getString(R.string.error_en), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (HelpCallback) activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();
        locationClient.connect();
        loadStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        locationClient.disconnect();
    }

    @Override
    public void onConnected(Bundle arg0) {
        Location loc = locationClient.getLastLocation();
        try {
            latitude = Double.toString(loc.getLatitude());
            longitude = Double.toString(loc.getLongitude());
            loadStop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Context context = this.getActivity().getApplicationContext();
        String language = Localization.language(context);
        if (language.equals("Беларускі"))
            Toast.makeText(getActivity(), getString(R.string.help_bel_error), Toast.LENGTH_LONG).show();
        else if (language.equals("Русский"))
            Toast.makeText(getActivity(), getString(R.string.help_rus_error), Toast.LENGTH_LONG).show();
        else Toast.makeText(getActivity(), getString(R.string.help_en_error), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(getActivity(), getString(R.string.help_disconnected), Toast.LENGTH_LONG).show();
    }

    public static interface HelpCallback {
        void onHelpSend();
    }

}
