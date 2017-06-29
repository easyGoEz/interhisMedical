package com.witnsoft.interhis.setting.myhistory;

import android.content.Context;
import android.text.TextUtils;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ComRecyclerViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

public class MyHistoryAdapter extends ComRecyclerAdapter<String> {

    private Context context;

    public MyHistoryAdapter(Context context, List<String> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(ComRecyclerViewHolder comRecyclerViewHolder, String item) {
        if (!TextUtils.isEmpty(item)) {
            comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_name, item);
        } else {
            comRecyclerViewHolder.setText(R.id.fragment_doctor_recycleView_item_name, "");
        }
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
