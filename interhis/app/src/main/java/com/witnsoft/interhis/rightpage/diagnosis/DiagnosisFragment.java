package com.witnsoft.interhis.rightpage.diagnosis;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.witnsoft.interhis.BaseV4Fragment;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.DiagnosisModel;
import com.witnsoft.interhis.mainpage.LoginActivity;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private String aiid;
    private DiagnosisAdapter adapter = null;

    private final class ErrCode {
        private static final String ErrCode_200 = "200";
        private static final String ErrCode_504 = "504";
    }

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
        this.aiid = getArguments().getString("aiid");
        callDiagnosisDb();
    }

    private void initClick() {
        RxView.clicks(btnSave)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (!TextUtils.isEmpty(etDiagnosis.getText().toString())) {
                            callSaveDiagnosis();
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.please_enter_dialogsis), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

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
            adapter = new DiagnosisAdapter(getActivity(), diagnosisModelList, R.layout.item_diagnosis);
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
     * F27.APP.01.08 保存诊断内容
     */
    private void callSaveDiagnosis() {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        // 问诊id
        data.setParam("aiid", aiid);
        // 诊断内容
        data.setParam("zdsm", etDiagnosis.getText().toString());
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.08");

        NetTool.getInstance().startRequest(false, true, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        if (null != response.get("DATA")) {
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
                            callDiagnosisDb();
                        }
                    }
                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
    }
}
