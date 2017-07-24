package com.witnsoft.interhis.rightpage.chinesemedical.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

public class ChineseMedSearchAdapter extends BaseAdapter {
    private List<ChineseDetailModel> list = new ArrayList<ChineseDetailModel>();
    private Context context;

    public ChineseMedSearchAdapter(Context context, List<ChineseDetailModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChineseMedSearchAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null);
            holder = new ChineseMedSearchAdapter.ViewHolder();
            holder.tv_ss = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        }
        holder = (ChineseMedSearchAdapter.ViewHolder) convertView.getTag();
        holder.tv_ss.setText(list.get(position).getCmc());
        return convertView;
    }

    class ViewHolder {
        TextView tv_ss;
    }
}
