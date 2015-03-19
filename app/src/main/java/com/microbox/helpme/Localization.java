package com.microbox.helpme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.MyAccountManager;

/**
 * Created by user on 24.11.2014.
 */
public class Localization {

    public static String language(Context context) {
        String language;
        try {
            AccountManager accountManager = AccountManager.get(context);
            Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.accountType));
            final Account account = MyAccountManager.getAccount(accounts, context);
            language = accountManager.getUserData(account, Const.LANG);
        }catch (Exception e){
            language = "English";
        }
        return language;
    }
}
