package com.microbox.helpme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.microbox.helpme.MainActivity;
import com.microbox.helpme.R;
import com.microbox.helpme.constants.Const;
import com.microbox.helpme.manager.DataManager;
import com.microbox.helpme.manager.DownloadImage;

import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 18.11.2014.
 */
public class MoreFragment extends Fragment implements DataManager.Callback<List>, AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static String title;
    private static String ticketID;
    private View rootView, header;
    private ProfCallback mCallbacks;
    private showCallback mShowCallbacks;
    private ListView lv;

    public MoreFragment() {
    }

    public static MoreFragment newInstance(int sectionNumber, String id, String t) {
        ticketID = id;
        title = t;
        MoreFragment fragment = new MoreFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_more_activity, container, false);
        startLoad();
        header = inflater.inflate(R.layout.more_list_header, null);
        header.findViewById(R.id.send_button).setOnClickListener(this);
        header.findViewById(R.id.get_author_prof_btn).setOnClickListener(this);
        setHasOptionsMenu(true);
        lv = ((ListView) rootView.findViewById(R.id.comment_list));
        lv.addHeaderView(header);
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "InfoTicket"));
        params.add(new BasicNameValuePair(Const.ID, ticketID));
        DataManager.call(params, getActivity().getApplicationContext(), this);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_more_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (ProfCallback) activity;
        mShowCallbacks = (showCallback) activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }

    @Override
    public void done(List result) {
        ArrayList<String> profDataMap = (ArrayList) result.get(0);
        ((TextView) header.findViewById(R.id.name_comment)).setText(profDataMap.get(6));
        ((TextView) header.findViewById(R.id.ticket_title)).setText(profDataMap.get(1));
        ((TextView) header.findViewById(R.id.ticket_message)).setText(profDataMap.get(2));
        ((TextView) header.findViewById(R.id.data_comment)).setText(profDataMap.get(5));
        ((TextView) header.findViewById(R.id.user_id)).setText(profDataMap.get(0));
        ((TextView) header.findViewById(R.id.loc_x)).setText(profDataMap.get(3));
        ((TextView) header.findViewById(R.id.loc_y)).setText(profDataMap.get(4));
        if (!profDataMap.get(7).equals("null")) {
            new DownloadImage((ImageView) header.findViewById(R.id.avatar_comment))
                    .execute(Const.IMAGE_URL + profDataMap.get(7));
        }
        ArrayList<HashMap<String, String>> messageDataMap = (ArrayList) result.get(1);
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
        stopLoad();
    }

    @Override
    public void error(Exception e) {
        Log.i("qwer", e + "");
        stopLoad();
    }

    public void startLoad() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.comment_list).setVisibility(View.GONE);
    }

    public void stopLoad() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.comment_list).setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            getProfAuthor(view);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_example) {
            Double latitude = Double.parseDouble( ((TextView) header.findViewById(R.id.loc_x)).getText().toString() );
            Double longitude = Double.parseDouble( ((TextView) header.findViewById(R.id.loc_y)).getText().toString() );
            mShowCallbacks.showInMap(new LatLng(latitude, longitude),  title, ticketID);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getProfAuthor(View view) {
        TextView idTextView = (TextView) view.findViewById(R.id.user_id);
        String userID = idTextView.getText().toString();
        mCallbacks.onProfRedirect(userID);
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().toString().equals("1")) {
            EditText messageTextEdit = (EditText) header.findViewById(R.id.comment_text);
            String message = messageTextEdit.getText().toString();
            if (!message.equals(new String())) {
                startLoad();
                ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                params.add(new BasicNameValuePair(Const.ACT, "Comments"));
                params.add(new BasicNameValuePair(Const.SAVE, "1"));
                params.add(new BasicNameValuePair(Const.MESSAGE, message));
                params.add(new BasicNameValuePair(Const.ID, ticketID));
                messageTextEdit.setText("");
                DataManager.call(params, getActivity().getApplicationContext(), this);
            }
        } else if (v.getTag().toString().equals("0")) {
            getProfAuthor(header);
        }
    }

    public static interface ProfCallback {
        void onProfRedirect(String userID);
    }

    public static interface showCallback {
        void showInMap(LatLng latLng, String title, String ticketID);
    }
}
