package com.witnsoft.interhis.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.inter.OnClick;
import com.witnsoft.interhis.tool.BaseViewHolder;

import java.util.List;

/**
 * Created by ${liyan} on 2017/6/22.
 */

public class ChineseMedicalDetailsAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context context;
    private List<ChineseDetailModel> list;

    public void setList(List<ChineseDetailModel> list) {
        this.list = list;
    }

    public ChineseMedicalDetailsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return BaseViewHolder.onCreatMyViewHolder(context,parent, R.layout.ease_row_recyclevire_item);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.setText(R.id.ease_item_name,list.get(position).getCmc());
        holder.setText(R.id.ease_item_number,list.get(position).getSl()+"g");
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }
}
