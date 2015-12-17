package cn.runhe.dkitandroid.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by runhe on 2015/12/17.
 */
public class ToastUtil {

    public static void showToast(Activity activity,String msg) {
        Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
    }
}
