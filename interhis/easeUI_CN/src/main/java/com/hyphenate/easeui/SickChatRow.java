package com.hyphenate.easeui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liyan on 2017/5/26.
 */

public class SickChatRow extends EaseChatRow {

    private TextView sickNameTv, sickSexTv, sickAgeTv, sickNumber, sickHeight, sickWeight, sickConditionTv, sickHope, sickPay, jzTv;
    private ImageView sickImg;


    public SickChatRow(Context context, EMMessage message, int position, BaseAdapter adapter, String imgDoc, String imgPat) {
        super(context, message, position, adapter, imgDoc, imgPat);
    }

    @Override
    protected void onInflateView() {

        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_sick
                : R.layout.ease_row_sent_sick, this);

    }

    @Override
    protected void onFindViewById() {
        sickNameTv = (TextView) findViewById(R.id.tv_sick_name);
        sickSexTv = (TextView) findViewById(R.id.tv_sick_sex);
        sickAgeTv = (TextView) findViewById(R.id.tv_sick_age);
        sickNumber = (TextView) findViewById(R.id.tv_sick_number);
        sickConditionTv = (TextView) findViewById(R.id.tv_sick_condition);
        sickHeight = (TextView) findViewById(R.id.tv_sick_height);
        sickWeight = (TextView) findViewById(R.id.tv_sick_weight);
        sickHope = (TextView) findViewById(R.id.tv_sick_hope);
        sickPay = (TextView) findViewById(R.id.tv_pay_state);
        jzTv = (TextView) findViewById(R.id.tv_jiezhen);

        jzTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "开始接诊", Toast.LENGTH_SHORT).show();
            }
        });

        sickImg = (ImageView) findViewById(R.id.sick_picture);

    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onSetUpView() {
        Map<String, Object> extMap = message.ext();

        String paytypemc = (String) extMap.get("paytypemc");
        sickPay.setText(paytypemc);

        Gson gson = new Gson();
        Map<String, Object> objectMap = new HashMap<String, Object>();
        String content = (String) extMap.get("content");
        Map<String, Object> contentMap = gson.fromJson(content, objectMap.getClass());

        Map<String, Object> patinfoMap = (Map<String, Object>) contentMap.get("patinfo");
        String patname = (String) patinfoMap.get("patname");
        String patsexname = (String) patinfoMap.get("patsexname");
        String patsgmc = (String) patinfoMap.get("patsgmc");
        String patnlmc = (String) patinfoMap.get("patnlmc");
        String mobile = (String) patinfoMap.get("mobile");
        String pattzmc = (String) patinfoMap.get("pattzmc");
        sickNameTv.setText(patname);
        sickSexTv.setText(patsexname);
        sickAgeTv.setText(patnlmc);
        sickNumber.setText(mobile);
        sickHeight.setText(patsgmc);
        sickWeight.setText(pattzmc);

        String jbmc = (String) contentMap.get("jbmc");
        sickConditionTv.setText(jbmc);
        String hope = (String) contentMap.get("hope");
        sickHope.setText(hope);


        List<String> piclist = (List<String>) contentMap.get("piclist");
        for (int i = 0; i < piclist.size(); i++) {
            Glide.with(context).load(piclist.get(i)).into(sickImg);
        }
        Log.e("piclist.size()", "piclist.size():" + piclist.size());


        // Log.e("patinfo!!!!!!!!!!", content);

    }

    @Override
    protected void onBubbleClick() {

    }
}
