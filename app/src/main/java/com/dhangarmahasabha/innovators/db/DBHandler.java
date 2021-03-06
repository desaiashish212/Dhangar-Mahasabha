package com.dhangarmahasabha.innovators.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dhangarmahasabha.innovators.listener.NewsListener;
import com.dhangarmahasabha.innovators.model.Category;
import com.dhangarmahasabha.innovators.model.News;
import com.innovators.localizationactivity.LocalizationActivity;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by AD on 1/14/2016.
 */
public class DBHandler extends SQLiteOpenHelper implements NewsListener {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "NewsDatabase.db";
    private static String TABLE_NAME_MARATHI = "news_marathi";
    private static String TABLE_NAME_HINDI = "news_hindi";
    private static String TABLE_NAME_ENGLISH = "news_english";
    private static String TABLE_NAME_CATEGORY = "news_category";
    private static String TABLE_NAME_ADVERTISE = "advertise";
    private static final String KEY_ID = "id";
    private static final String KEY_NID = "nid";
    private static final String KEY_TITLE = "title";
    private static final String KEY_NEWS = "news";
    private static final String KEY_TIME = "time";
    private static final String KEY_STATUS = "status";
    private static final String KEY_DATE = "date";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_PATH = "path";
    private static final String KEY_PATH1 = "path1";

    private static final String KEY_CATID = "cat_id";
    private static final String KEY_CATNAME = "cat_name";
    private static final String KEY_LANGUAGE = "lang_status";
    private static final String KEY_CATPRIORITY = "cat_priority";

    private static final String KEY_ADDSID = "adds_id";
    private static final String KEY_ADDSPATH = "adds_path";
    private static final String KEY_ADDSSTATUS= "adds_status";

    private String language = null;

    String CREATE_TABLE_MARATHI = "CREATE TABLE "+TABLE_NAME_MARATHI+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NID+" INTEGER,"+KEY_TITLE+" TEXT,"+KEY_NEWS+" TEXT,"+KEY_TIME+" TEXT,"+KEY_DATE+" TEXT,"+KEY_STATUS+" TEXT,"+KEY_PATH+" TEXT,"+KEY_PATH1+" TEXT,"+KEY_IMAGE+" BLOB"+")";
    String DROP_TABLE_MARATHI = "DROP TABLE IF EXISTS "+TABLE_NAME_MARATHI;
    String CREATE_TABLE_HINDI = "CREATE TABLE "+TABLE_NAME_HINDI+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NID+" INTEGER,"+KEY_TITLE+" TEXT,"+KEY_NEWS+" TEXT,"+KEY_TIME+" TEXT,"+KEY_DATE+" TEXT,"+KEY_STATUS+" TEXT,"+KEY_PATH+" TEXT,"+KEY_PATH1+" TEXT,"+KEY_IMAGE+" BLOB"+")";
    String DROP_TABLE_HINDI = "DROP TABLE IF EXISTS "+TABLE_NAME_HINDI;
    String CREATE_TABLE_ENGLISH = "CREATE TABLE "+TABLE_NAME_ENGLISH+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_NID+" INTEGER,"+KEY_TITLE+" TEXT,"+KEY_NEWS+" TEXT,"+KEY_TIME+" TEXT,"+KEY_DATE+" TEXT,"+KEY_STATUS+" TEXT,"+KEY_PATH+" TEXT,"+KEY_PATH1+" TEXT,"+KEY_IMAGE+" BLOB"+")";
    String DROP_TABLE_ENGLISH = "DROP TABLE IF EXISTS "+TABLE_NAME_ENGLISH;

    String CREATE_TABLE_CATEGORY = "CREATE TABLE "+TABLE_NAME_CATEGORY+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_CATID+" INTEGER,"+KEY_CATNAME+" TEXT,"+KEY_LANGUAGE+" INTEGER,"+KEY_CATPRIORITY+" INTEGER"+")";
    String DROP_TABLE_CATEGORY = "DROP TABLE IF EXISTS "+TABLE_NAME_CATEGORY;

    String CREATE_TABLE_ADVERTISE = "CREATE TABLE "+TABLE_NAME_ADVERTISE+" ("+KEY_ID+" INTEGER PRIMARY KEY,"+KEY_ADDSID+" INTEGER,"+KEY_ADDSPATH+" TEXT,"+KEY_ADDSSTATUS+" INTEGER"+")";
    String DROP_TABLE_ADVERTISE = "DROP TABLE IF EXISTS "+TABLE_NAME_ADVERTISE;

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MARATHI);
        db.execSQL(CREATE_TABLE_HINDI);
        db.execSQL(CREATE_TABLE_ENGLISH);
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_ADVERTISE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_MARATHI);
        db.execSQL(DROP_TABLE_HINDI);
        db.execSQL(DROP_TABLE_ENGLISH);
        db.execSQL(DROP_TABLE_CATEGORY);
        db.execSQL(DROP_TABLE_ADVERTISE);
        onCreate(db);
    }

    @Override
    public void addNews(News news) {
        SQLiteDatabase db = this.getWritableDatabase();
        language = LocalizationActivity.getLanguage();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_NID, news.getNid());
            values.put(KEY_TITLE, news.gettitle());
            values.put(KEY_NEWS, news.getnews());
            values.put(KEY_TIME,news.gettime());
            values.put(KEY_DATE,news.getdate());
            values.put(KEY_STATUS, news.getStatus());
            values.put(KEY_PATH, news.getpath());
            values.put(KEY_PATH1, news.getpath1());
            System.out.println("Path inserting:" + news.getpath() + " Lang:" + language);
            if (language.equals("ma")){
                db.insert(TABLE_NAME_MARATHI, null, values);
            }else if (language.equals("hi")){
                db.insert(TABLE_NAME_HINDI, null, values);
            }else if (language.equals("en")){
                db.insert(TABLE_NAME_ENGLISH, null, values);
            }
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    public News getNewsByID(int id) {
        language = LocalizationActivity.getLanguage();
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY=null;
        News news = null;
        try{
            news = new News();
            if (language.equals("ma")){
                QUERY = "SELECT * FROM "+TABLE_NAME_MARATHI+" WHERE nid="+id;
            }else if (language.equals("hi")){
                QUERY = "SELECT * FROM "+TABLE_NAME_HINDI+" WHERE nid="+id;
            }else if (language.equals("en")){
                QUERY = "SELECT * FROM "+TABLE_NAME_ENGLISH+" WHERE nid="+id;
            }
            Cursor cursor = db.rawQuery(QUERY,null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    news.setId(cursor.getInt(0));
                    news.setNid(cursor.getInt(1));
                    news.settitle(cursor.getString(2));
                    news.setnews(cursor.getString(3));
                    news.settime(cursor.getString(4));
                    news.setdate(cursor.getString(5));
                    news.setStatus(cursor.getString(6));
                    news.setpath(cursor.getString(7));
                    news.setpath1(cursor.getString(8));
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return news;
    }

    @Override
    public ArrayList<News> getAllNews(String status) {
        language = LocalizationActivity.getLanguage();
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY=null;
        ArrayList<News> newsList = null;
        try{
            newsList = new ArrayList<News>();
            if (language.equals("ma")){
                QUERY = "SELECT * FROM "+TABLE_NAME_MARATHI+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }else if (language.equals("hi")){
                QUERY = "SELECT * FROM "+TABLE_NAME_HINDI+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }else if (language.equals("en")){
                QUERY = "SELECT * FROM "+TABLE_NAME_ENGLISH+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }
            Cursor cursor = db.rawQuery(QUERY,null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    News news = new News();
                    news.setId(cursor.getInt(0));
                    news.setNid(cursor.getInt(1));
                    news.settitle(cursor.getString(2));
                    news.setnews(cursor.getString(3));
                    news.settime(cursor.getString(4));
                    news.setdate(cursor.getString(5));
                    news.setStatus(cursor.getString(6));
                    news.setpath(cursor.getString(7));
                    news.setpath1(cursor.getString(8));

                    newsList.add(news);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return newsList;
    }

    @Override
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_CATID, category.getCat_id());
            values.put(KEY_CATNAME, category.getCat_name());
            values.put(KEY_LANGUAGE, category.getLang_status());
            values.put(KEY_CATPRIORITY, category.getPriority());

                db.insert(TABLE_NAME_CATEGORY, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    public void addBanner(int id,String path,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ADDSID, id);
            values.put(KEY_ADDSPATH, path);
            values.put(KEY_ADDSSTATUS, status);

            db.insert(TABLE_NAME_ADVERTISE, null, values);
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    public void updateCategory(Category category){
        SQLiteDatabase db = this.getWritableDatabase();

        try{
            ContentValues values = new ContentValues();
            values.put(KEY_CATID, category.getCat_id());
            values.put(KEY_CATNAME, category.getCat_name());
            values.put(KEY_LANGUAGE, category.getLang_status());
            values.put(KEY_CATPRIORITY, category.getPriority());

           // db.insert(TABLE_NAME_CATEGORY, null, values);
            db.update(TABLE_NAME_CATEGORY, values, KEY_CATID + " = ?",
                    new String[] { String.valueOf(category.getCat_id()) });
            db.close();
        }catch (Exception e){
            Log.e("problem",e+"");
        }
    }

    @Override
    public ArrayList<Category> getAllCategory(int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        String QUERY=null;
        ArrayList<Category> categoriesList = null;
        int i=0;
        try{
            categoriesList = new ArrayList<Category>();

                QUERY = "SELECT * FROM "+TABLE_NAME_CATEGORY+" WHERE lang_status='"+status+"'"+" ORDER BY cat_priority asc";

            Cursor cursor = db.rawQuery(QUERY,null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {
                    Category category = new Category();
                    category.setId(cursor.getInt(0));
                    category.setCat_id(cursor.getInt(1));
                    category.setCat_name(cursor.getString(2));
                    category.setLang_status(cursor.getInt(3));

                    categoriesList.add(category);
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return categoriesList;
    }


    @Override
    public int getNewsCount(String status) {
        int num = 0;
        String QUERY=null;
        language = LocalizationActivity.getLanguage();
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            if (language.equals("ma")){
                QUERY = "SELECT * FROM "+TABLE_NAME_MARATHI+" WHERE status='"+status+"'";
            }else if (language.equals("hi")){
                QUERY = "SELECT * FROM "+TABLE_NAME_HINDI+" WHERE status='"+status+"'";
            }else if (language.equals("en")){
                QUERY = "SELECT * FROM "+TABLE_NAME_ENGLISH+" WHERE status='"+status+"'";
            }
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }

    public int[] getIdArray(String status){
        int[] result = new int[getNewsCount(status)];
        String QUERY=null;
        int i=0;
        language = LocalizationActivity.getLanguage();
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            if (language.equals("ma")){
                QUERY = "SELECT nid FROM "+TABLE_NAME_MARATHI+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }else if (language.equals("hi")){
                QUERY = "SELECT nid FROM "+TABLE_NAME_HINDI+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }else if (language.equals("en")){
                QUERY = "SELECT nid FROM "+TABLE_NAME_ENGLISH+" WHERE status='"+status+"'"+" ORDER BY date desc,time desc";
            }
            Cursor cursor = db.rawQuery(QUERY, null);
            if(!cursor.isLast())
            {
                while (cursor.moveToNext())
                {

                    result[i] = cursor.getInt(0);
                    i++;
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return result;
    }

    public int getCategoryCount(int status) {
        int num = 0;
        String QUERY=null;
        SQLiteDatabase db = this.getReadableDatabase();
        try{
                QUERY = "SELECT * FROM "+TABLE_NAME_CATEGORY+" WHERE lang_status='"+status+"'";
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }
    public int getCategoryCount() {
        int num = 0;
        int status=1;
        language = LocalizationActivity.getLanguage();
        if (language.equals("ma")){
            status =1;
        }else if (language.equals("hi")){
            status=2;
        }else if (language.equals("en")){
            status =3;
        }
        String QUERY=null;

        SQLiteDatabase db = this.getReadableDatabase();
        try{

                QUERY = "SELECT * FROM "+TABLE_NAME_CATEGORY+" WHERE lang_status='"+status+"'";

            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return 0;
    }
    @Override
    public boolean getIdNews(int news_id) {
        int num = 0;
        String QUERY=null;
        boolean result=false;
        language = LocalizationActivity.getLanguage();
        SQLiteDatabase db = this.getReadableDatabase();
        try{
            if (language.equals("ma")){
                QUERY = "SELECT * FROM "+TABLE_NAME_MARATHI+" WHERE nid="+news_id;
            }else if (language.equals("hi")){
                QUERY = "SELECT * FROM "+TABLE_NAME_HINDI+" WHERE nid="+news_id;
            }else if (language.equals("en")){
                QUERY = "SELECT * FROM "+TABLE_NAME_ENGLISH+" WHERE nid='"+news_id+"'";
            }
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();

            if (num>0){
                System.out.println("iffffffff:"+news_id);
                result = true;
            }else {
                System.out.println("elssss:"+news_id);
                result = false;
            }
            db.close();
            return result;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return result;
    }

    public boolean getIdCategory(int cat_id) {
        int num = 0;
        String QUERY=null;
        boolean result=false;
        SQLiteDatabase db = this.getReadableDatabase();
        try{

           QUERY = "SELECT * FROM "+TABLE_NAME_CATEGORY+" WHERE cat_id="+cat_id;

            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();

            if (num>0){
                System.out.println("iffffffff:"+cat_id);
                result = true;
            }else {
                System.out.println("elssss:"+cat_id);
                result = false;
            }
            db.close();
            return result;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return result;
    }

    public boolean getIdBanner(int id,int status) {
        int num = 0;
        String QUERY=null;
        boolean result=false;
        SQLiteDatabase db = this.getReadableDatabase();
        try{

            QUERY = "SELECT * FROM "+TABLE_NAME_ADVERTISE+" WHERE adds_id="+id+" and adds_status="+status;

            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();

            if (num>0){
                System.out.println("iffffffff:"+id);
                result = true;
            }else {
                System.out.println("elssss:"+id);
                result = false;
            }
            db.close();
            return result;
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return result;
    }

    public String getBanner() {
        int num = 0;
        String QUERY=null;
        String path = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try{

            QUERY = "SELECT * FROM "+TABLE_NAME_ADVERTISE;

            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            System.out.println("Counttttt:"+num);
            if (num>0){
                Random rand = new Random();

                int randomNum = rand.nextInt((num - 1) + 1) + 1;
                System.out.println("randomNum:"+randomNum);
                QUERY = "SELECT * FROM "+TABLE_NAME_ADVERTISE+" WHERE id="+randomNum;
                Cursor cursor1 = db.rawQuery(QUERY,null);
                if(!cursor1.isLast())
                {
                    while (cursor1.moveToNext())
                    {

                        path = cursor1.getString(2);

                    }
                }
            }
            db.close();
        }catch (Exception e){
            Log.e("error",e+"");
        }
        return path;
    }

}
