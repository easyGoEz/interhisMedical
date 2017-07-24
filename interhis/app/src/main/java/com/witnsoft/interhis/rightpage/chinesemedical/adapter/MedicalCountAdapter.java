package com.witnsoft.interhis.rightpage.chinesemedical.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witnsoft.interhis.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

public class MedicalCountAdapter extends BaseAdapter {
    private List<String> list = new ArrayList<String>();
    private Context context;
    private String unit;

    public MedicalCountAdapter(Context context, List<String> list, String unit) {
        this.context = context;
        this.list = list;
        this.unit = unit;
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
        MedicalCountAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_search, null);
            holder = new MedicalCountAdapter.ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        }
        holder = (MedicalCountAdapter.ViewHolder) convertView.getTag();
        holder.tvName.setText(list.get(position) + unit);
        return convertView;
    }

    class ViewHolder {
        TextView tvName;

    }
}
