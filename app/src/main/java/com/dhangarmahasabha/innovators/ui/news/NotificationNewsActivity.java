package com.dhangarmahasabha.innovators.ui.news;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.dhangarmahasabha.innovators.MyApplication;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.gcm.NotificationUtils;
import com.dhangarmahasabha.innovators.model.News;
import com.dhangarmahasabha.innovators.tool.BaseActivity;
import com.dhangarmahasabha.innovators.tool.ObservableScrollView;
import com.dhangarmahasabha.innovators.tool.ObservableScrollViewCallbacks;
import com.dhangarmahasabha.innovators.tool.ScrollState;
import com.dhangarmahasabha.innovators.tool.ScrollUtils;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.dhangarmahasabha.innovators.util.DateUtils;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.dhangarmahasabha.innovators.util.NetworkUtils;
import com.innovators.localizationactivity.LocalizationActivity;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AD on 06-Jun-16.
 */
public class NotificationNewsActivity extends BaseActivity implements ObservableScrollViewCallbacks {
    String TAG = NotificationNewsActivity.class.getSimpleName();

    private View mImageView;
    private View mToolbarView;
    private ImageView imageView;
    private ImageView ads;
    private TextView textView;
    private TextView txtTitle;
    private TextView txtDate;
    private ActionBar actionBar;
    private DBHandler handler;
    private News news;
    private String id,status="1";
    private String language = null;
//    private ProgressDialog progressDialog;
    private NetworkUtils networkUtils;

    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_news);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setHomeButtonEnabled(true);

        mImageView = findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        handler = new DBHandler(this);
        networkUtils = new NetworkUtils(this);
        language = LocalizationActivity.getLanguage();
        Intent i = getIntent();

        id = i.getStringExtra(ConstCore.TAG_NID);
      //  status = i.getStringExtra(ConstCore.TAG_STATUS);
        if (networkUtils.isConnectingToInternet()) {
            loadNews();
            NotificationUtils notificationUtils = new NotificationUtils(this);
            notificationUtils.clearNotifications();
            finish();
        }else {
            Toast.makeText(NotificationNewsActivity.this, "Check your internet", Toast.LENGTH_LONG).show();
            NotificationUtils notificationUtils = new NotificationUtils(this);
            notificationUtils.clearNotifications();
            finish();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.menu_share:
                Intent localIntent2 = new Intent("android.intent.action.SEND");
                localIntent2.setType("text/plain");
                localIntent2.putExtra("android.intent.extra.SUBJECT", "Subject Here");
                localIntent2.putExtra("android.intent.extra.TEXT", news.gettitle()+":"+news.getnews());
                startActivity(Intent.createChooser(localIntent2, "Share via"));
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    private void showNews(){
        news = handler.getNewsByID(Integer.parseInt(id));
        imageView = (ImageView) mImageView;
        ads = (ImageView) findViewById(R.id.ads);
        textView = (TextView) findViewById(R.id.body);
        txtTitle = (TextView) findViewById(R.id.title);
        txtDate =  (TextView) findViewById(R.id.date);
        ImageLoader.getInstance().displayImage(Config.IMAGE_URL + handler.getBanner(),
                ads, ConstCore.UIL_DEFAULT_DISPLAY_OPTIONS);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage(Config.IMAGE_URL + handler.getBanner(), ads);
        textView.setText(news.getnews());
        txtTitle.setText(news.gettitle());
        final String date1 = news.getdate();
        long longDate=0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(date1);

            longDate = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            DialogUtils.show(this,e.toString());
        }
        txtDate.setText(DateUtils.longToNewsDate(longDate));
        imageView.setImageResource(R.drawable.logo);
        if (news.getpath()!=null) {
            ImageLoader.getInstance().displayImage(Config.IMAGE_URL + news.getpath(),
                    imageView, ConstCore.UIL_DEFAULT_DISPLAY_OPTIONS);
            // imageLoader.displayImage(Config.IMAGE_URL + news.getpath(), imageView);
        }else {
            imageView.setImageResource(R.drawable.logo);
        }
    }

    public void loadNews() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_NEWS, new Response.Listener<String>() {

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
                            news.setStatus(id);
                            news.setpath(c.getString(ConstCore.TAG_PATH));
                            System.out.println("iddddd:" + Integer.parseInt(c.getString(ConstCore.TAG_NID)));
                            if (!handler.getIdNews(Integer.parseInt(c.getString(ConstCore.TAG_NID)))) {
                                handler.addNews(news);// Inserting into DB
                                System.out.println("inserting db " + news);
                            } else {
                                System.out.println("News is already present");
                            }
                            showNews();
                        }

                    } else {
                        Toast.makeText(NotificationNewsActivity.this, message, Toast.LENGTH_LONG).show();
                        finish();
                    }

                } catch (JSONException e) {
                    Toast.makeText(NotificationNewsActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    finish();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("NewsFragment","Error: " + error.getMessage());
                Toast.makeText(NotificationNewsActivity.this,
                        "Error: " + error.toString(),
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(ConstCore.TAG_NID, id);
                params.put(ConstCore.TAG_STATUS, status);
                params.put(ConstCore.TAG_LANGUAGE, language);

                Log.e("NewsFragment", "Posting params: " + params.toString());
                return params;
            }

        };

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(strReq);
    }

}