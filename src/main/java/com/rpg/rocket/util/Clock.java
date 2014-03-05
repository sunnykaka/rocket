package com.rpg.rocket.util;

/**
 * User: liubin
 * Date: 14-2-28
 */
public class Clock {


    public static long nowInMillisecond() {
        return System.currentTimeMillis();
    }

    public static boolean isTimeout(long timeout) {
        if(timeout <= 0) return false;
        return timeout < nowInMillisecond();
    }

}
