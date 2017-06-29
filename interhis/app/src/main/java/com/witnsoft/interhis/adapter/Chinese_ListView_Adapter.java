package com.witnsoft.interhis.adapter;

import android.content.Context;
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
 * Created by ${liyan} on 2017/5/17.
 */

public class Chinese_ListView_Adapter extends BaseAdapter {

    private List<ChineseDetailModel> list=new ArrayList<ChineseDetailModel>();
    private Context context;
    private FilterListener listener=null;//接口对象

    public Chinese_ListView_Adapter(List<ChineseDetailModel> list, Context context, FilterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public void delete(){
        list.clear();
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
        ViewHolder holder=null;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.fragment_helper_chinese_listview_item,null);
            holder=new ViewHolder();
            holder.tv_ss= (TextView) convertView.findViewById(R.id.item_text);
            convertView.setTag(holder);
        }
        holder= (ViewHolder) convertView.getTag();
        holder.tv_ss.setText(list.get(position).getCmc());
        return convertView;
    }

    class ViewHolder{
        TextView tv_ss;

    }
}
