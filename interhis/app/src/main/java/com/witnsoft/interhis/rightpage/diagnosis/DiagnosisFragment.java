package com.witnsoft.interhis.rightpage.diagnosis;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.base.BaseV4Fragment;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.interhis.db.model.DiagnosisModel;
import com.witnsoft.interhis.db.model.WesternModel;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_diagnosis)
public class DiagnosisFragment extends BaseV4Fragment {

    @ViewInject(R.id.rv_diagnosis)
    private RecyclerView rvDiagnosis;
    @ViewInject(R.id.ed_diagnosis)
    private EditText etDiagnosis;
    @ViewInject(R.id.btn_save)
    private Button btnSave;

    private View rootView;
    private String accId;
    private ChineseModel chineseModel = null;
    private WesternModel westernModel = null;
    private DiagnosisAdapter adapter = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = x.view().inject(this, inflater, container);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        initClick();
    }

    private void init() {
        try {
            this.accId = getArguments().getString("userId").toLowerCase();
        } catch (Exception e) {
            this.accId = getArguments().getString("userId");
        }
        callDiagnosisDb();
//        callChineseDb();
//        callWesternDb();
    }

    private void initClick() {
        RxView.clicks(btnSave)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(etDiagnosis.getText().toString())) {
                            try {
                                DiagnosisModel diagnosisModel = new DiagnosisModel();
                                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                                String date = sDateFormat.format(new java.util.Date());
                                diagnosisModel.setTime(date + accId);
                                diagnosisModel.setAccId(accId);
                                diagnosisModel.setDescribe(etDiagnosis.getText().toString());
                                HisDbManager.getManager().saveDiagnosis(diagnosisModel);
                                Toast.makeText(getActivity(), getResources().getString(R.string.save_success), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                Toast.makeText(getActivity(), getResources().getString(R.string.save_failed), Toast.LENGTH_LONG).show();
                            }
                            // TODO: 2017/7/4 诊断两主表都存 
//                            updateChinese();
//                            updateWestern();
                            callDiagnosisDb();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_dialogsis), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

//    private void updateChinese() {
//        if (null != chineseModel) {
//            // 数据库有数据，更新表
//            try {
//                chineseModel.setZdsm(etDiagnosis.getText().toString());
//                HisDbManager.getManager().upDateChinese(chineseModel, "ZDSM");
//            } catch (DbException e) {
//
//            }
//        } else {
//            // 数据库没有数据，创建表
//            try {
//                ChineseModel chineseModel = new ChineseModel();
//                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
//                String date = sDateFormat.format(new java.util.Date());
//                chineseModel.setTime(date);
//                chineseModel.setAccId(accId);
//                chineseModel.setZdsm(etDiagnosis.getText().toString());
//                HisDbManager.getManager().saveAskChinese(chineseModel);
//            } catch (DbException e) {
//
//            }
//        }
//    }

//    private void updateWestern() {
//        if (null != westernModel) {
//            // 数据库有数据，更新表
//            try {
//                westernModel.setZdsm(etDiagnosis.getText().toString());
//                HisDbManager.getManager().upDateWestern(westernModel, "ZDSM");
//            } catch (DbException e) {
//
//            }
//        } else {
//            // 数据库没有数据，创建表
//            try {
//                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
//                String date = sDateFormat.format(new java.util.Date());
//                WesternModel westernModel = new WesternModel();
//                westernModel.setTime(date);
//                westernModel.setAwId(accId);
//                westernModel.setZdsm(etDiagnosis.getText().toString());
//                HisDbManager.getManager().saveAskWestern(westernModel);
//            } catch (DbException e) {
//
//            }
//        }
//    }

    /**
     * 读取数据库(诊断)
     */
    private List<DiagnosisModel> diagnosisModelList = new ArrayList<>();

    private void callDiagnosisDb() {
        showWaitingDialogCannotCancel();
        Observable.create(new Observable.OnSubscribe<List<DiagnosisModel>>() {
            @Override
            public void call(Subscriber<? super List<DiagnosisModel>> subscriber) {
                try {
                    List<DiagnosisModel> list = HisDbManager.getManager().findDiagnosisList(accId);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(list);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DbException e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DiagnosisModel>>() {

                    @Override
                    public void onCompleted() {
                        hideWaitingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideWaitingDialog();
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<DiagnosisModel> list) {
                        diagnosisModelList.clear();
                        if (null != list && 0 < list.size()) {
                            for (DiagnosisModel model : list) {
                                diagnosisModelList.add(model);
                            }
                        }
                        refreshUi();
                    }
                });
    }

    private void refreshUi() {
        if (null == adapter) {
            adapter = new DiagnosisAdapter(getActivity(), diagnosisModelList, R.layout.item_western_search);
            rvDiagnosis.setLayoutManager(new LinearLayoutManager(getActivity()));
            rvDiagnosis.setHasFixedSize(true);
            rvDiagnosis.setAdapter(adapter);
        }
        adapter.setCanNotReadBottom(false);
        adapter.setOnItemClickListener(new ComRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {

            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * 读取数据库(中药)
     */
    private void callChineseDb() {
        Observable.create(new Observable.OnSubscribe<ChineseModel>() {
            @Override
            public void call(Subscriber<? super ChineseModel> subscriber) {
                try {
                    ChineseModel model = HisDbManager.getManager().findChineseModel(accId);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DbException e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChineseModel>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(ChineseModel model) {
                        chineseModel = model;
                        if (null != model && !TextUtils.isEmpty(model.getZdsm())) {
                            //诊断说明
                            etDiagnosis.setText(model.getZdsm());
                        }
                    }
                });
    }

    /**
     * 读取数据库(西药)
     */
    private void callWesternDb() {
        Observable.create(new Observable.OnSubscribe<WesternModel>() {
            @Override
            public void call(Subscriber<? super WesternModel> subscriber) {
                try {
                    WesternModel model = HisDbManager.getManager().findWesternModel(accId);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DbException e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<WesternModel>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(WesternModel model) {
                        westernModel = model;
                    }
                });
    }
}
