package com.witnsoft.interhis.utils;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;

/**
 * Created by zhengchengpeng on 2017/6/15.
 */

public class PermissionUtil {
    public PermissionUtil() {
    }

    public static void requestPermission(Activity activity, String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
