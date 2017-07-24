package com.witnsoft.interhis.setting.myhistory.adapter;

import android.content.Context;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ComRecyclerViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

public class MyHistoryAdapter extends ComRecyclerAdapter<Map<String, String>> {

    private Context context;

    public MyHistoryAdapter(Context context, List<Map<String, String>> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(ComRecyclerViewHolder comRecyclerViewHolder, Map<String, String> item) {
        // 姓名
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_name, item.get("PATNAME"));
        // 性别
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_sex, item.get("PATSEXNAME"));
        // 年龄
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_age, item.get("PATNL"));
        // 症状
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_content, item.get("JBMC"));
        // 头像
        comRecyclerViewHolder.setImageUrl(context, R.id.fragment_doctor_recycleView_item_image, item.get("PATPHOTOURL"), R.drawable.touxiang);
    }

    @Override
    public void convertHeader(ComRecyclerViewHolder comRecyclerViewHolder) {

    }

    @Override
    public void convertFooter(ComRecyclerViewHolder comRecyclerViewHolder) {
        try {
            if (!canNotReadBottom) {
                comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_done));
            } else {
                comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_more));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
