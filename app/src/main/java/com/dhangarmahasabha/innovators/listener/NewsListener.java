package com.dhangarmahasabha.innovators.listener;


import com.dhangarmahasabha.innovators.model.Category;
import com.dhangarmahasabha.innovators.model.News;

import java.util.ArrayList;

/**
 * Created by AD on 1/3/2016.
 */
public interface NewsListener {
    public void addNews(News news);

    public ArrayList<News> getAllNews(String status);

    public void addCategory(Category category);

    public ArrayList<Category> getAllCategory(int status);

    public int getNewsCount(String status);
    public boolean getIdNews(int news_id);
}
