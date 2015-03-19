package com.microbox.helpme.manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.utils.Parser;

import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 05.11.2014.
 */
public class DataManager  {
    private final static String ERROR = "error";
    private static Exception error = null;
    public static void call(ArrayList<BasicNameValuePair> params, Context c, final Callback callback) {
        final Context context = c;
        new AsyncTask<ArrayList, String, String>() {
            @Override
            protected String doInBackground(ArrayList... params) {
                String result, authToken, userID;
                try {
                    AccountManager accountManager = AccountManager.get(context);
                    Account[] a = accountManager.getAccountsByType(context.getString(R.string.accountType));
                    Account account = MyAccountManager.getAccount(a, context);
                    userID = accountManager.getUserData(account, Const.USER_ID);
                    AccountManagerFuture<Bundle> acc = accountManager.getAuthToken(account, context.getString(R.string.accountType), null, null,
                            null, null);
                    Bundle authTokenBundle = acc.getResult();
                    authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();
                } catch (Exception e) {
                    authToken = "empty";
                    userID = "empty";
                }
                try {
                    DefaultHttpClient hc = new DefaultHttpClient();
                    ResponseHandler<String> res = new BasicResponseHandler();
                    HttpPost postMethod = new HttpPost(Const.SERVER_URL);
                    List<NameValuePair> nameValuePairs = params[0];
                    nameValuePairs.add(new BasicNameValuePair(Const.TOKEN, authToken));
                    nameValuePairs.add(new BasicNameValuePair(Const.USER_ID, userID));
                    postMethod.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
                    result = hc.execute(postMethod, res);
                } catch (Exception e) {
                    result = ERROR;
                    error = e;
                }
                return result;
            }
            @Override
            protected void onPostExecute(String result) {
                List res = null;
                Log.i("qwer", result);
                try {
                    JSONObject status = new JSONObject(result);
                    if (status.getInt("status") == 404 || result.equals(ERROR)) {
                        new Exception("Server not found");
                        callback.error(error);
                    } else {
                        res = Parser.parse(result);
                        callback.done(res);
                    }
                } catch (Exception e) {
                    callback.error(new Exception(e));
                }
            }
        }.execute(params);
    }

    public static interface Callback<List> {
        public void done(List result);
        public void error(Exception e);
    }
}
