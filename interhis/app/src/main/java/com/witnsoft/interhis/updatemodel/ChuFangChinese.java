package com.witnsoft.interhis.updatemodel;

import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.WesternDetailModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ${liyan} on 2017/6/14.
 */

public class ChuFangChinese extends ChuFangBase {

    private static final String TAG = "ChuFangChinese";
    JSONArray jsonArray;

    public void setChineseList(List<ChineseDetailModel> list) {
        jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmc", list.get(i).getCmc())
                        .put("sl", list.get(i).getSl())
                        .put("cdm", list.get(i).getCdm())
                        .put("dj", list.get(i).getDj())
                        .put("je", list.get(i).getJe());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
    }

    public void setWesternList(List<WesternDetailModel> list) {
        jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("awmc", list.get(i).getCmc())
                        .put("awsl", list.get(i).getSl())
                        .put("awdm", list.get(i).getAwDm())
                        .put("dj", list.get(i).getDj())
                        .put("je", list.get(i).getJe())
                        .put("awggmc", list.get(i).getAwGgMc())
                        .put("awggdm", list.get(i).getAwGgDm());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
    }

    public JSONObject fromJSONChinese(List<ChineseDetailModel> list, String aiid, String zdsm, String acmxs, String acsm, String je) {
        setChineseList(list);
        // DATA
        JSONObject dataJo = new JSONObject();
        try {
            dataJo.put("yftype", "chinese")
                    .put("aiid", aiid)
                    .put("zdsm", zdsm)
                    .put("acmxs", acmxs)
                    .put("acsm", acsm)
                    .put("je", je)
                    .put("chufangmx", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataJo;
    }

    public JSONObject fromJSONWestern(List<WesternDetailModel> list, String aiid, String zdsm, String acsm, String je) {
        setWesternList(list);
        // DATA
        JSONObject dataJo = new JSONObject();
        try {
            dataJo.put("yftype", "western")
                    .put("aiid", aiid)
                    .put("zdsm", zdsm)
                    .put("acsm", acsm)
                    .put("je", je)
                    .put("chufangmx", jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataJo;
    }


}



