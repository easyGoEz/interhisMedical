package com.witnsoft.interhis.rightpage.westernmedical;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.WesternDetailModel;
import com.witnsoft.interhis.rightpage.chinesemedical.ChineseMedicalTopAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/7/4.
 */

public class WesternMedicalTopAdapter extends BaseAdapter {
    private List<WesternDetailModel> list = new ArrayList<WesternDetailModel>();
    private Context context;

    public WesternMedicalTopAdapter(Context context, List<WesternDetailModel> list) {
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
        WesternMedicalTopAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_western_med_top, null);
            holder = new WesternMedicalTopAdapter.ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }
        holder = (WesternMedicalTopAdapter.ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(list.get(position).getCmc())) {
            holder.tvName.setText(list.get(position).getCmc());
        } else {
            holder.tvName.setText("");
        }
        String content = "";
        if (!TextUtils.isEmpty(list.get(position).getAwGgMc())) {
            content = list.get(position).getAwGgMc();
        }
        if (!TextUtils.isEmpty(list.get(position).getSl())) {
            content = content + "×" + list.get(position).getSl() + context.getResources().getString(R.string.day);
        }
        holder.tvCount.setText(content);
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvCount;
    }
}
