package cn.missfresh.geapplication.utils;

import android.util.Log;

/**
 * Created by gchen on 16/3/3.
 */
public class Logger {
    private static final boolean LOG_ENABLE = true;

    public static void e(String tag, String msg) {
        if (LOG_ENABLE) {
            Log.d(tag, msg);
        }
    }
}
