package com.witnsoft.interhis.rightpage.westernmedical;

import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.utils.ClearEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;


/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_western)
public class WesternFragment extends Fragment {

    @ViewInject(R.id.keyboard)
    private KeyboardView keyboardView;
    @ViewInject(R.id.et_search)
    private ClearEditText etSearch;
    @ViewInject(R.id.gv_search)
    private GridView gvSearch;
    @ViewInject(R.id.tv_med_count)
    private TextView tvMedCount;
    @ViewInject(R.id.tv_med_money)
    private TextView tvMedMoney;
    @ViewInject(R.id.gv_med_top)
    private GridView gvMedTop;
    @ViewInject(R.id.btn_save)
    private Button btnSave;
    @ViewInject(R.id.et_usage)
    private EditText etUsage;
    @ViewInject(R.id.iv_signature)
    private ImageView ivSignature;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = x.view().inject(this, inflater, container);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {

    }
}
