package com.witnsoft.interhis.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by ${liyan} on 2017/6/8.
 */

public class YaoListDBHelper extends SQLiteOpenHelper {
    private OnDbOpened onDbOpened;
    private Context mContext;

    private static String DB_NAME = "YaoList.db";
    public static final String YAO_TB_NAME = "K_BG02_NEW";

    public YaoListDBHelper(Context context, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, version);
        mContext = context;
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        Log.d("YaoistDBHelper", "running for conConfigure");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        Log.d("YaoistDBHelper", "running for onOpen");
        // 数据库已打开
        if (null != onDbOpened) {
            onDbOpened.onDbOpened();
        }
    }

    /**
     * 数据库第一次创建时调用
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //executeAssetsSQL(db, "schema.sql");
        if (null != onDbOpened) {
            onDbOpened.onDbCreate();
        }
        Log.e("YaoListDBHelper", "running for onCreate");
        db.execSQL("CREATE TABLE IF NOT EXISTS " +
                YAO_TB_NAME + "(" +
                "yaoid integer primary key, " +
                "sfxmbm  varchar, " +
                "xmmc varchar, " +
                "xmrj varchar, " +
                "sfdlbm varchar, " +
                "bzjg varchar)");
        initYaoList(db, "k_bg02_new.sql");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("YaoListDBHelper", "running for onUpgrade");
//        initYaoList(db, "k_bg02_new.sql");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("YaoListDBHelper", "running for onDowngrade");
//        initYaoList(db, "k_bg02_new.sql");

    }

    //初始化药品列表数据
    public void initYaoList(SQLiteDatabase db, String schemaName) {
        BufferedReader in = null;
//        db.execSQL("CREATE TABLE IF NOT EXISTS " +
//                YAO_TB_NAME + "(" +
//                "yaoid integer primary key, " +
//                "sfxmbm  varchar, " +
//                "xmmc varchar, " +
//                "xmrj varchar, " +
//                "sfdlbm varchar, " +
//                "bzjg varchar)");

//        db.execSQL("delete from "+YAO_TB_NAME);

        try {
            in = new BufferedReader(new InputStreamReader(mContext.getAssets().open(schemaName)));
            String line;
            String buffer = "";
            while ((line = in.readLine()) != null) {
                if (!line.trim().endsWith("..")) {
                    buffer += line;
                }
                if (line.trim().endsWith(";")) {
                    Log.e("YaoListDBHelper", buffer.replace(";", ""));
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
        } catch (IOException e) {
            Log.e("db-error", e.toString());
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                Log.e("db-error", e.toString());
            }
        }
//        db.close();
    }

    public void setOnDbOpened(OnDbOpened onDbOpened) {
        this.onDbOpened = onDbOpened;
    }
}




