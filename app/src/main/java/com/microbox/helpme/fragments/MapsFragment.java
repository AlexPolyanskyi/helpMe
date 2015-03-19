package com.microbox.helpme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microbox.helpme.MainActivity;
import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 03.11.2014.
 */
public class MapsFragment extends Fragment implements DataManager.Callback<List> {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GoogleMap map;
    private MapView mapView;
    private static LatLng latLng;
    private static String title;
    private ReturnCallback mCallbacks;
    private static String ticketID;
    private static boolean isLoading = false;
    private View rootView;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_activity, container, false);
        rootView.findViewById(R.id.map).setVisibility(View.INVISIBLE);
        rootView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        isLoading = true;
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
        switch (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) )
        {
            case ConnectionResult.SUCCESS:
                mapView = (MapView) rootView.findViewById(R.id.map);
                mapView.onCreate(savedInstanceState);
                if(mapView!=null)
                {
                    map = mapView.getMap();
                    map.getUiSettings().setMyLocationButtonEnabled(true);
                    map.setMyLocationEnabled(true);
                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            String lId = marker.getTitle().substring(11);
                            String lTitle = marker.getSnippet();
                            Log.i("12345", lId +"  "+ lTitle);
                            mCallbacks.more(lId, lTitle);
                        }
                    });
                }
                break;
            case ConnectionResult.SERVICE_MISSING:
                Toast.makeText(getActivity(), "SERVICE MISSING", Toast.LENGTH_SHORT).show();
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                Toast.makeText(getActivity(), "UPDATE REQUIRED", Toast.LENGTH_SHORT).show();
                break;
            default: Toast.makeText(getActivity(), GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()), Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (ReturnCallback) activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }
    public static MapsFragment newInstance(int sectionNumber, LatLng lLng, String t, String id) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        latLng = lLng;
        title = t;
        ticketID = id;
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        try {
            mapView.onResume();
            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                @Override
                public void onMyLocationChange(Location location) {
                    if (latLng == null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                        params.add(new BasicNameValuePair(Const.ACT, "Maps"));
                        DataManager.call(params, getActivity().getApplicationContext(), MapsFragment.this);
                    } else {
                        Marker marker = map.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Help Me!!!#" + ticketID)
                                .snippet(title));
                        marker.showInfoWindow();
                        loadStop();
                        zoom();
                    }

                }
            });
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
    }
    public void zoom(){
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.animateCamera(cameraUpdate);
        map.setOnMyLocationChangeListener(null);
        latLng = null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mapView.onDestroy();
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        try {
            mapView.onPause();
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        try {
            mapView.onLowMemory();
        } catch (Exception e) {
            Log.e("Address Map", "Could not initialize google play", e);
        }
    }

    @Override
    public void done(List result) {
        Marker marker;
        for(int i = 0; i < result.size(); i++){
            marker = map.addMarker((MarkerOptions)result.get(i));
            marker.showInfoWindow();
        }
        loadStop();
        zoom();
    }

    @Override
    public void error(Exception e) {
        loadStop();
    }

    public static interface ReturnCallback {
        void more(String ticketID, String title);
    }
    public void loadStop(){
        if(isLoading){
            rootView.findViewById(R.id.map).setVisibility(View.VISIBLE);
            rootView.findViewById(android.R.id.progress).setVisibility(View.INVISIBLE);
            isLoading = false;
        }
    }
}