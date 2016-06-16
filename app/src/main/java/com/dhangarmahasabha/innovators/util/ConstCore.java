package com.dhangarmahasabha.innovators.util;

import android.graphics.Bitmap;

import com.dhangarmahasabha.innovators.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by AD on 23-Feb-16.
 */
public class ConstCore {

    public static  String TAG_SUCCESS = "success";
    public static  String TAG_NID = "id";
    public static  String TAG_news = "news";
    public static  String TAG_TITLE = "title";
    public static  String TAG_NEWSS = "news";
    public static  String TAG_TIME = "time";
    public static  String TAG_DATE = "date";
    public static  String TAG_PATH = "path";
    public static  String TAG_PATH1 = "path1";
    public static  String TAG_STATUS = "status";
    public static  String TAG_LANGUAGE = "language";
    public static  String POST = "POST";
    public static  String TAG_BANNER = "advertise";

    public static boolean appendNotificationMessages = true;
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;


    public static final DisplayImageOptions UIL_DEFAULT_DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
            .cacheInMemory(true).imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).cacheOnDisc(true).
                    considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

}
