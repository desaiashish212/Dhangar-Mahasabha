package com.dhangarmahasabha.innovators.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.adaper.NewsAdapter;
import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.model.News;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
import com.dhangarmahasabha.innovators.util.PrefManager;
import com.innovators.localizationactivity.LocalizationActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private ListView listView;
    private View imageView;
    private ImageView ads_imageView;
    private TextView stickyView;
    private View stickyViewSpacer;
    private NewsAdapter adapter;
    private DBHandler handler;
   // private String STATUS = "1";
    private String language = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NetworkUtils utils;
    private String cat_id;
    private String cat_name;
    private PrefManager manager;
    private TextView splashText;
    public NewsFragment() {
        // Required empty public constructor
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_tajya_batmya, container, false);
        Bundle bundle = this.getArguments();
        cat_id = bundle.getString("id");
        cat_name = bundle.getString("name");
        System.out.println("id:"+cat_id+"......."+"name:"+cat_name);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        listView = (ListView) rootView.findViewById(R.id.list);
        imageView = rootView.findViewById(R.id.ads);
        ads_imageView = (ImageView) imageView;
        stickyView= (TextView) rootView.findViewById(R.id.stickyView);
        swipeRefreshLayout.setColorSchemeColors(R.color.swype_1,R.color.swype_2,R.color.swype_3,R.color.swype_4);
        stickyView.setText(cat_name);
        LayoutInflater inflater1 = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listHeader = inflater1.inflate(R.layout.list_header, null);
        stickyViewSpacer = listHeader.findViewById(R.id.stickyViewPlaceholder);
        listView.addHeaderView(listHeader);
        language = LocalizationActivity.getLanguage();
        handler = new DBHandler(getActivity());
        utils = new NetworkUtils(getActivity());
        manager = new PrefManager(getActivity());
        System.out.println("Bannerrrrr:"+handler.getBanner());
        splashText = (TextView) rootView.findViewById(R.id.splashText);
        splashText.setText(manager.getSplash());
        splashText.setSelected(true);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.IMAGE_URL + handler.getBanner(), ads_imageView);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int                                                                           visibleItemCount,int totalItemCount)
            {
                if (listView.getFirstVisiblePosition() == 0) {
                    View firstChild = listView.getChildAt(0);
                    int topY = 0;
                    if (firstChild != null) {
                        topY = firstChild.getTop();
                    }

                    int heroTopY = stickyViewSpacer.getTop();
                    stickyView.setY(Math.max(0, heroTopY + topY));
                    imageView.setY(topY * 0.5f);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(utils.isConnectingToInternet()) {
                                            swipeRefreshLayout.setRefreshing(true);
                                           // new LoadAllNews(getActivity(),cat_id,language,listView,swipeRefreshLayout).execute();
                                            loadNews();
                                        }else{
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                }
        );
        System.out.println("Lang......"+language);
        if(handler.getNewsCount(cat_id)==0 && utils.isConnectingToInternet()) {
           // new LoadAllNews(getActivity(),cat_id,language,listView,swipeRefreshLayout).execute();
            loadNews();
        }
        else
        {
            ArrayList<News> newsList = handler.getAllNews(cat_id);
            adapter = new NewsAdapter(getActivity(),newsList);
            listView.setAdapter(adapter);
        }
        return rootView;
    }

    @Override
    public void onRefresh() {
        if(handler.getNewsCount(cat_id)==0 && utils.isConnectingToInternet()) {
           // new LoadAllNews(getActivity(),cat_id,language,listView,swipeRefreshLayout).execute();
            loadNews();
        }else{
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void loadNews() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_ALL_NEWS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("NewsFragment", response.toString());

                try {

                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        // parsing the user profile information
                        JSONArray jsonArray = responseObj.getJSONArray(ConstCore.TAG_news);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            News news = new News();
                            news.setNid(Integer.parseInt(c.getString(ConstCore.TAG_NID)));
                            news.settitle(c.getString(ConstCore.TAG_TITLE));
                            news.setnews(c.getString(ConstCore.TAG_NEWSS));
                            news.settime(c.getString(ConstCore.TAG_TIME));
                            news.setdate(c.getString(ConstCore.TAG_DATE));
                            news.setStatus(cat_id);
                            news.setpath(c.getString(ConstCore.TAG_PATH));
                            news.setpath1(c.getString(ConstCore.TAG_PATH1));
                            System.out.println("iddddd:" + Integer.parseInt(c.getString(ConstCore.TAG_NID)));
                            if (!handler.getIdNews(Integer.parseInt(c.getString(ConstCore.TAG_NID)))) {
                                handler.addNews(news);// Inserting into DB
                                System.out.println("inserting db " + news);
                            } else {
                                System.out.println("News is already present");
                            }
                        }
                        JSONArray jsonArray1 = responseObj.getJSONArray(ConstCore.TAG_BANNER);
                        System.out.println("jsonArray1::::"+jsonArray1);
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            JSONObject c = jsonArray1.getJSONObject(i);
                            int id = Integer.parseInt(c.getString(ConstCore.TAG_NID));
                            String path = c.getString(ConstCore.TAG_PATH);
                            int status = Integer.parseInt(c.getString(ConstCore.TAG_NID));
                            if (!handler.getIdBanner(id,status)) {
                                handler.addBanner(id,path,status);// Inserting into DB
                                System.out.println("inserting db " + path);
                            } else {
                                System.out.println("Banner is already present");
                            }
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        ArrayList<News> newsList = handler.getAllNews(cat_id);
                        System.out.println("on post  "+ newsList);
                        adapter = new NewsAdapter(getActivity(),newsList);
                        listView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("NewsFragment","Error: " + error.getMessage());
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ConstCore.TAG_STATUS, cat_id);
                params.put(ConstCore.TAG_LANGUAGE, language);

                Log.e("NewsFragment", "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}
