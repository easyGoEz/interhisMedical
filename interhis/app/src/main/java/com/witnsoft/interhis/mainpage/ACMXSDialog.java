package com.witnsoft.interhis.mainpage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.libinterhis.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by ${liyan} on 2017/6/15.
 */

@ContentView(R.layout.activity_number_medical_acmxs)
public class ACMXSDialog extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "ACMXSDialog";
    private LinearLayout ll_root,ll_dialog;
    private Button first_cancel;
    private TextView seven,fourteen,twenty_one,number,add,less;
    private String acid;
    private int num=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();
    }

    private void initView() {
        ll_root = (LinearLayout) findViewById(R.id.acmxs_ll_root);
        ll_dialog = (LinearLayout) findViewById(R.id.acmxs_ll_dialog);
        seven = (TextView) findViewById(R.id.acmxs_dialog_seven);
        fourteen = (TextView) findViewById(R.id.acmxs_dialog_fourteen);
        twenty_one = (TextView) findViewById(R.id.acmxs_dialog_twenty_one);
        number = (TextView) findViewById(R.id.acmxs_dialog_number);
        add = (TextView) findViewById(R.id.acmxs_dialog_add);
        less = (TextView) findViewById(R.id.acmxs_dialog_less);
        first_cancel = (Button) findViewById(R.id.acmxs_first_cancel);


        ll_root.setOnClickListener(this);
        ll_dialog.setOnClickListener(this);
        seven.setOnClickListener(this);
        fourteen.setOnClickListener(this);
        twenty_one.setOnClickListener(this);
        number.setOnClickListener(this);
        add.setOnClickListener(this);
        less.setOnClickListener(this);
        first_cancel.setOnClickListener(this);
        less.setTag("+");
        add.setTag("-");

        acid=getIntent().getStringExtra("accid");
        Log.e(TAG, "initView: "+acid );
        SetViewListener();


    }

    private void SetViewListener() {
        add.setOnClickListener(new OnButtonClickListener());
        less.setOnClickListener(new OnButtonClickListener());
        number.addTextChangedListener(new OnTextChangeListener());
    }

    @Override
    public void onClick(View v) {
        ChineseModel chineseModel=new ChineseModel();
        chineseModel.setAcId(acid);
        switch (v.getId()){
            case R.id.acmxs_ll_root:
                finish();
                break;
            case R.id.acmxs_dialog_seven:
                chineseModel.setAcMxs("7");
                EventBus.getDefault().post(chineseModel);
                finish();
                break;
            case R.id.acmxs_dialog_fourteen:
                chineseModel.setAcMxs("14");
                EventBus.getDefault().post(chineseModel);
                finish();
                break;
            case R.id.acmxs_dialog_twenty_one:
                chineseModel.setAcMxs("21");
                EventBus.getDefault().post(chineseModel);
                finish();
                break;
            case R.id.acmxs_dialog_number:
                chineseModel.setAcMxs(String.valueOf(num));
                EventBus.getDefault().post(chineseModel);
                finish();
                break;
            case R.id.first_cancel:
                finish();
                break;
        }
    }

    /**
     * 加减按钮事件监听器
     *
     *
     */
    class OnButtonClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v)
        {
            String numString = number.getText().toString();
            if (numString == null || numString.equals(""))
            {
                num = 0;
                number.setText("0");
            } else
            {
                if (v.getTag().equals("-"))
                {
                    if (++num < 0)  //先加，再判断
                    {
                        num--;
                        Toast.makeText(ACMXSDialog.this, "请输入一个大于0的数字",
                                Toast.LENGTH_SHORT).show();
                    } else
                    {
                        number.setText(String.valueOf(num));
//                        show.setText(number.getText()+"g");
                    }
                } else if (v.getTag().equals("+"))
                {
                    if (--num < 0)  //先减，再判断
                    {
                        num++;
                        Toast.makeText(ACMXSDialog.this, "请输入一个大于0的数字",
                                Toast.LENGTH_SHORT).show();
                    } else
                    {
                        number.setText(String.valueOf(num));
//                        show.setText(number.getText()+"g");
                    }
                }
            }
        }
    }

    /**
     * EditText输入变化事件监听器
     */
    class OnTextChangeListener implements TextWatcher
    {

        @Override
        public void afterTextChanged(Editable s)
        {
            String numString = s.toString();
            if(numString == null || numString.equals(""))
            {
                num = 0;
                number.setText(0);
//                show.setText(number.getText());
            }
            else {
                int numInt = Integer.parseInt(numString);
                if (numInt < 0)
                {
                    Toast.makeText(ACMXSDialog.this, "请输入一个大于0的数字",
                            Toast.LENGTH_SHORT).show();
                }else {
//                    show.setText(number.getText()+"g");
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after)
        {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count)
        {

        }
    }
}
