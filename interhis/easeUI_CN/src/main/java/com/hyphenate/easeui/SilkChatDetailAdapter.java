package com.hyphenate.easeui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/7/18.
 */

public class SilkChatDetailAdapter extends BaseAdapter {
    private Context context;
    private List<String> list = new ArrayList<>();

    public SilkChatDetailAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        try {
            return list.size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        try {
            return list.get(position);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SilkChatDetailAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_silk_chat_detail, null);
            holder = new SilkChatDetailAdapter.ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.iv_img);
            convertView.setTag(holder);
        }
        holder = (SilkChatDetailAdapter.ViewHolder) convertView.getTag();

        Glide.with(context).load(list.get(position))
                .centerCrop().error(R.drawable.img_default).into(holder.imageView);

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
    }
}
