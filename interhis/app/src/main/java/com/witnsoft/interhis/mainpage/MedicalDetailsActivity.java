package com.witnsoft.interhis.mainpage;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.adapter.ChineseMedicalDetailsAdapter;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.libinterhis.base.BaseActivity;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by ${liyan} on 2017/6/22.
 */
@ContentView(R.layout.ease_row_medical_details)
public class MedicalDetailsActivity extends BaseActivity {
    @ViewInject(R.id.ease_recycleview)
    private RecyclerView recyclerView;

    @ViewInject(R.id.ease_acid)
    private TextView tv_acid;

    @ViewInject(R.id.ease_aiid)
    private TextView tv_aiid;

    @ViewInject(R.id.ease_time)
    private TextView tv_time;

    @ViewInject(R.id.ease_price)
    private TextView tv_price;

    @ViewInject(R.id.ease_number)
    private TextView tv_number;

    @ViewInject(R.id.ease_advice)
    private TextView tv_advice;

    @ViewInject(R.id.chinese_ll_root)
    private RelativeLayout relativeLayout;

    private static final String TAG = "MedicalDetailsActivity";
    private ChineseMedicalDetailsAdapter adapter;
    private List<ChineseDetailModel> list;

    private String acid,aiid,time,number,advice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        acid=getIntent().getStringExtra("acid");
        aiid=getIntent().getStringExtra("aiid");
        time=getIntent().getStringExtra("time");
        number=getIntent().getStringExtra("number");
        advice=getIntent().getStringExtra("advice");


        tv_acid.setText(acid);
        tv_aiid.setText(aiid);
        tv_time.setText(time);
        tv_number.setText(number);
        tv_advice.setText(advice);

        initData();
    }

    private void initData() {
        try {
            list = HisDbManager.getManager().findChineseDeatilModel(acid);
        } catch (DbException e) {
            e.printStackTrace();
        }
        recyclerView.setLayoutManager(new GridLayoutManager(this,4));
        adapter=new ChineseMedicalDetailsAdapter(this);
        adapter.setList(list);
        recyclerView.setAdapter(adapter);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
