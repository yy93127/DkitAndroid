package cn.runhe.dkitandroid.utils;

import android.util.Log;

/**
 * Created by runhe on 2015/12/17.
 */
public class LogUtil {
    private static boolean isdebug = true;

    public static void i(Object obj, String msg) {
        if (isdebug) {
            Log.i(obj.toString(), msg);
        }
    }
}
