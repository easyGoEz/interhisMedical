package com.witnsoft.interhis.utils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.witnsoft.interhis.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by zhengchengpeng on 2017/6/8.
 */

public class ComRecyclerViewHolder extends RecyclerView.ViewHolder {
    public SparseArray<View> mViews;
    public View wholeView;
    public Context context;

    public ComRecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        wholeView = itemView;
        this.context = context;
        mViews = new SparseArray<View>();
    }

    public static ComRecyclerViewHolder getComRecyclerViewHolder(Context context, int layoutId, ViewGroup parent) {
        View wholeView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ComRecyclerViewHolder(context, wholeView);
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = wholeView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getWholeView() {
        return wholeView;
    }

    public ComRecyclerViewHolder setText(int viewId, String data) {
        TextView tv = getView(viewId);
        if (!TextUtils.isEmpty(data)) {
            tv.setText(data);
        } else {
            tv.setText("");
        }
        return this;
    }

    /**
     * 设置本地图片为背景
     */
    public ComRecyclerViewHolder setBackgroundResource(int viewId, int picId) {
        ImageView imageView = getView(viewId);
        imageView.setBackgroundResource(picId);
        return this;
    }

    public ComRecyclerViewHolder setImageUrl(Context cxt, int viewId, String url, int errorId) {
        CircleImageView imageView = getView(viewId);
        if (!TextUtils.isEmpty(url)) {
            Glide.with(cxt)
                    .load(url)
                    .error(errorId)
                    .into(imageView);
        } else {
            imageView.setImageResource(errorId);
        }
        return this;
    }

    /**
     * 设置字体颜色
     */
    public ComRecyclerViewHolder setTextColor(int viewId, int colorId) {
        TextView view = getView(viewId);
        view.setTextColor(colorId);
        return this;
    }

    /**
     * 设置背景颜色
     */
    public ComRecyclerViewHolder setBackgroundColor(int viewId, int colorId) {
        View view = getView(viewId);
        view.setBackgroundColor(context.getResources().getColor(colorId));
        return this;
    }

    /**
     * 设置View是否Gone
     */
    public ComRecyclerViewHolder setVisible(int viewId, boolean isVisible) {
        View view = getView(viewId);
        if (isVisible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        return this;
    }


}
