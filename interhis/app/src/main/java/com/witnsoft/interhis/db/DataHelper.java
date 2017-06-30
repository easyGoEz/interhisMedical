package com.witnsoft.interhis.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by ${liyan} on 2017/6/8.
 */

public class DataHelper {
    private static int DB_VERSION = 24;
    private SQLiteDatabase db;
    private YaoListDBHelper yaoListDBHelper;

    private static DataHelper dataHelper;


    //静态方法，获得私有化的那个本类对象，如果对象为空则创建，不为空则直接返回该对象
    public static DataHelper getInstance(Context context) {
        if (dataHelper == null) {

            synchronized (DataHelper.class) {
                if (dataHelper == null) {
                    dataHelper = new DataHelper(context);
                }
            }
        }
        return dataHelper;
    }

    public DataHelper(Context context) {
        yaoListDBHelper = new YaoListDBHelper(context, null, DB_VERSION);

        db = yaoListDBHelper.getWritableDatabase();
    }

    public void close() {
        db.close();
        yaoListDBHelper.close();
    }

    //根据拼音进行查询
    public Cursor getXMRJ(String keyword) {
        Log.e("11111", "getXMRJ: " + keyword);
        Cursor cursor = db.rawQuery("select * from " + YaoListDBHelper.YAO_TB_NAME + " where sfdlbm='003' and xmrj like ? ", new String[]{"%" + keyword + "%"});
        Log.e("2222", "getXMRJ: " + cursor.getCount());
        return cursor;
    }

    //根据名称进行查询
    public Cursor getFixedMed(String keyword) {
        Cursor cursor = db.rawQuery("select * from " + YaoListDBHelper.YAO_TB_NAME + " where sfdlbm='003' and xmmc=?", new String[]{keyword});
        return cursor;
    }
}
