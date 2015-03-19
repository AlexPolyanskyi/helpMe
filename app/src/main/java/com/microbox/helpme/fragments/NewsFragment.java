package com.microbox.helpme.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

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
 * Created by user on 03.11.2014.
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView listView;
    private View rootView;
    private NewsCallback callbacks;
    public NewsFragment() {
    }

    public static NewsFragment newInstance(int sectionNumber) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_news_activity, container, false);
        startLoad();
        listView = ((ListView) rootView.findViewById(R.id.comment_list));
        ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        params.add(new BasicNameValuePair(Const.ACT, "AllTicket"));
        DataManager.call(params, getActivity().getApplicationContext(), new DataManager.Callback<List>() {
            @Override
            public void done(List result) {
                ArrayList<HashMap<String, String>> messageDataMap = (ArrayList) result;
                String[] from = {Const.ID, Const.TITLE, Const.DATA, Const.FULL_NAME, Const.AVATAR};
                int[] to = {R.id.user_id, R.id.text_comment, R.id.data_comment, R.id.name_comment, R.id.avatar_comment};
                SimpleAdapter adapter = new SimpleAdapter(getActivity(), messageDataMap, R.layout.comment_list_item, from, to){
                    @Override
                    public void setViewImage(ImageView v, String value) {
                        if (!value.equals("null")) {
                            new DownloadImage(v).execute(Const.IMAGE_URL + value);
                        }
                    }
                };
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(NewsFragment.this);
                stopLoad();
            }

            @Override
            public void error(Exception e) {

            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (NewsCallback) activity;
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        ((MainActivity) activity).restoreActionBar();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView text = (TextView) view.findViewById(R.id.user_id);
        TextView textComment = (TextView) view.findViewById(R.id.text_comment);
        callbacks.more(text.getText().toString(), textComment.getText().toString());
    }

    public void startLoad() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.comment_list).setVisibility(View.GONE);
    }

    public void stopLoad() {
        rootView.findViewById(android.R.id.progress).setVisibility(View.GONE);
        rootView.findViewById(R.id.comment_list).setVisibility(View.VISIBLE);
    }

    public static interface NewsCallback {
        void more(String ticketID, String title);
    }
}