package com.example.guoshijie.studyword.HttpHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkUtil {

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    private static int ret;
    public static int isNetworkAvailable(Context context) {

        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec("ping -c 3 www.baidu.com");
            ret = p.waitFor();
//            Log.d("Avalible", "Process:"+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

}