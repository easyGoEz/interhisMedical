package com.hyphenate.easeui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by liyan on 2017/5/9.
 */

public class MyEaseChatRowProject extends EaseChatRow {

    private TextView userName, yaofangType, yaofangNum, yaoNum, yaofangPrice, searchContent;
    private ImageView yaofangIv;

    public MyEaseChatRowProject(Context context, EMMessage message, int position, BaseAdapter adapter, String imgDoc, String imgPat) {
        super(context, message, position, adapter, imgDoc, imgPat);
        // TODO Auto-generated constructor stub
    }

    /**
     * 注入布局a
     */
    @Override
    protected void onInflateView() {
        // TODO Auto-generated method stub
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ? R.layout.ease_row_received_project
                : R.layout.ease_row_sent_project, this);
    }

    /**
     * 寻找id
     */
    @Override
    protected void onFindViewById() {
        userName = (TextView) findViewById(R.id.tv_yaofang_name);
        yaofangType = (TextView) findViewById(R.id.tv_yaofang_type);
        yaofangIv = (ImageView) findViewById(R.id.iv_yaofang);
        yaofangNum = (TextView) findViewById(R.id.tv_yaofang_num);
        yaoNum = (TextView) findViewById(R.id.tv_yao_num);
        yaofangPrice = (TextView) findViewById(R.id.tv_yaofang_price);
        yaofangIv = (ImageView) findViewById(R.id.iv_yaofang);
        searchContent = (TextView) findViewById(R.id.tv_searchcontent);
        searchContent.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    /**
     * 刷新列表视图
     */
    @Override
    protected void onUpdateView() {
        // TODO Auto-generated method stub
        adapter.notifyDataSetChanged();
    }

    /**
     * 显示消息和位置等属性
     */
    @Override
    protected void onSetUpView() {

        if (message.getBooleanAttribute("yaofang", true)) {
            String userName = message.getStringAttribute("userName", null);
            String yaofangType = message.getStringAttribute("yaofangType", null);
            String yaofangNum = message.getStringAttribute("yaofangNum", null);
            String yaoNum = message.getStringAttribute("yaoNum", null);
            String yaofangPrice = message.getStringAttribute("yaofangPrice", null);

            this.userName.setText("您给" + userName + "开了一个");
            this.yaofangType.setText(yaofangType + "处方");
            this.yaofangNum.setText("药方号：" + yaofangNum);
            this.yaoNum.setText("共" + yaoNum + "付");
            this.yaofangPrice.setText(yaofangPrice + "元");

        }

    }

    /**
     * 点击气泡
     */
    @Override
    protected void onBubbleClick() {
        Intent intent=new Intent("MingXi");
        getContext().sendBroadcast(intent);

    }

}
