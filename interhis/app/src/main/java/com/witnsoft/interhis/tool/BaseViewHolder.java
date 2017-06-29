package com.witnsoft.interhis.tool;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * Created by ${liyan} on 2017/5/8.
 */

public class BaseViewHolder extends RecyclerView.ViewHolder{
    private View view;
    private Context context;
    private SparseArray<View> viewSparseArray;

    public View getView() {
        return view;
    }

    public BaseViewHolder(View itemView,Context context) {
        super(itemView);
        this.context=context;
        view=itemView;
        viewSparseArray=new SparseArray<>();
    }

    public static BaseViewHolder onCreatMyViewHolder(Context context, ViewGroup viewGroup,int layoutId){
        View view= LayoutInflater.from(context).inflate(layoutId,viewGroup,false);
        BaseViewHolder baseViewHolder=new BaseViewHolder(view,context);
        return baseViewHolder;
    }

    public static BaseViewHolder onCreatMyListViewHolder(View view,ViewGroup viewGroup,int layoutId){
        BaseViewHolder baseViewHolder=null;
        if (view==null){
            view=LayoutInflater.from(viewGroup.getContext()).inflate(layoutId,viewGroup,false);
            baseViewHolder=new BaseViewHolder(view,viewGroup.getContext());
        }else {
            baseViewHolder= (BaseViewHolder) view.getTag();
        }
        return baseViewHolder;
    }

    public <T extends View> T getView(int resId){
        View view=viewSparseArray.get(resId);
        if (view==null){
            view=itemView.findViewById(resId);
            viewSparseArray.put(resId,view);
        }
        return (T) view;
    }

    public BaseViewHolder setText(int resId,String s){
        TextView textView= (TextView) view.findViewById(resId);
        if (s!=null){
            textView.setText(s);
        }
        return this;
    }

    public BaseViewHolder setOnLineImage(int resId,String url){
        ImageView imageView= (ImageView) view.findViewById(resId);
        if (url!=null){
            Glide.with(context).load(url).into(imageView);
        }
        return this;
    }

    public BaseViewHolder setBackGround(int resId,int color){
        LinearLayout linearLayout= (LinearLayout) view.findViewById(resId);

        linearLayout.setBackgroundColor(color);

        return this;
    }

}
