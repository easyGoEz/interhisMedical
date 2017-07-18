package com.hyphenate.easeui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/7/18.
 */

public class SickChatDetailDialog {

    private Context context;
    private AlertDialog ad;
    private GridView gvImage;
    private ArrayList<String> list = new ArrayList<>();
    private SilkChatDetailAdapter adapter = null;

    public SickChatDetailDialog(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    public void init() {
        // 是否为编辑弹出框
        ad = new AlertDialog.Builder(context).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.dialog_silk_chat_detail);
        gvImage = (GridView) window.findViewById(R.id.gv_image);
        initUi();
    }

    private void initUi() {
        adapter = new SilkChatDetailAdapter(context, list);
        gvImage.setAdapter(adapter);
        gvImage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, SilkImageActivity.class);
                intent.putStringArrayListExtra("imgList", list);
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
    }
}
