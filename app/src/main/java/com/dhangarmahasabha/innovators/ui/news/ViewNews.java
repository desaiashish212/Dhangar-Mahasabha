package com.dhangarmahasabha.innovators.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.db.DBHandler;
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
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ViewNews extends BaseActivity implements ObservableScrollViewCallbacks {

    String TAG = ViewNews.class.getSimpleName();

    private View mImageView;
    private View mToolbarView;
    private ImageView imageView;
    private ImageView imageView1;
    private ImageView ads;
    private TextView textView;
    private TextView txtTitle;
    private TextView txtDate;
    private TextView txtBack;
    private TextView txtBackId;
    private TextView txtForwarId;
    private TextView txtForward;
    private ActionBar actionBar;
    private DBHandler handler;
    private News news;

    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

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
        Intent i = getIntent();

        final int id = Integer.parseInt(i.getStringExtra("id"));
        news = handler.getNewsByID(id);
        imageView = (ImageView) mImageView;
        imageView1 =(ImageView) findViewById(R.id.image1);
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
        String path = news.getpath();
        String path1 = news.getpath1();
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.getInstance().displayImage(Config.IMAGE_URL + news.getpath(),
                    imageView, ConstCore.UIL_DEFAULT_DISPLAY_OPTIONS);
           // imageLoader.displayImage(Config.IMAGE_URL + news.getpath(), imageView);
        }else {
            imageView.setImageResource(R.drawable.logo);
        }
        if (path1.length()>4){
            Log.d(TAG,"In if conditon:"+path1.length());
            imageView1.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage(Config.IMAGE_URL + news.getpath1(),
                    imageView1, ConstCore.UIL_DEFAULT_DISPLAY_OPTIONS);
        }else {
            imageView1.setVisibility(View.GONE);
        }
        txtBack =  (TextView) findViewById(R.id.newsBack);
        txtForward =  (TextView) findViewById(R.id.newsForward);
        txtBackId =  (TextView) findViewById(R.id.tvBackId);
        txtForwarId =  (TextView) findViewById(R.id.tvForwardID);

        // Perfect storm:
        final int count = handler.getNewsCount(news.getStatus());
        final int[] array = handler.getIdArray(news.getStatus());
        final int indexNum = getArrayIndex(array,id);
        txtForwarId.setText(String.valueOf(count));
        txtBackId.setText(String.valueOf(indexNum+1));
        Log.d(TAG,String.valueOf(indexNum));

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array[0]==id){
                    int backid = array[count-1];
                    Intent intent = new Intent(ViewNews.this, ViewNews.class);
                    intent.putExtra("id",String.valueOf(backid));
                    startActivity(intent);
                    finish();
                }else {
                    int backid = array[indexNum-1];
                    Intent intent = new Intent(ViewNews.this, ViewNews.class);
                    intent.putExtra("id",String.valueOf(backid));
                    startActivity(intent);
                    finish();
                }
            }
        });

        txtForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (array[count-1]==id){
                    int backid = array[0];
                    Intent intent = new Intent(ViewNews.this, ViewNews.class);
                    intent.putExtra("id",String.valueOf(backid));
                    startActivity(intent);
                    finish();
                }else {
                    int backid = array[indexNum+1];
                    Intent intent = new Intent(ViewNews.this, ViewNews.class);
                    intent.putExtra("id",String.valueOf(backid));
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public int getArrayIndex(int[] arr,int value) {

        int k=0;
        for(int i=0;i<arr.length;i++){
            System.out.println(arr[i]);

            if(arr[i]==value){
                k=i;
                break;
            }
        }
        return k;
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
        String url = "https://play.google.com/store/apps/details?id="+getPackageName();
        String appname = getString(R.string.app_name);
        switch (id){
            case R.id.menu_share:
                Intent localIntent2 = new Intent("android.intent.action.SEND");
                localIntent2.setType("text/plain");
                localIntent2.putExtra("android.intent.extra.SUBJECT", "Subject Here");
                localIntent2.putExtra("android.intent.extra.TEXT", news.gettitle()+"\n\n"+url+"\n\nvia "+appname);
                startActivity(Intent.createChooser(localIntent2, "Share via"));
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

}