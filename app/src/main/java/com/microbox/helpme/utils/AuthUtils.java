package com.microbox.helpme.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.microbox.helpme.constants.Const;


public class AuthUtils {

    private static boolean IS_AUTHORIZED = false;
    private static SharedPreferences pref;

    public static boolean isLogged(Context context) {
        pref = context.getSharedPreferences(Const.FILE_NAME, context.MODE_PRIVATE);
        IS_AUTHORIZED = pref.getBoolean(Const.FILE_NAME, false);
        return IS_AUTHORIZED;
    }
}
