package com.witnsoft.interhis.rightpage.chinesemedical;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/30.
 */

public class MedicalCountDialog {

    private Fragment context;
    private String medName;
    private String[] medCountNum;
    private AlertDialog ad;
    private Button btnCancel;
    private TextView tvName;
    private ListView lvCount;
    private TextView tvAdd;
    private TextView tvNum;
    private TextView tvMinus;
    private CallBackMedCount callBackMedCount;
    private ChineseDetailModel searchModel = new ChineseDetailModel();

    public MedicalCountDialog(Fragment context, ChineseDetailModel searchModel, String[] medCountNum) {
        this.context = context;
        this.searchModel = searchModel;
        this.medCountNum = medCountNum;
        if (!TextUtils.isEmpty(searchModel.getCmc())) {
            this.medName = searchModel.getCmc();
        }
    }

    public void init() {
        ad = new AlertDialog.Builder(context.getActivity()).create();
        ad.show();
        Window window = ad.getWindow();
        window.setContentView(R.layout.view_medical_count);
        btnCancel = (Button) window.findViewById(R.id.btn_sure);
        tvName = (TextView) window.findViewById(R.id.tv_name);
        lvCount = (ListView) window.findViewById(R.id.lv_count);
        tvAdd = (TextView) window.findViewById(R.id.tv_add);
        tvNum = (TextView) window.findViewById(R.id.tv_num);
        tvMinus = (TextView) window.findViewById(R.id.tv_minus);
        callBackMedCount = (CallBackMedCount) context;
        initData();
        iniClick();
    }

    private void initData() {
        if (!TextUtils.isEmpty(medName)) {
            tvName.setText(medName);
        }
        final List<String> medCountList = new ArrayList<>();
        if (null != medCountNum && 0 < medCountNum.length) {
            for (int i = 0; i < medCountNum.length; i++) {
                medCountList.add(medCountNum[i]);
            }
            MedicalCountAdapter medicalAdapter = new MedicalCountAdapter(context.getActivity(), medCountList);
            lvCount.setAdapter(medicalAdapter);
            lvCount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String medCount = medCountList.get(position);
                    searchModel.setSl(medCount);
                    callBackMedCount.setMedCount(searchModel);
                    dismiss();
                }
            });
        }
    }

    private void iniClick() {
        RxView.clicks(tvNum)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(tvNum.getText().toString())) {
                            String medCount = tvNum.getText().toString();
                            int num = 0;
                            try {
                                num = Integer.parseInt(medCount);
                            } catch (Exception e) {
                                num = 0;
                            }
                            if (0 < num) {
                                if (medCount.contains("g")) {
                                    medCount.replace("g", "");
                                }
                                searchModel.setSl(medCount);
                                callBackMedCount.setMedCount(searchModel);
                            } else {
                                Toast.makeText(context.getActivity(), context.getResources().
                                        getString(R.string.please_choose_count), Toast.LENGTH_LONG).show();
                            }
                        }
                        dismiss();
                    }
                });
        RxView.clicks(tvMinus)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(tvNum.getText().toString())) {
                            try {
                                int num = Integer.parseInt(tvNum.getText().toString());
                                if (0 < num) {
                                    num--;
                                    tvNum.setText(String.valueOf(num));
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                });
        RxView.clicks(tvAdd)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(tvNum.getText().toString())) {
                            try {
                                int num = Integer.parseInt(tvNum.getText().toString());
                                num++;
                                tvNum.setText(String.valueOf(num));
                            } catch (Exception e) {

                            }
                        }
                    }
                });
        RxView.clicks(btnCancel)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        dismiss();
                    }
                });
    }

    public void dismiss() {
        ad.dismiss();
    }

    public interface CallBackMedCount {
        void setMedCount(ChineseDetailModel searchModel);
    }
}
