package cn.runhe.dkitandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by runhe on 2015/12/17.
 */
public class SPUtil {

    public static void putString(Context ctx,String key,String value) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("dkitandroid.txt", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key,value);
        edit.commit();
    }

    public static String getString(Context ctx,String key) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences("dkitandroid.txt", Context.MODE_PRIVATE);
       return  sharedPreferences.getString(key,"");
    }
}
