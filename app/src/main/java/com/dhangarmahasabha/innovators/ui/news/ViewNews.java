package com.dhangarmahasabha.innovators.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.tool.BaseActivity;
import com.dhangarmahasabha.innovators.tool.ObservableScrollView;
import com.dhangarmahasabha.innovators.tool.ObservableScrollViewCallbacks;
import com.dhangarmahasabha.innovators.tool.ScrollState;
import com.dhangarmahasabha.innovators.tool.ScrollUtils;
import com.dhangarmahasabha.innovators.util.Config;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ViewNews extends BaseActivity implements ObservableScrollViewCallbacks {

    private View mImageView;
    private View mToolbarView;
    private ImageView imageView;
    private TextView textView;
    private TextView txtTitle;
    private TextView txtDate;
    private ActionBar actionBar;

    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_news);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        mImageView = findViewById(R.id.image);
        mToolbarView = findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);
        Intent i = getIntent();

        // getting product id (pid) from intent
        int id = Integer.parseInt(i.getStringExtra("id"));
        String title = i.getStringExtra("title");
        String news = i.getStringExtra("news");
        String time = i.getStringExtra("time");
        String path = i.getStringExtra("path");
        imageView = (ImageView) mImageView;
        textView = (TextView) findViewById(R.id.body);
        txtTitle = (TextView) findViewById(R.id.title);
        txtDate =  (TextView) findViewById(R.id.date);
        textView.setText(news);
        txtTitle.setText(title);
        txtDate.setText(time);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(Config.IMAGE_URL + path, imageView);
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
}