package com.witnsoft.interhis.setting;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.base.BaseFragment;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

public class ChildBaseFragment extends BaseFragment {

    private FragmentManager mFragmentManager;

    @Override
    protected View initView(View view, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return null;
    }

    public void pushChildFragment(ChildBaseFragment fragment, Bundle bundle, boolean isToBackStack) {
        mFragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        transaction.replace(R.id.fl_content, fragment, ChildBaseFragment.class.getSimpleName());
        if (isToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    //右侧子fragment进栈
    public void pushFragment(ChildBaseFragment fragment, Bundle bundle, boolean isToBackStack) {
        mFragmentManager = getFragmentManager();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        transaction.add(R.id.fl_content, fragment, ChildBaseFragment.class.getSimpleName());
        if (isToBackStack) {
            transaction.addToBackStack(null);
        }
        hideSoftInput();
        transaction.commit();
    }

    public void finishFragment() {
        hideSoftInput();
        mFragmentManager = getFragmentManager();
        mFragmentManager.popBackStack();
    }

    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    public void load(String url, ImageView iv, int id) {
        if (!TextUtils.isEmpty(url)) {
            if ((!(url.endsWith(".png"))) && (!(url.endsWith(".jpg")))) {
                url = url + ".png";
            }
            Glide.with(this)
                    .load(url)
                    .error(id)
                    .into(iv);
        }
    }
}
