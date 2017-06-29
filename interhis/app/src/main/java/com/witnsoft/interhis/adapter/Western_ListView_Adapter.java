package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.WesternDetailModel;
import com.witnsoft.interhis.inter.FilterListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${liyan} on 2017/6/13.
 */

public class Western_ListView_Adapter extends BaseAdapter {
    private List<WesternDetailModel> list=new ArrayList<WesternDetailModel>();
    private Context context;
    private FilterListener listener=null;//接口对象

    public Western_ListView_Adapter(List<WesternDetailModel> list, Context context, FilterListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
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
            convertView=LayoutInflater.from(context).inflate(R.layout.fragment_helper_western_listview_item,null);
            holder=new ViewHolder();
           holder.tv_ss= (TextView) convertView.findViewById(R.id.western_item_text);
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
