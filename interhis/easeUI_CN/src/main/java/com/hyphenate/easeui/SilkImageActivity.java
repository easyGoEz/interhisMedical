package com.hyphenate.easeui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.hyphenate.easeui.widget.photoview.EasePhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/7/18.
 */

public class SilkImageActivity extends EaseBaseActivity {

    private static final String Tag = "SilkImageActivity";
    private TextView tvNo;
    private ViewPager viewPager;
    private LinearLayout llBack;
    private ViewPagerAdapter adapter = null;
    private List<View> views = new ArrayList<>();
    private ArrayList<String> list = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_silk_image);
        super.onCreate(savedInstanceState);
        tvNo = (TextView) findViewById(R.id.tv_no);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        llBack = (LinearLayout) findViewById(R.id.ll_back);
        init();
    }

    private void init() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        list = intent.getStringArrayListExtra("imgList");
        position = intent.getIntExtra("position", 0);
        if (null != list && 0 < list.size()) {
            views.clear();
            for (String str : list) {
                ImageView iv = new ImageView(this);
                Glide.with(this).load(str).fitCenter().into(iv);
                views.add(iv);
            }
        }
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        tvNo.setText(String.valueOf(position + 1) + "/" + views.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float positionOffset, int positionOffsetPixels) {
                tvNo.setText(String.valueOf(i + 1) + "/" + views.size());
                position = i;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (null != views && 0 < views.size()) {
                return views.size();
            } else {
                return 0;
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
