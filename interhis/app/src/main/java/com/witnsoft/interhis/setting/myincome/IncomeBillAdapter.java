package com.witnsoft.interhis.setting.myincome;

import android.content.Context;
import android.text.TextUtils;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ComRecyclerViewHolder;

import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/7/13.
 */

public class IncomeBillAdapter extends ComRecyclerAdapter<Map<String, String>> {

    private Context context;

    public IncomeBillAdapter(Context context, List<Map<String, String>> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(ComRecyclerViewHolder comRecyclerViewHolder, Map<String, String> item) {
        comRecyclerViewHolder.setText(R.id.tv_time, item.get("time"));
        comRecyclerViewHolder.setText(R.id.tv_name, item.get("name"));
        if (!TextUtils.isEmpty(item.get("count"))) {
            comRecyclerViewHolder.setText(R.id.tv_count, "+" + item.get("count") + "¥");
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
