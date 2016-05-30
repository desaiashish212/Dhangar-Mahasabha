package com.dhangarmahasabha.innovators.adaper;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.model.News;
import com.dhangarmahasabha.innovators.ui.news.ViewNews;
import com.dhangarmahasabha.innovators.util.Config;
import com.dhangarmahasabha.innovators.util.ConstCore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by AD on 1/3/2016.
 */
public class NewsAdapter extends BaseAdapter {
    Context context;
    ArrayList<News> listData;
  //  String barTitle;
  //  ImageLoader imageLoader;

    public NewsAdapter(Context context, ArrayList<News> listData){
        this.context = context;
        this.listData = listData;
    //    this.barTitle = title;
   //     imageLoader = new ImageLoader(context);
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

      //  imageLoader.DisplayImage(ConstCore.IMAGE_URL + newsPath, viewHolder.image);
        if(TextUtils.isEmpty(newsPath)){
            viewHolder.textViewtitle2.setText(newsTitle);
            viewHolder.textViewTime2.setText(newsDate+" "+newsTime);
        }else {
            viewHolder.textViewTime.setText(newsTime);
            viewHolder.textViewDate.setText(newsDate);
            viewHolder.textViewtitle.setText(newsTitle);
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(Config.IMAGE_URL + newsPath, viewHolder.image);
        }
        System.out.println("path:"+ Config.IMAGE_URL + newsPath);

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get the position
                Intent intent = new Intent(context, ViewNews.class);
                // Pass all data rank
                // Pass all data country
                intent.putExtra("id",id);
                intent.putExtra("title", newsTitle);
                // Pass all data population
                intent.putExtra("news", news.getnews());
                // Pass all data flag
                intent.putExtra("time", newsDate+" "+newsTime);
                intent.putExtra("path", newsPath);
                context.startActivity(intent);
            }
        });
        return view;
    }
}


