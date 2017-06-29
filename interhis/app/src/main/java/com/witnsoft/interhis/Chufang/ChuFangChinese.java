package com.witnsoft.interhis.Chufang;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.setting.myinfo.MyInfoFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${liyan} on 2017/6/14.
 */

public class ChuFangChinese extends ChuFangBase {

    private static final String TAG = "ChuFangChinese";
    private String acmxs,acsm,zdsm,aiid,acid;
    JSONArray jsonArray;

    public void setList(List<ChineseDetailModel> list) {
        jsonArray=new JSONArray();
        for (int i = 0;i<list.size();i++){
            JSONObject jsonObject=new JSONObject();
            try {
                jsonObject.put("cmc",list.get(i).getCmc())
                          .put("sl", list.get(i).getSl())
                          .put("cdm",list.get(i).getCdm())
                          .put("dj",list.get(i).getDj());
            }catch (JSONException e){
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
    }

    @Override
    public void setHelperId(String helperId) {
        this.acid=helperId;
    }

    @Override
    public void setAcmxs(String acmxs) {
        this.acmxs = acmxs;
    }

    @Override
    public void setAcsm(String acsm) {
        this.acsm = acsm;
    }

    @Override
    public void setZdsm(String zdsm) {
        this.zdsm = zdsm;
    }

    @Override
    public void setAiid(String aiid) {
        this.aiid = aiid;
    }

    public JSONObject fromJSON(List<ChineseDetailModel> list,String acmxs,String acsm,String zdsm,String aiid) {
        setList(list);
        setAcmxs(acmxs);
        setAcsm(acsm);
        setZdsm(zdsm);
        setAiid(aiid);
        // DATA
        JSONObject dataJo = new JSONObject();
        try {
            dataJo.put("yftype", "chinese")
                    .put("aiid",aiid)
                    .put("zdsm",zdsm)
                    .put("acmxs",acmxs)
                    .put("acsm",acsm)
                    .put("je", "1293")
                    .put("chufangmx",jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
            return dataJo;
    }


    }



