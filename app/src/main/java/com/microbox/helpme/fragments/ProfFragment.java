package com.microbox.helpme.fragments;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.microbox.helpme.MainActivity;
import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;
import com.microbox.helpme.manager.DownloadImage;
import com.microbox.helpme.manager.MyAccountManager;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 03.11.2014.
 */
public class ProfFragment extends Fragment implements AdapterView.OnItemClickListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static boolean isMyProff = true;
    private static String thisProfID;
    private AccountManager accountManager;
    private Account account;
    private View rootView;
    private EditText comments;
    private View v;
    private ListView lv;
    private Context context;

    public ProfFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfFragment newInstance(int sectionNumber) {
        ProfFragment fragment = new ProfFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public static void setMyProff(boolean proff, String tPID) {

        isMyProff = proff;
        thisProfID = tPID;
    }

    public static boolean isMyProff() {
        return isMyProff;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.comment_list_head_item, null);
        rootView = inflater.inflate(R.layout.fragment_prof_activity, container, false);
        context = getActivity().getApplicationContext();
        accountManager = AccountManager.get(context);
        Account[] a = accountManager.getAccountsByType(getString(R.string.accountType));
        account = MyAccountManager.getAccount(a, context);
        lv = ((ListView) rootView.findViewById(R.id.comment_list));
        lv.addHeaderView(v);
        if (isMyProff()) {
            getProfInfo();
        } else {
            redirectProf(thisProfID);
        }

        v.findViewById(R.id.send_comment_prof).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vl) {
                v.findViewById(R.id.message_box).setVisibility(View.VISIBLE);
                v.findViewById(R.id.message_text_box).setVisibility(View.VISIBLE);
            }
        });
        comments = (EditText) v.findViewById(R.id.message_text_box);
        v.findViewById(R.id.send_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vl) {
                if (!comments.getText().toString().equals(new String())) {
                    loadStart();
                    ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    params.add(new BasicNameValuePair(Const.ACT, "Reviews"));
                    params.add(new BasicNameValuePair(Const.ID, thisProfID));
                    params.add(new BasicNameValuePair(Const.TYPE, "1"));
                    params.add(new BasicNameValuePair(Const.MESSAGE, comments.getText().toString()));
                    DataManager.call(params, context, new DataManager.Callback<List>() {
                        @Override
                        public void done(List result) {
                            comments.getText().clear();
                            setMessageWall(result);
                            loadStop();
                        }

                        @Override
                        public void error(Exception e) {
                            loadStop();
                        }
                    });
                } else {
                    Log.i("qwer", "111");
                }
            }
        });
        v.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vl) {
                v.findViewById(R.id.message_box).setVisibility(View.INVISIBLE);
                comments.setVisibility(View.GONE);
            }

        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }

    public void setMessageWall(List<ArrayList> data) {
        ArrayList<String> profDataMap = data.get(0);
        ((TextView) v.findViewById(R.id.userName)).setText(profDataMap.get(1));
        ((TextView) v.findViewById(R.id.rate)).setText(profDataMap.get(3));
        ((TextView) v.findViewById(R.id.help)).setText(profDataMap.get(7));
        ((TextView) v.findViewById(R.id.number)).setText(profDataMap.get(4));
        ((TextView) v.findViewById(R.id.prof_country)).setText(profDataMap.get(5));
        ((TextView) v.findViewById(R.id.prof_city)).setText(profDataMap.get(6));
        if (!profDataMap.get(2).equals("null")) {
            new DownloadImage((ImageView) v.findViewById(R.id.userPhoto))
                    .execute(Const.IMAGE_URL + profDataMap.get(2));
        }else{
            ((ImageView) v.findViewById(R.id.userPhoto)).setImageResource(R.drawable.default_image);
        }
        if (isMyProff()) {
            MyAccountManager.syncAccount(profDataMap, account, accountManager);
        }
        ArrayList<HashMap<String, String>> messageDataMap = data.get(1);
        String[] from = {Const.USER_ID, Const.MESSAGE, Const.DATA, Const.FULL_NAME, Const.AVATAR};
        int[] to = {R.id.user_id, R.id.text_comment, R.id.data_comment, R.id.name_comment, R.id.avatar_comment};
        SimpleAdapter adapter = new SimpleAdapter(getActivity(), messageDataMap, R.layout.comment_list_item, from, to){
            @Override
            public void setViewImage(ImageView v, String value) {
                if (!value.equals("null")) {
                    new DownloadImage(v).execute(Const.IMAGE_URL + value);
                }
            }
        };
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(this);
    }

    public void getProfInfo() {
        loadStart();
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "Account"));
        params.add(new BasicNameValuePair(Const.LANG, accountManager.getUserData(account, Const.LANG)));
        DataManager.call(params, context, new DataManager.Callback<List>() {
            @Override
            public void done(List result) {
                setMessageWall(result);
                loadStop();
            }

            @Override
            public void error(Exception e) {
                Log.i("qwer", "123" + e);
                loadStop();
            }
        });
    }

    public void loadStart() {
        rootView.findViewById(R.id.comment_list).setVisibility(View.GONE);
        rootView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
    }

    public void loadStop() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.comment_list).setVisibility(View.VISIBLE);
        v.findViewById(R.id.message_box).setVisibility(View.INVISIBLE);
        v.findViewById(R.id.message_text_box).setVisibility(View.GONE);
        if (isMyProff()) {
            v.findViewById(R.id.send_comment_prof).setVisibility(View.GONE);
        } else {
            v.findViewById(R.id.send_comment_prof).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position < 1) {
            return;
        }
        loadStart();
        if (accountManager.getUserData(account, Const.USER_ID).equals(((TextView) view.findViewById(R.id.user_id)).getText().toString())) {
            setMyProff(true, accountManager.getUserData(account, Const.USER_ID));
        } else {
            setMyProff(false, ((TextView) view.findViewById(R.id.user_id)).getText().toString());
        }
        String userId = ((TextView) view.findViewById(R.id.user_id)).getText().toString();
        redirectProf(userId);
    }

    public void redirectProf(String id) {
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "Account"));
        params.add(new BasicNameValuePair(Const.ID, id));
        params.add(new BasicNameValuePair(Const.LANG, accountManager.getUserData(account, Const.LANG)));
        DataManager.call(params, context, new DataManager.Callback<List>() {
            @Override
            public void done(List result) {
                setMessageWall(result);
                loadStop();
            }

            @Override
            public void error(Exception e) {
                Log.i("qwer", "123" + e);
                loadStop();
            }
        });
    }
    /**/
}
