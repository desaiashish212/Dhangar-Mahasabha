package com.dhangarmahasabha.innovators.newsloader;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.ListView;

import com.dhangarmahasabha.innovators.JSON.JSONParser;
import com.dhangarmahasabha.innovators.R;
import com.dhangarmahasabha.innovators.adaper.NewsAdapter;
import com.dhangarmahasabha.innovators.db.DBHandler;
import com.dhangarmahasabha.innovators.model.News;
import com.dhangarmahasabha.innovators.util.ConstCore;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AD on 23-Feb-16.
 */
public class LoadAllNews extends AsyncTask<String, String, String> {
    JSONParser jParser = new JSONParser();
    ListView listView;
    NewsAdapter adapter;
    DBHandler handler;
    private String STATUS;
    private String language = null;
    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;

    public LoadAllNews(Context context,String status,String language,ListView listView,SwipeRefreshLayout swipeRefreshLayout){
        this.context = context;
        this.STATUS = status;
        this.language = language;
        this.listView = listView;
        handler = new DBHandler(context);
        this.swipeRefreshLayout = swipeRefreshLayout;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    protected String doInBackground(String... args) {

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(ConstCore.TAG_STATUS,STATUS));
        params.add(new BasicNameValuePair(ConstCore.TAG_LANGUAGE, language));

        JSONObject json = jParser.makeHttpRequest(ConstCore.url_all_news, ConstCore.POST, params);

        Log.d("All News: ", json.toString());

        try {
            int success = json.getInt(ConstCore.TAG_SUCCESS);

            if (success == 1) {

                JSONArray jsonArray = json.getJSONArray(ConstCore.TAG_news);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject c = jsonArray.getJSONObject(i);
                    News news = new News();
                    news.setNid(Integer.parseInt(c.getString(ConstCore.TAG_NID)));
                    news.settitle(c.getString(ConstCore.TAG_TITLE));
                    news.setnews(c.getString(ConstCore.TAG_NEWSS));
                    news.settime(c.getString(ConstCore.TAG_TIME));
                    news.setdate(c.getString(ConstCore.TAG_DATE));
                    news.setStatus(STATUS);
                    news.setpath(c.getString(ConstCore.TAG_PATH));
                    System.out.println("iddddd:"+Integer.parseInt(c.getString(ConstCore.TAG_NID)));
                    if (!handler.getIdNews(Integer.parseInt(c.getString(ConstCore.TAG_NID)))){
                        handler.addNews(news);// Inserting into DB
                        System.out.println("inserting db " + news);
                    } else {
                        System.out.println("News is already present");
                    }

                }
            } else {

                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("Message","Json Object",e);
        }

        return null;
    }

    protected void onPostExecute(String e) {
        swipeRefreshLayout.setRefreshing(false);
        ArrayList<News> newsList = handler.getAllNews(STATUS);
        System.out.println("on post  "+ newsList);
        adapter = new NewsAdapter(context,newsList);
        listView.setAdapter(adapter);
    }
}