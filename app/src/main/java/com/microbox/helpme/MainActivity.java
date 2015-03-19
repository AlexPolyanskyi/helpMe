package com.microbox.helpme;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;

import com.google.android.gms.maps.model.LatLng;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.fragments.HelpFragment;
import com.microbox.helpme.fragments.MapsFragment;
import com.microbox.helpme.fragments.MoreFragment;
import com.microbox.helpme.fragments.NewsFragment;
import com.microbox.helpme.fragments.ProfFragment;
import com.microbox.helpme.fragments.SettingsFragment;
import com.microbox.helpme.manager.MyAccountManager;

public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, HelpFragment.HelpCallback, NewsFragment.NewsCallback, MoreFragment.ProfCallback, MoreFragment.showCallback, MapsFragment.ReturnCallback {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    public  CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity2);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        getActionBar().setIcon(R.drawable.ic_logo);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        switch (position) {
            case 0:
                ProfFragment.setMyProff(true, null);
                fragmentManager.replace(R.id.container, ProfFragment.newInstance(position));
                break;
            case 1:
                fragmentManager.replace(R.id.container, HelpFragment.newInstance(position));
                break;
            case 2:

                fragmentManager.replace(R.id.container, MapsFragment.newInstance(position, null, null, null));
                break;
            case 3:
                fragmentManager.replace(R.id.container, NewsFragment.newInstance(position));
                break;
            case 4:
                fragmentManager.replace(R.id.container, SettingsFragment.newInstance(position));
                break;
            case 5:
                Context context = this.getApplicationContext();
                SharedPreferences pref = context.getSharedPreferences(Const.FILE_NAME, MODE_PRIVATE);
                SharedPreferences.Editor ed = pref.edit();
                ed.putBoolean(Const.FILE_NAME, false);
                ed.commit();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
        }
        fragmentManager.commit();
    }

    public void onSectionAttached(int number) {
        Context context = this.getApplicationContext();
        String language = Localization.language(context);
        if (language.equals("2")) {
            switch (number) {
                case 0:
                    mTitle = getString(R.string.prof_bel);
                    break;
                case 1:
                    mTitle = getString(R.string.help_bel);
                    break;
                case 2:
                    mTitle = getString(R.string.map_bel);
                    break;
                case 3:
                    mTitle = getString(R.string.news_bel);
                    break;
                case 4:
                    mTitle = getString(R.string.settings_bel);
                    break;
                case 5:
                    mTitle = MoreFragment.title;
                    break;
            }
        } else if (language.equals("0")) {
            switch (number) {
                case 0:
                    mTitle = getString(R.string.prof_rus);
                    break;
                case 1:
                    mTitle = getString(R.string.help_rus);
                    break;
                case 2:
                    mTitle = getString(R.string.map_rus);
                    break;
                case 3:
                    mTitle = getString(R.string.news_rus);
                    break;
                case 4:
                    mTitle = getString(R.string.settings_rus);
                    break;
                case 5:
                    mTitle = MoreFragment.title;
                    break;
            }
        } else if (language.equals("1")) {
            switch (number) {
                case 0:
                    mTitle = getString(R.string.prof_en);
                    break;
                case 1:
                    mTitle = getString(R.string.help_en);
                    break;
                case 2:
                    mTitle = getString(R.string.map_en);
                    break;
                case 3:
                    mTitle = getString(R.string.news_en);
                    break;
                case 4:
                    mTitle = getString(R.string.settings_en);
                    break;
                case 5:
                    mTitle = MoreFragment.title;
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public void onHelpSend() {
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.container, NewsFragment.newInstance(3));
        fragmentManager.commit();
    }

@Override
    public void more(String ticketID, String title) {
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.container, MoreFragment.newInstance(5, ticketID, title));
        fragmentManager.commit();
    }

    @Override
    public void onProfRedirect(String id) {
        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] a = accountManager.getAccountsByType(getString(R.string.accountType));
        Account account = MyAccountManager.getAccount(a, getApplicationContext());
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        if(id.equals(accountManager.getUserData(account, Const.USER_ID))){
            ProfFragment.setMyProff(true, id);
        }else{
            ProfFragment.setMyProff(false, id);
        }
        fragmentManager.replace(R.id.container, ProfFragment.newInstance(0));
        fragmentManager.commit();
    }

    @Override
    public void showInMap(LatLng latLng, String title, String ticketID) {
        FragmentTransaction fragmentManager = getFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.container, MapsFragment.newInstance(2, latLng, title, ticketID));
        fragmentManager.commit();
    }
}
