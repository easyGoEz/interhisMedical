package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.inter.OnFixClick;
import com.witnsoft.interhis.tool.BaseViewHolder;

import java.util.List;

/**
 * Created by ${liyan} on 2017/5/21.
 */

public class Chinese_Fixed_Adapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<ChineseDetailModel> list;
    private Context context;
    private OnFixClick onFixClick;

    public void setOnFixClick(OnFixClick onFixClick) {
        this.onFixClick = onFixClick;
    }

    public void setList(List<ChineseDetailModel> list) {
        this.list = list;
    }

    public Chinese_Fixed_Adapter(Context context) {

        this.context = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.onCreatMyViewHolder(context,parent, R.layout.fragment_helper_chinese_fixed_recycleview_item);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        holder.setText(R.id.fragment_helper_chinese_fixed_recycleview_text,list.get(position).getCmc());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFixClick.OnFixItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
