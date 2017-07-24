package com.witnsoft.interhis.rightpage.diagnosis.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.DiagnosisModel;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.interhis.utils.ComRecyclerViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/7/6.
 */

public class DiagnosisAdapter extends ComRecyclerAdapter<DiagnosisModel> {

    private Context context;
    private List<DiagnosisModel> list = new ArrayList<>();

    public DiagnosisAdapter(Context context, List<DiagnosisModel> list, int layoutId) {
        super(context, list, layoutId);
        this.context = context;
        this.list = list;
    }

    @Override
    public void convert(ComRecyclerViewHolder comRecyclerViewHolder, DiagnosisModel item) {
        comRecyclerViewHolder.setText(R.id.tv_text, item.getDescribe());
        comRecyclerViewHolder.setText(R.id.tv_time, item.getDesTime());
    }

    @Override
    public void convertHeader(ComRecyclerViewHolder comRecyclerViewHolder) {

    }

    @Override
    public void convertFooter(ComRecyclerViewHolder comRecyclerViewHolder) {
        if (!canNotReadBottom) {
            comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_done));
        } else {
            comRecyclerViewHolder.setText(R.id.load_more, context.getString(R.string.load_more));
        }
    }
}
