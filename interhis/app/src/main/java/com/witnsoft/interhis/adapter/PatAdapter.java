package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ComRecyclerViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/2.
 */

public class PatAdapter extends ComRecyclerAdapter<Map<String, String>> {

    private Context context;
    private int pos = -1;
    private int unread;

    public PatAdapter(Context context, List<Map<String, String>> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(ComRecyclerViewHolder comRecyclerViewHolder, Map<String, String> item) {
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_name, item.get("PATNAME"));
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_age, item.get("PATNL"));
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_sex, item.get("PATSEXNAME"));
        comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_content, item.get("JBMC"));
        if (!TextUtils.isEmpty(item.get("color"))) {
            if ("changed".equals(item.get("color"))) {
                comRecyclerViewHolder.setBackgroundColor(R.id.ll_back, R.color.colorWhite);
            } else {
                comRecyclerViewHolder.setBackgroundColor(R.id.ll_back, R.color.colorGray);
            }
        } else {
            comRecyclerViewHolder.setBackgroundColor(R.id.ll_back, R.color.colorGray);
        }
        int unReadNumber = 0;
        if (!TextUtils.isEmpty(item.get("readNo"))) {
            try {
                unReadNumber = Integer.valueOf(item.get("readNo"));
            } catch (ClassCastException e) {
                unReadNumber = 0;
            }
        } else {
            unReadNumber = 0;
        }
        if (0 < unReadNumber) {
            comRecyclerViewHolder.setVisible(R.id.tv_read, true);
            comRecyclerViewHolder.setText(R.id.tv_read, String.valueOf(unReadNumber));
        } else {
            comRecyclerViewHolder.setVisible(R.id.tv_read, false);
        }
        comRecyclerViewHolder.setImageUrl(context, R.id.fragment_doctor_recycleView_item_image, item.get("PHOTOURL"), R.drawable.touxiang);
    }

    @Override
    public void convertHeader(ComRecyclerViewHolder comRecyclerViewHolder) {

    }

    @Override
    public void convertFooter(ComRecyclerViewHolder comRecyclerViewHolder) {
        if (!canNotReadBottom) {
            comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_done));
        } else {
            comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_more));
        }
    }
}
