package com.dhangarmahasabha.innovators.adaper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.model.News;
import com.dhangarmahasabha.innovators.ui.news.NotificationNewsActivity;
import com.dhangarmahasabha.innovators.ui.news.ViewNews;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.dhangarmahasabha.innovators.util.DateUtils;
import com.dhangarmahasabha.innovators.util.DialogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by AD on 1/3/2016.
 */
public class NewsAdapter extends BaseAdapter {
    Context context;
    ArrayList<News> listData;
    private int lastPosition = -1;
    private DisplayImageOptions displayImageOptions;

    public NewsAdapter(Context context, ArrayList<News> listData){
        this.context = context;
        this.listData = listData;
        initImageLoaderOptions();
    }
    public void initImageLoaderOptions() {
        displayImageOptions = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.logo)
                .showImageForEmptyUri(R.drawable.logo).showImageOnFail(R.drawable.logo).cacheInMemory(
                        true).cacheOnDisc(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }
    @Override
    public    int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class ViewHolder {
        private TextView textViewtitle;
        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewtitle2;
        private TextView textViewTime2;
        private ImageView image;
        private ImageView imageLogo;
        private ProgressBar progressBar;
        private ViewPager viewPager;
    }
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(view == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final News news = listData.get(position);
            final String newsPath = news.getpath();
            viewHolder = new ViewHolder();
            if (!TextUtils.isEmpty(newsPath)) {
                view = inflater.inflate(R.layout.list_item, null);
                viewHolder.textViewtitle = (TextView) view.findViewById(R.id.title);
                viewHolder.textViewDate = (TextView) view.findViewById(R.id.date);
                viewHolder.textViewTime = (TextView) view.findViewById(R.id.time);
                viewHolder.image = (ImageView) view.findViewById(R.id.headerPic);
              //  viewHolder.viewPager = (ViewPager) view.findViewById(R.id.viewpager);

                viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            }else {
                view = inflater.inflate(R.layout.list_item1, null);
                viewHolder.imageLogo = (ImageView) view.findViewById(R.id.imageIcon);
                viewHolder.textViewtitle2 = (TextView) view.findViewById(R.id.title2);
                viewHolder.textViewTime2 = (TextView) view.findViewById(R.id.time2);
            }

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        final News news = listData.get(position);
        final String id = String.valueOf(news.getNid());
        final String newsTitle = news.gettitle();
        final String newsDate = news.getdate();
        final String newsTime = news.gettime();
        final String newsPath = news.getpath();
        final String newsPath1 = news.getpath1();
        long longDate=0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
            Date date = sdf.parse(newsDate);

            longDate = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            DialogUtils.show(context,e.toString());
        }

        if(TextUtils.isEmpty(newsPath)){
            viewHolder.textViewtitle2.setText(newsTitle);
            viewHolder.textViewTime2.setText(DateUtils.longToNewsDate(longDate));
        }else {
        //    viewHolder.textViewTime.setText(newsTime);
            viewHolder.textViewDate.setText(DateUtils.longToNewsDate(longDate));
            viewHolder.textViewtitle.setText(newsTitle);
//            ImageLoader imageLoader = ImageLoader.getInstance();
//            imageLoader.displayImage(Config.IMAGE_URL + newsPath, viewHolder.image);
//            if (news.getpath1()!=null){
//                String[] paths = {news.getpath(),news.getpath1()};
//                viewHolder.viewPager.setAdapter(new CustomPagerAdapter(context,paths));
//            }else {
//
//                String[] paths = {news.getpath()};
//                viewHolder.viewPager.setAdapter(new CustomPagerAdapter(context,paths));
//            }

//            ImageLoader.getInstance().displayImage(Config.IMAGE_URL + newsPath,
//                    viewHolder.image, ConstCore.UIL_DEFAULT_DISPLAY_OPTIONS);
            applyImage(viewHolder.image,Config.IMAGE_URL + newsPath,viewHolder.progressBar,position);
        }
        System.out.println("path:"+ Config.IMAGE_URL + newsPath);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                Intent intent = new Intent(context, ViewNews.class);
                intent.putExtra("id",id);
                context.startActivity(intent);
            }
        });
        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        view.startAnimation(animation);
        lastPosition = position;
        return view;
    }
    private void applyImage(ImageView img,final String path, final ProgressBar progressBar, int position) {
        //img.setImageBitmap(DataHolder.getDataHolder().getNoteCity(position));
        ImageLoader.getInstance().displayImage(
                path,img, displayImageOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setProgress(0);
                        progressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        lastPosition = -1;
    }
}


