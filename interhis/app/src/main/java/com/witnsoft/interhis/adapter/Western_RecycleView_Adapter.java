package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.WesternDetailModel;
import com.witnsoft.interhis.inter.OnClick;
import com.witnsoft.interhis.tool.BaseViewHolder;

import java.util.List;

/**
 * Created by ${liyan} on 2017/6/13.
 */

public class Western_RecycleView_Adapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<WesternDetailModel> list;
    private OnClick onClick;

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public Western_RecycleView_Adapter(Context context) {
        this.context = context;
    }

    public void setList(List<WesternDetailModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }
    //定义一个添加text的方法
    public void addTextView(WesternDetailModel str){
        list.add(str);
    }

    public void deleteTextView(int position){
        list.remove(position);
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.onCreatMyViewHolder(context,parent, R.layout.fragment_helper_western_recycleview_item);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        holder.setText(R.id.fragment_helper_western_recycleview_item_text,list.get(position).getCmc());
        holder.setText(R.id.fragment_helper_western_recycleview_item_number,list.get(position).getSl()+""+"g");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onIteClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
