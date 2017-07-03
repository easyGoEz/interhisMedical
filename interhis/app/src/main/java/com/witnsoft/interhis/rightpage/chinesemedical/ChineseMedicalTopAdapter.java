package com.witnsoft.interhis.rightpage.chinesemedical;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.inter.FilterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/6/30.
 */

public class ChineseMedicalTopAdapter extends BaseAdapter {
    private List<ChineseDetailModel> list = new ArrayList<ChineseDetailModel>();
    private Context context;
    private double moneyAmount;

    public ChineseMedicalTopAdapter(Context context, List<ChineseDetailModel> list) {
        this.context = context;
        this.list = list;
        this.moneyAmount = 0.0;
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
        ChineseMedicalTopAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chinese_med_top, null);
            holder = new ChineseMedicalTopAdapter.ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }
        holder = (ChineseMedicalTopAdapter.ViewHolder) convertView.getTag();
        if (!TextUtils.isEmpty(list.get(position).getCmc())) {
            holder.tvName.setText(list.get(position).getCmc());
        } else {
            holder.tvName.setText("");
        }
        if (!TextUtils.isEmpty(list.get(position).getSl())) {
            holder.tvCount.setText(list.get(position).getSl() + "g");
        } else {
            holder.tvCount.setText("");
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvCount;
    }
}
