package com.dhangarmahasabha.innovators.tool;

/**
 * Created by AD on 15-May-16.
 */
public interface ObservableScrollViewCallbacks {

    void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging);


    void onDownMotionEvent();


    void onUpOrCancelMotionEvent(ScrollState scrollState);
}