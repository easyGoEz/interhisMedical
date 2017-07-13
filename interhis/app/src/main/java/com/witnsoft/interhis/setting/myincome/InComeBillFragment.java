package com.witnsoft.interhis.setting.myincome;

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
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;

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

    private void callIncomeBill(boolean isRefresh) {
        historyList.clear();
        for (int i = 0; i < 10; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("time", "2017-01-01");
            map.put("count", String.valueOf(i * 5));
            map.put("name", "patname");
            historyList.add(map);
        }
        freshUi();
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
                if (historyList != null && historyList.size() == PAGE_COUNT) {
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
//        slRefresh.setRefreshing(false);
//        slRefresh.setEnabled(true);
//        refreshRecyclerView();
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
