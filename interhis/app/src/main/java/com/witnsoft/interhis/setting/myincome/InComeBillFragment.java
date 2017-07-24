package com.witnsoft.interhis.setting.myincome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.login.LoginActivity;
import com.witnsoft.interhis.base.ChildBaseFragment;
import com.witnsoft.interhis.setting.myhistory.model.SerializableMap;
import com.witnsoft.interhis.setting.myincome.adapter.IncomeBillAdapter;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libinterhis.utils.ui.AutoScaleRelativeLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

@ContentView(R.layout.fragment_income_bill)
public class IncomeBillFragment extends ChildBaseFragment {

    private static final String TAG = "IncomeBillFragment";
    private View rootView;
    private IncomeBillAdapter incomeBillAdapter = null;
    private List<Map<String, String>> list = new ArrayList<>();
    private String docId;
    private int pageNo = 1;
    private static final int PAGE_COUNT = 10;

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.sl_refresh)
    private SwipeRefreshLayout slRefresh;
    @ViewInject(R.id.rv_bill)
    private RecyclerView rvBill;
    @ViewInject(R.id.ll_day)
    private AutoScaleLinearLayout llDay;
    @ViewInject(R.id.rl_picker)
    private AutoScaleRelativeLayout rlPicker;
    @ViewInject(R.id.dp_date)
    private DatePicker dbDate;
    @ViewInject(R.id.ll_date_dismiss)
    private AutoScaleLinearLayout llDateDismiss;
    @ViewInject(R.id.tv_date)
    private TextView tvDate;
    @ViewInject(R.id.tv_cancel)
    private TextView tvCancel;
    @ViewInject(R.id.tv_ensure)
    private TextView tvEnsure;

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
        initClick();
        init();
    }

    private void initClick() {
        RxView.clicks(llBack)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        finishFragment();
                    }
                });
        RxView.clicks(llDay)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (View.VISIBLE == rlPicker.getVisibility()) {
                            rlPicker.setVisibility(View.GONE);
                        } else {
                            rlPicker.setVisibility(View.VISIBLE);
                        }
                    }
                });
        RxView.clicks(llDateDismiss)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        rlPicker.setVisibility(View.GONE);
                    }
                });
        RxView.clicks(tvCancel)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        rlPicker.setVisibility(View.GONE);
                    }
                });
        RxView.clicks(tvEnsure)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .compose(this.<Void>bindToLifecycle())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        rlPicker.setVisibility(View.GONE);
                        tvDate.setText(
                                getString(R.string.date_format,
                                        String.valueOf(dbDate.getYear()), String.valueOf(dbDate.getMonth() + 1)));
                        incomeBillAdapter = null;
                        list.clear();
                        pageNo = 1;
                        callIncomeBill(true);
                    }
                });
    }

    private void init() {
        initDatePicker();
        docId = ThriftPreUtils.getDocId(getActivity());
        callIncomeBill(true);
    }

    private int year;
    private int month;

    private void initDatePicker() {
        Calendar c = Calendar.getInstance();
        try {
            // 隐藏日
            ((ViewGroup) (((ViewGroup) dbDate.getChildAt(0)).getChildAt(0)))
                    .getChildAt(2).setVisibility(View.GONE);
        } catch (Exception e) {

        }
        dbDate.setMaxDate(c.getTime().getTime());
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        this.tvDate.setText(getString(R.string.date_format,
                String.valueOf(year), String.valueOf(month + 1)));
//        this.dbDate.init(year, month + 1, 0,
//                new DatePicker.OnDateChangedListener() {
//                    @Override
//                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                        IncomeBillFragment.this.tvDate.setText(
//                                getString(R.string.date_format,
//                                        String.valueOf(year), String.valueOf(monthOfYear + 1)));
//                    }
//                });
    }

    public static String getDateStart(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String str = sdf.format(firstDate);
        Log.d(TAG, "billIncome startTime" + str);
        return str;
    }

    public static String getDateEnd(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String str = sdf.format(lastDate);
        Log.d(TAG, "billIncome endTime" + str);
        return str;
    }

    /**
     * F27.APP.01.13 收入账单查询
     */
    private List<Map<String, String>> respList = new ArrayList<>();

    private void callIncomeBill(boolean isProgress) {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("docid", docId);
        data.setParam("stime", getDateStart(dbDate.getYear(), dbDate.getMonth()));
        data.setParam("etime", getDateEnd(dbDate.getYear(), dbDate.getMonth() + 1));
        data.setParam("rowsperpage", String.valueOf(PAGE_COUNT));
        data.setParam("pageno", String.valueOf(pageNo));
        data.setParam("ordercolumn", "optime");
        data.setParam("ordertype", "asc");
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.13");

        NetTool.getInstance().startRequest(false, isProgress, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        respList = new ArrayList<>();
                        respList = (List<Map<String, String>>) response.get("DATA");
                        if (null != respList && 0 < respList.size()) {
                            if (1 == pageNo) {
                                // 如果是第一页，表示重新加载数据
                                list.clear();
                                for (int i = 0; i < respList.size(); i++) {
                                    list.add(respList.get(i));
                                }
                            } else {
                                // 不是第一页，表示分页加载
                                for (int i = 0; i < respList.size(); i++) {
                                    list.add(respList.get(i));
                                }
                            }
                        }
                        freshUi();
                    }

                } else if (ErrCode.ErrCode_504.equals(resultCode)) {
                    // token失效
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    slRefresh.setRefreshing(false);
                    slRefresh.setEnabled(true);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                slRefresh.setRefreshing(false);
                slRefresh.setEnabled(true);
            }
        });
    }

    private void freshUi() {
        if (null == incomeBillAdapter) {
            incomeBillAdapter = new IncomeBillAdapter(getActivity().getBaseContext(), list, R.layout.item_income_bill);
            incomeBillAdapter.setFootViewId(R.layout.activity_load_footer);
            rvBill.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            rvBill.setHasFixedSize(true);
            rvBill.setAdapter(incomeBillAdapter);
        }
        incomeBillAdapter.setCanNotReadBottom(false);
        incomeBillAdapter.setOnRecyclerViewBottomListener(new ComRecyclerAdapter.OnRecyclerViewBottomListener() {
            @Override
            public void OnBottom() {
                if (respList != null && respList.size() == PAGE_COUNT) {
                    slRefresh.setEnabled(false);
                    slRefresh.setRefreshing(true);
                    incomeBillAdapter.setCanNotReadBottom(true);
                    pageNo++;
                    callIncomeBill(true);
                }
            }
        });
        incomeBillAdapter.setOnItemClickListener(new ComRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                SerializableMap map = new SerializableMap();
                map.setMap(list.get(position));
                bundle.putSerializable("income_bill", map);
                IncomeBillDetailFragment incomeBillDetailFragment = new IncomeBillDetailFragment();
                pushFragment(incomeBillDetailFragment, bundle, true);
            }
        });
        incomeBillAdapter.notifyDataSetChanged();
        slRefresh.setRefreshing(false);
        slRefresh.setEnabled(true);
        refreshRecyclerView();
    }

    /**
     * 下拉刷新
     */
    private void refreshRecyclerView() {
        slRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        slRefresh.setEnabled(false);
                        slRefresh.setRefreshing(true);
                        incomeBillAdapter = null;
                        list.clear();
                        pageNo = 1;
                        callIncomeBill(false);
                    }
                }, 600);
            }
        });
    }
}
