package com.hyphenate.easeui.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.easeui.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/7/7.
 */

public class ChatDetailAdapter extends BaseAdapter {
    private JSONArray jsonArray;
    private Context context;
    private JSONObject json;
    private int type = -1;

    public ChatDetailAdapter(Context context, JSONArray jsonArray, String type) {
        this.context = context;
        this.jsonArray = jsonArray;
        this.json = new JSONObject();
        if (!TextUtils.isEmpty(type)) {
            if ("中药".equals(type)) {
                this.type = 0;
            } else if ("西药".equals(type)) {
                this.type = 1;
            } else {
                this.type = -1;
            }
        } else {
            this.type = -1;
        }
    }

    @Override
    public int getCount() {
        try {
            return jsonArray.length();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        try {
            return jsonArray.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatDetailAdapter.ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_chat_detail, null);
            holder = new ChatDetailAdapter.ViewHolder();
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvCount = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        }
        holder = (ChatDetailAdapter.ViewHolder) convertView.getTag();
        try {
            json = jsonArray.getJSONObject(position);
            if (!TextUtils.isEmpty(json.getString("cmc"))) {
                holder.tvName.setText(json.getString("cmc"));
            } else {
                holder.tvName.setText("");
            }
            if (!TextUtils.isEmpty(json.getString("sl"))) {
                if (0 == this.type) {
                    // 中药
                    holder.tvCount.setText(json.getString("sl") + "g");
                } else if (1 == this.type) {
                    // 西药
                    holder.tvCount.setText(json.getString("sl") + "天");
                } else {

                    holder.tvCount.setText(json.getString("sl"));
                }
            } else {
                holder.tvCount.setText("");
            }
        } catch (JSONException e) {

        }
        return convertView;
    }

    class ViewHolder {
        TextView tvName;
        TextView tvCount;
    }
}
