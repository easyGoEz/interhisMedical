package com.witnsoft.interhis.tool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.witnsoft.interhis.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ${liyan} on 2017/6/2.
 */

public class DBManager {
    private final int BUFFER_SIZE=400000;
    private static String PACKAGE_NAME="com.chinese_medical.list";
    public static final String DB_NAME="chinese_city.db";
    //存放路径
    public static final String DB_PATH="/data"+ Environment.getDataDirectory().getAbsolutePath()+"/"+PACKAGE_NAME ;
    private Context context;
    private SQLiteDatabase database;
    public DBManager(Context context){
        this.context=context;
    }

    /**
     * 被调用方法
     */
    public void openDateBase(){
        this.database=this.openDateBase(DB_PATH + "/" + DB_NAME);
    }

    /**
     * 打开数据库
     * @param s
     * @return
     */
    private SQLiteDatabase openDateBase(String s) {
        File file=new File(s);
        if (!file.exists()){
            //打开assets中的数据库文件，获得stream流
            InputStream stream=this.context.getResources().openRawResource(R.raw.chinese_medical);
            try {
                FileOutputStream outputStream=new FileOutputStream(s);
                byte[] buffer=new byte[BUFFER_SIZE];
                int count=0;
                while ((count=stream.read(buffer))>0){
                    outputStream.write(buffer,0,count);
                }
                outputStream.close();
                stream.close();
                SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(s,null);
                return db;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return database;
    }

    public void closeDatabase(){
        if (database!=null&&database.isOpen()){
            this.database.close();
        }
    }
}
