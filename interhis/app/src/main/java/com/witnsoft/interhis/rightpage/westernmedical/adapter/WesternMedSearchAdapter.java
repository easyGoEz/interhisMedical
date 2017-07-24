package com.witnsoft.interhis.rightpage.westernmedical.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.WesternDetailModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/7/4.
 */

public class WesternMedSearchAdapter extends BaseAdapter {
    private List<WesternDetailModel> list = new ArrayList<WesternDetailModel>();
    private Context context;

    public WesternMedSearchAdapter(Context context, List<WesternDetailModel> list) {
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
        WesternMedSearchAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_western_search, null);
            holder = new WesternMedSearchAdapter.ViewHolder();
            holder.tv_ss = (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        }
        holder = (WesternMedSearchAdapter.ViewHolder) convertView.getTag();
        String content = "";
        if (!TextUtils.isEmpty(list.get(position).getCmc())) {
            content = list.get(position).getCmc();
        }
        if (!TextUtils.isEmpty(list.get(position).getAwGgMc())) {
            content = content + "（" + list.get(position).getAwGgMc() + "）";
        }
        holder.tv_ss.setText(content);
        return convertView;
    }

    class ViewHolder {
        TextView tv_ss;
    }
}
