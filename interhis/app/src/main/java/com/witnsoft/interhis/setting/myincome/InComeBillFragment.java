package com.witnsoft.interhis.setting.myincome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.mainpage.LoginActivity;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/14.
 */

@ContentView(R.layout.fragment_income_bill)
public class IncomeBillFragment extends ChildBaseFragment {

    private View rootView;
    private IncomeBillAdapter incomeBillAdapter = null;
    private List<Map<String, String>> historyList = new ArrayList<>();
    private String docId;
    private int pageNo = 1;
    private static final int PAGE_COUNT = 10;

    @ViewInject(R.id.ll_back)
    private AutoScaleLinearLayout llBack;
    @ViewInject(R.id.sl_refresh)
    private SwipeRefreshLayout slRefresh;
    @ViewInject(R.id.rv_bill)
    private RecyclerView rvBill;

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
    }

    private void init() {
        docId = ThriftPreUtils.getDocId(getActivity());
        callIncomeBill(true);
    }


//    private void callIncomeBill(boolean isRefresh) {
////        historyList.clear();
//        int c1 = 0;
//        int c2 = 1;
//        int c3 = 2;
//        for (int i = 0; i < 1000; i++) {
//            Map<String, String> map = new HashMap<>();
//            map.put("time", "2017-01-01-" + String.valueOf(historyList.size()));
//            map.put("count", String.valueOf(historyList.size() * 5));
//            map.put("name", "patname");
//            if (c1 == i) {
//                map.put("color", String.valueOf(R.color.red));
//                c1 = c1 + 3;
//            } else if (c2 == i) {
//                map.put("color", String.valueOf(R.color.green));
//                c2 = c2 + 3;
//            } else if (c3 == i) {
//                map.put("color", String.valueOf(R.color.blue));
//                c3 = c3 + 3;
//            }
//            historyList.add(map);
//        }
//        freshUi();
//    }

    /**
     * F27.APP.01.13 收入账单查询
     */
    private List<Map<String, String>> respList = new ArrayList<>();

    private void callIncomeBill(boolean isProgress) {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("docid", docId);
        data.setParam("stime", "");
        data.setParam("etime", "");
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
                                historyList.clear();
                                for (int i = 0; i < respList.size(); i++) {
                                    historyList.add(respList.get(i));
                                }
                            } else {
                                // 不是第一页，表示分页加载
                                for (int i = 0; i < respList.size(); i++) {
                                    historyList.add(respList.get(i));
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
            incomeBillAdapter = new IncomeBillAdapter(getActivity().getBaseContext(), historyList, R.layout.item_income_bill);
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
                        historyList.clear();
                        pageNo = 1;
                        callIncomeBill(false);
                    }
                }, 600);
            }
        });
    }
}
