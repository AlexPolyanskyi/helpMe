package com.microbox.helpme.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;

import java.util.ArrayList;

/**
 * Created by user on 10.11.2014.
 */
public class MyAccountManager {

    private static SharedPreferences pref;

    public static Account getAccount(Account[] a, Context context) {
        pref = context.getSharedPreferences(Const.FILE_NAME, context.MODE_PRIVATE);
        String name = pref.getString(context.getString(R.string.Account_name), "none");
        for (Account acc : a) {
            if (acc.name.equals(name))
                return acc;
        }
        return null;
    }

    public static void syncAccount(final ArrayList<String> profDataMap, final Account account, final AccountManager accountManager){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fullName = profDataMap.get(1);
                String[] fAndLName = fullName.split(" ");
                accountManager.setUserData(account, Const.FIRST_NAME, fAndLName[0]);
                accountManager.setUserData(account, Const.LAST_NAME, fAndLName[1]);
                accountManager.setUserData(account, Const.AVATAR, profDataMap.get(2));
                accountManager.setUserData(account, Const.THANKS, profDataMap.get(3));
                accountManager.setUserData(account, Const.ASKED, profDataMap.get(7));
                Log.i("qwer", accountManager.getUserData(account, Const.ASKED));
            }
        }).start();

    }
}
