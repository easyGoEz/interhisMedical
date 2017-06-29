package com.witnsoft.interhis.utils.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.utils.ui.AutoScaleRelativeLayout;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

public class ItemSettingRight extends AutoScaleRelativeLayout {

    private TextView tvTitle;
    private ImageView ivIcon;
    private View line;

    public ItemSettingRight(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.item_setting_right, this, true);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        line = (View) findViewById(R.id.line);
    }

    public void setTvTitle(String str) {
        if (!TextUtils.isEmpty(str)) {
            tvTitle.setText(str);
        } else {
            tvTitle.setText(str);
        }
    }

    public void setTvTitle(String str, boolean isLineBottomVisible) {
        if (!TextUtils.isEmpty(str)) {
            tvTitle.setText(str);
        } else {
            tvTitle.setText(str);
        }
        if (isLineBottomVisible) {
            line.setVisibility(VISIBLE);
        } else {
            line.setVisibility(GONE);
        }
    }

    public void setIconBackGround(Drawable res) {
        try {
            ivIcon.setVisibility(VISIBLE);
            ivIcon.setBackground(res);
        } catch (Exception e) {
            ivIcon.setVisibility(GONE);
        }
    }

}
