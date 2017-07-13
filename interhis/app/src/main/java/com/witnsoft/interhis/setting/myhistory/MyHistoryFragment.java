package com.witnsoft.interhis.setting.myhistory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.mainpage.LoginActivity;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.setting.myhistory.model.SerializableMap;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/13.
 */

@ContentView(R.layout.fragment_my_history)
public class MyHistoryFragment extends ChildBaseFragment {

    private View rootView;
    private MyHistoryAdapter adapter;
    private List<Map<String, String>> historyList = new ArrayList<>();
    private String docId;
    private int pageNo = 1;
    private static final int PAGE_COUNT = 10;

    // 总计接诊量
    @ViewInject(R.id.tv_visit_count_all)
    private TextView tvVisitCountAll;
    // 月接诊量
    @ViewInject(R.id.tv_visit_count_month)
    private TextView tvVisitCountMonth;
    // 日接诊量
    @ViewInject(R.id.tv_visit_count_daily)
    private TextView tvVisitCountDaily;

    @ViewInject(R.id.sl_refresh)
    private SwipeRefreshLayout slRefresh;
    @ViewInject(R.id.rv_history)
    private RecyclerView rvHistory;

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
    }

    private void init() {
        docId = ThriftPreUtils.getDocId(getActivity());
        callCount();
        callSavePersonalIntroduction(true);
    }

    /**
     * F27.APP.01.12 我的问诊记录查询
     */
    private void callCount() {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("docid", docId);
        data.setParam("gettype", "jzltj");
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.12");

        NetTool.getInstance().startRequest(false, false, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        if (null != response) {
                            Map<String, String> data = (Map<String, String>) response.get("DATA");
                            // 总接诊量
                            if (!TextUtils.isEmpty(data.get("all"))) {
                                tvVisitCountAll.setText(data.get("all"));
                            }
                            // 月接诊量
                            if (!TextUtils.isEmpty(data.get("yue"))) {
                                tvVisitCountMonth.setText(data.get("yue"));
                            }
                            // 本日接诊量
                            if (!TextUtils.isEmpty(data.get("ri"))) {
                                tvVisitCountDaily.setText(data.get("ri"));
                            }
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

    /**
     * F27.APP.01.11 我的问诊记录查询
     */
    private List<Map<String, String>> respList = new ArrayList<>();

    private void callSavePersonalIntroduction(boolean isProgress) {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("docid", docId);
        data.setParam("rowsperpage", String.valueOf(PAGE_COUNT));
        data.setParam("pageno", String.valueOf(pageNo));
        data.setParam("ordercolumn", "paytime");
        data.setParam("ordertype", "asc");
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.11");

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
        if (null == adapter) {
            adapter = new MyHistoryAdapter(getActivity().getBaseContext(), historyList, R.layout.item_ask_history);
            adapter.setFootViewId(R.layout.activity_load_footer);
            rvHistory.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            rvHistory.setHasFixedSize(true);
            rvHistory.setAdapter(adapter);
        }
        adapter.setCanNotReadBottom(false);
        adapter.setOnRecyclerViewBottomListener(new ComRecyclerAdapter.OnRecyclerViewBottomListener() {
            @Override
            public void OnBottom() {
                if (respList != null && respList.size() == PAGE_COUNT) {
                    slRefresh.setEnabled(false);
                    slRefresh.setRefreshing(true);
                    adapter.setCanNotReadBottom(true);
                    pageNo++;
                    callSavePersonalIntroduction(true);
                }
            }
        });
        adapter.setOnItemClickListener(new ComRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Bundle bundle = new Bundle();
                SerializableMap map = new SerializableMap();
                map.setMap(historyList.get(position));
                bundle.putSerializable("history_detail", map);
                MyHistoryDetailFragment myHistoryDetailFragment = new MyHistoryDetailFragment();
                pushFragment(myHistoryDetailFragment, bundle, true);
            }
        });
        adapter.notifyDataSetChanged();
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
                        adapter = null;
                        historyList.clear();
                        pageNo = 1;
                        callSavePersonalIntroduction(false);
                    }
                }, 600);
            }
        });
    }
}
