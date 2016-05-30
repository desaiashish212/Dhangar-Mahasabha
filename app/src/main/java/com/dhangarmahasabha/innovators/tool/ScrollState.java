package com.dhangarmahasabha.innovators.tool;

/**
 * Created by AD on 15-May-16.
 */
public enum ScrollState {
    /**
     * Widget is stopped.
     * This state does not always mean that this widget have never been scrolled.
     */
    STOP,

    /**
     * Widget is scrolled up by swiping it down.
     */
    UP,

    /**
     * Widget is scrolled down by swiping it up.
     */
    DOWN,
}