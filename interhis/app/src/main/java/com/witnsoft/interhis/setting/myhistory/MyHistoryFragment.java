package com.witnsoft.interhis.setting.myhistory;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.utils.ComRecyclerAdapter;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengchengpeng on 2017/6/13.
 */

@ContentView(R.layout.fragment_my_history)
public class MyHistoryFragment extends ChildBaseFragment {
    View rootView;
    private MyHistoryAdapter adapter;
    private List<String> list = new ArrayList<>();

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
        tvVisitCountAll.setText("33");
        tvVisitCountMonth.setText("22");
        tvVisitCountDaily.setText("11");
        callList();

    }

    private void callList() {
        for (int i = 0; i < 8; i++) {
            list.add(String.valueOf(i));
        }
        try {
            freshUi();
        } catch (NullPointerException e) {

        }
    }

    private void freshUi() {
        if (null == adapter) {
            adapter = new MyHistoryAdapter(getActivity().getBaseContext(), list, R.layout.item_ask_history);
            adapter.setFootViewId(R.layout.activity_load_footer);
            rvHistory.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
            rvHistory.setHasFixedSize(true);
            rvHistory.setAdapter(adapter);
        }
        adapter.setCanNotReadBottom(false);
        adapter.setOnRecyclerViewBottomListener(new ComRecyclerAdapter.OnRecyclerViewBottomListener() {
            @Override
            public void OnBottom() {

            }
        });
        adapter.setOnItemClickListener(new ComRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(getActivity(), "点击了" + String.valueOf(position), Toast.LENGTH_LONG).show();
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
                        list.clear();
                        callList();
                    }
                }, 600);
            }
        });
    }
}
