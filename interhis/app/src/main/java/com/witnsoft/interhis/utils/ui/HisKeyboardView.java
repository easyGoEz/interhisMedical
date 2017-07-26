package com.witnsoft.interhis.utils.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;

/**
 * Created by zhengchengpeng on 2017/7/7.
 */

public class HisKeyboardView extends AutoScaleLinearLayout implements View.OnClickListener {

    private Context context;
    private AutoScaleLinearLayout llRow1;
    private AutoScaleLinearLayout llRow2;
    private AutoScaleLinearLayout llRow3;
    private AutoScaleLinearLayout llRow4;
    private Button delete;
    private OnKeyboardActionListener mKeyboardActionListener;

    public HisKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.view_his_keyboard, this, true);
        llRow1 = (AutoScaleLinearLayout) findViewById(R.id.ll_row1);
        llRow2 = (AutoScaleLinearLayout) findViewById(R.id.ll_row2);
        llRow3 = (AutoScaleLinearLayout) findViewById(R.id.ll_row3);
        llRow4 = (AutoScaleLinearLayout) findViewById(R.id.ll_row4);
        delete = (Button) findViewById(R.id.btn_delete);
        delete.setOnClickListener(this);
    }

    public void init(Context context) {
        this.context = context;
        initItem();
    }

    private void initItem() {
        String[] row1 = context.getResources().getStringArray(R.array.key_row1);
        String[] row2 = context.getResources().getStringArray(R.array.key_row2);
        String[] row3 = context.getResources().getStringArray(R.array.key_row3);
        String[] row4 = context.getResources().getStringArray(R.array.key_row4);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.weight = 1.0f;
        addView(llRow1, row1, params);
        addView(llRow2, row2, params);
        addView(llRow3, row3, params);
        addView(llRow4, row4, params);
    }

    private void addView(AutoScaleLinearLayout ll, String[] row, LinearLayout.LayoutParams params) {
        ll.removeAllViews();
        for (int i = 0; i < row.length; i++) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_keyboard, null, false);

            view.setLayoutParams(params);
            Button btn = (Button) view.findViewById(R.id.btn_key);
            btn.setText(row[i]);
            btn.setTag(row[i]);
            btn.setOnClickListener(this);
            ll.addView(view);
        }
    }

    private int calculateDpToPx(int padding_in_dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (padding_in_dp * scale + 0.5f);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_delete) {
            mKeyboardActionListener.onDelete();
        } else {
            mKeyboardActionListener.onKey(String.valueOf(v.getTag()));
        }
    }

    public interface OnKeyboardActionListener {

        void onKey(String str);

        void onDelete();
    }

    public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
        mKeyboardActionListener = listener;
    }
}
