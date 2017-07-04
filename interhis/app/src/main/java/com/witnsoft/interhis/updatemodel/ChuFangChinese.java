package com.witnsoft.interhis.updatemodel;

import com.witnsoft.interhis.db.model.ChineseDetailModel;

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

    public void setList(List<ChineseDetailModel> list) {
        jsonArray = new JSONArray();
        for (int i = 0; i < list.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cmc", list.get(i).getCmc())
                        .put("sl", list.get(i).getSl())
                        .put("cdm", list.get(i).getCdm())
                        .put("dj", list.get(i).getDj());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }
    }

//    @Override
//    public void setHelperId(String helperId) {
//        this.acid = helperId;
//    }

    public JSONObject fromJSON(List<ChineseDetailModel> list, String aiid, String zdsm, String acmxs, String acsm, String je) {
        setList(list);
//        this.acmxs = acmxs;
//        this.acsm = acsm;
//        this.zdsm = zdsm;
//        this.aiid = aiid;
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


}



