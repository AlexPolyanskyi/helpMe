package com.microbox.helpme.utils;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.fragments.MapsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by user on 10.11.2014.
 */
public class Parser {
    public static List parse(String json) throws JSONException {
        List result = null;
        JSONObject done = null;
        Log.i("result", json);
        JSONObject res = new JSONObject(json);
        String act = res.getString(Const.ACT);
        if ((act.equals("getCities")) || (act.equals("getCountries"))) {
            result = new ArrayList<ArrayList<String>>();
            ArrayList<String> data = new ArrayList<String>();
            ArrayList<String> id = new ArrayList<String>();
            result.add(data);
            result.add(id);
            JSONArray d = res.getJSONArray(Const.DONE);
            for (int i = 0; i < d.length(); i++) {
                JSONObject obj = d.getJSONObject(i);
                data.add(obj.getString("title"));
                id.add(obj.getString("id"));
            }
        } else if (act.equals("Registration")) {
            result = new ArrayList<String>();
            done = res.getJSONObject(Const.DONE);
            result.add(done.getString(Const.USER_ID));
            result.add(done.getString(Const.TOKEN));
            result.add(done.getString("step"));
        } else if (act.equals("Authentication")) {
            result = new ArrayList<Bundle>();
            Bundle b = new Bundle();
            done = res.getJSONObject(Const.DONE);
            b.putString(Const.USER_ID, done.getString(Const.USER_ID));
            b.putString(Const.TOKEN, done.getString(Const.TOKEN));
            b.putString(Const.FIRST_NAME, done.getString(Const.FIRST_NAME));
            b.putString(Const.LAST_NAME, done.getString(Const.LAST_NAME));
            b.putString(Const.PHONE, done.getString(Const.PHONE));
            b.putString(Const.AVATAR, done.getString(Const.AVATAR));
            b.putString(Const.P_CONFIM, done.getString(Const.P_CONFIM));
            b.putString(Const.COUNTRY, done.getString(Const.COUNTRY));
            b.putString(Const.CITY, done.getString(Const.CITY));
            b.putString(Const.LANG, done.getString(Const.LANG));
            result.add(b);
        } else if (act.equals("Account") || act.equals("Reviews")) {
            result = new ArrayList<ArrayList<String>>(2);
            done = res.getJSONObject(Const.DONE);
            ArrayList<String> dataMap = new ArrayList<String>(7);
            dataMap.add(0, done.getString(Const.USER_ID));
            dataMap.add(1, done.getString(Const.FULL_NAME));
            dataMap.add(2, done.getString(Const.AVATAR));
            dataMap.add(3, done.getString(Const.THANKS));
            dataMap.add(4, done.getString(Const.PHONE));
            dataMap.add(5, done.getString(Const.COUNTRY));
            dataMap.add(6, done.getString(Const.CITY));
            dataMap.add(7, done.getString(Const.ASKED));
            result.add(dataMap);
            ArrayList<HashMap<String, String>> messageData = new ArrayList<HashMap<String, String>>();
            JSONArray doneArr = done.getJSONArray("reviews");
            HashMap<String, String> dataMessage;
            for(int i = 0; i < doneArr.length(); i++){
                dataMessage = new HashMap<String, String>();
                done = doneArr.getJSONObject(i);
                dataMessage.put(Const.MESSAGE, done.getString(Const.MESSAGE));
                dataMessage.put(Const.USER_ID, done.getString(Const.USER_ID));
                dataMessage.put(Const.DATA, usingDateFormatterWithTimeZone(Integer.parseInt(done.getString(Const.DATA))));
                dataMessage.put(Const.FULL_NAME, done.getString(Const.FULL_NAME));
                dataMessage.put(Const.AVATAR, done.getString(Const.AVATAR));
                messageData.add(dataMessage);
            }
            result.add(messageData);
        }else if(act.equals("AllTicket")) {
            JSONArray d = res.getJSONArray(Const.DONE);
            result = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> dataMessage;
            for (int i = 0; i < d.length(); i++) {
                dataMessage = new HashMap<String, String>();
                done = d.getJSONObject(i);
                dataMessage.put(Const.ID, done.getString(Const.ID));
                dataMessage.put(Const.TITLE, done.getString(Const.TITLE));
                dataMessage.put(Const.DATA, usingDateFormatterWithTimeZone(Integer.parseInt(done.getString(Const.DATA))));
                dataMessage.put(Const.FULL_NAME, done.getString(Const.FULL_NAME));
                dataMessage.put(Const.AVATAR, done.getString(Const.AVATAR));
                result.add(dataMessage);
            }
        }
        else if(act.equals("InfoTicket")  || act.equals("Comments")) {
            result = new ArrayList<Object>(2);
            done = res.getJSONObject(Const.DONE);
            ArrayList<String> dataMap = new ArrayList<String>(7);
            dataMap.add(0, done.getString(Const.USER_ID));
            dataMap.add(1, done.getString(Const.TITLE));
            dataMap.add(2, done.getString(Const.MESSAGE));
            dataMap.add(3, done.getString(Const.GEO_X));
            dataMap.add(4, done.getString(Const.GEO_Y));
            dataMap.add(5, usingDateFormatterWithTimeZone(Integer.parseInt(done.getString(Const.DATA))));
            dataMap.add(6, done.getString(Const.FULL_NAME));
            dataMap.add(7, done.getString(Const.AVATAR));
            result.add(dataMap);
            ArrayList<HashMap<String, String>> messageData = new ArrayList<HashMap<String, String>>();
            JSONArray doneArr = done.getJSONArray("comments");
            HashMap<String, String> dataMessage;
            for(int i = 0; i < doneArr.length(); i++){
                dataMessage = new HashMap<String, String>();
                done = doneArr.getJSONObject(i);
                dataMessage.put(Const.MESSAGE, done.getString(Const.MESSAGE));
                dataMessage.put(Const.USER_ID, done.getString(Const.USER_ID));
                dataMessage.put(Const.DATA, usingDateFormatterWithTimeZone(Integer.parseInt(done.getString(Const.DATA))));
                dataMessage.put(Const.FULL_NAME, done.getString(Const.FULL_NAME));
                dataMessage.put(Const.AVATAR, done.getString(Const.AVATAR));
                messageData.add(dataMessage);
            }
            result.add(messageData);
        }else if(act.equals("Maps")){
            JSONArray d = res.getJSONArray(Const.DONE);
            result = new ArrayList<MarkerOptions>();
            MarkerOptions markerOptions;
            for(int i = 0; i < d.length(); i++) {
                markerOptions = new MarkerOptions().position(new LatLng(d.getJSONObject(i).getDouble(Const.GEO_X), d.getJSONObject(i).getDouble(Const.GEO_Y)))
                                                                 .title("Help Me!!!#"+d.getJSONObject(i).getString(Const.ID))
                                                                 .snippet(d.getJSONObject(i).getString(Const.TITLE));
                result.add(markerOptions);
            }

        }
        return result;
    }
    private static String usingDateFormatterWithTimeZone(long input){
        input*=1000;
        Date date = new Date(input);
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yy");
        sdf.setCalendar(cal);
        cal.setTime(date);
        return sdf.format(date);
    }
}
