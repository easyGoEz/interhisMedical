package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;


import com.witnsoft.interhis.R;
import com.witnsoft.interhis.bean.PatChatInfo;
import com.witnsoft.interhis.inter.OnClick;
import com.witnsoft.interhis.tool.BaseViewHolder;

import java.util.List;

/**
 * Created by ${liyan} on 2017/5/9.
 */

public class DoctorAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "DoctorAdapter";
    private Context context;
    private List<PatChatInfo> list;
    private OnClick onClick;
    private int pos;

    public void setPos(int pos) {
        this.pos = pos;

    }

    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }

    public void setList(List<PatChatInfo> list) {
        this.list = list;

    }

    public DoctorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.onCreatMyViewHolder(context,parent, R.layout.fragment_doctor_recycleview_item);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, final int position) {
//            holder.setText(R.id.fragment_doctor_recycleView_item_name,list.get(0).getDATAARRAY().get(0).getDATA().get(position).getDOCNAME());
//           // holder.setText(R.id.fragment_doctor_recycleView_item_sex,list.get(0).getDATAARRAY().get(0).getDATA().get(position).);
//            holder.setText(R.id.fragment_doctor_recycleView_item_content,list.get(0).getDATAARRAY().get(0).getDATA().get(position).getJBMC());
//           // holder.setText(R.id.fragment_doctor_recycleView_item_age, list.get(position).getAge()+"");

            holder.setText(R.id.fragment_doctor_recycleView_item_age,list.get(position).getAge()+"");
            holder.setText(R.id.fragment_doctor_recycleView_item_name,list.get(position).getName());
            holder.setText(R.id.fragment_doctor_recycleView_item_sex,list.get(position).getSex());
            holder.setText(R.id.fragment_doctor_recycleView_item_content,list.get(position).getContent());

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onIteClick(position);
                if (pos==position){
            holder.setBackGround(R.id.fragment_doctor_recycleview_item,Color.parseColor("#FFFFFF"));
        }else {
            holder.setBackGround(R.id.fragment_doctor_recycleview_item,Color.parseColor("#F2F2F2"));
        }
    }
});
    }

    @Override
    public int getItemCount() {
        return 3;

    }
}
