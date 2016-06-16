package com.dhangarmahasabha.innovators.adaper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.google.android.gms.analytics.ecommerce.Product;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by AD on 07-Jun-16.
 */
public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    private DisplayImageOptions displayImageOptions;
    private String[] path;
    ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageLoadingListener imageListener;
    DisplayImageOptions options;


    public CustomPagerAdapter(Context context,String[] path) {
        mContext = context;
        this.path = path;
        initImageLoaderOptions();
        imageListener = new ImageDisplayListener();

    }

    public void initImageLoaderOptions() {
        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo).showImageOnFail(R.drawable.logo).cacheInMemory(
                        true).cacheOnDisc(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.vp_image, collection, false);

        ImageView mImageView = (ImageView) view
                .findViewById(R.id.image_display);

        imageLoader.displayImage(Config.IMAGE_URL+path[position], mImageView,
        options, imageListener);
        collection.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return path.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    private static class ImageDisplayListener extends
            SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections
                .synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view,
                                      Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }
}