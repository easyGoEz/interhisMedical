package com.witnsoft.interhis.rightpage;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.EaseChatInputMenu;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.login.LoginActivity;
import com.witnsoft.interhis.rightpage.chinesemedical.ChineseMedicalFragment;
import com.witnsoft.interhis.rightpage.chinesemedical.OnPageChanged;
import com.witnsoft.interhis.rightpage.diagnosis.DiagnosisFragment;
import com.witnsoft.interhis.rightpage.history.HistoryFragment;
import com.witnsoft.interhis.rightpage.westernmedical.OnWesternPageChanged;
import com.witnsoft.interhis.rightpage.westernmedical.WesternFragment;
import com.witnsoft.interhis.rightpage.withchinesemedical.WithChineseMedicalFragment;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_right_main)
public class RightMainFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RightMainFragment";
    private View rootView;
    private ArrayList<Fragment> viewContainter = new ArrayList<Fragment>();
    private OnPageChanged onPageChanged;

    private static final int TAG_CAHT = 0;
    private static final int TAG_DIAGNOSIS = 1;
    private static final int TAG_CHINESE_MEDICAL = 2;
    private static final int TAG_WITH_CHINESE_MEDICAL = 3;
    private static final int TAG_WESTERN_MEDICAL = 4;
    private static final int TAG_HISTORY = 5;

    private String aiid;
    private String helperId;
    private String userName;
    private String type1;
    private int single1;
    private String imgDoc;
    private String imgPat;
    private String patName;
    private String patSexName;
    private String patId;

    private boolean isInputVisible;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;
    // 咨询
    @ViewInject(R.id.rb_ask)
    private RadioButton rbChat;
    // 诊断
    @ViewInject(R.id.rb_diagnosis)
    private RadioButton rbDiagnosis;
    // 中药
    @ViewInject(R.id.rb_chinese_medical)
    private RadioButton rbChineseMedical;
    // 中成药
    @ViewInject(R.id.rb_with_chinese_medical)
    private RadioButton rbWithChineseMedical;
    // 西药
    @ViewInject(R.id.rb_western_medical)
    private RadioButton rbWesternMedical;
    // 历史
    @ViewInject(R.id.rb_history)
    private RadioButton rbHistory;

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
        this.aiid = getArguments().getString("aiid");
        try {
            this.helperId = getArguments().getString("userId").toLowerCase();
        } catch (Exception e) {
            this.helperId = getArguments().getString("userId");
        }
        this.userName = getArguments().getString("userName");
        this.type1 = getArguments().getString("type");
        this.single1 = getArguments().getInt("single");
        this.imgDoc = getArguments().getString("img_doc");
        this.imgPat = getArguments().getString("img_pat");
        this.patName = getArguments().getString("pat_name");
        this.patSexName = getArguments().getString("pat_sex_name");
        this.patId = getArguments().getString("pat_id");

        if (TextUtils.isEmpty(getArguments().getString("end_type"))) {
            if ((!TextUtils.isEmpty(getArguments().getString("begin_flag")))
                    && ("y".equals(getArguments().getString("begin_flag")))) {
                // 问诊已经开始，打开输入
                Log.e(TAG, "!!!!!!!!!!!ask going on");
                isInputVisible = true;
            } else {
                // 问诊还未开始（未接诊），隐藏输入
                Log.e(TAG, "!!!!!!!!!!!ask ever begin");
                isInputVisible = false;
            }
        } else {
            Log.e(TAG, "!!!!!!!!!!!ask end");
            // 已经结束问诊，隐藏输入
            isInputVisible = false;
        }

        EaseChatFragment chatFragment = new EaseChatFragment();
        chatFragment.setOnReceivedListener(new EaseChatFragment.OnReceivedListener() {
            @Override
            public void onReceiveClicked(EaseChatInputMenu inputMenu, TextView tv) {
                // 接诊
                callReceiveApi(inputMenu, tv);
            }

            @Override
            public void onHistoryClicked() {
                // 查看历史记录
                viewPager.setCurrentItem(TAG_HISTORY);
            }
        });
        initChat(chatFragment);
        DiagnosisFragment diagnosisFragment = new DiagnosisFragment();
        initDiagnosis(diagnosisFragment);
        ChineseMedicalFragment chineseMedicalFragment = new ChineseMedicalFragment();
        chineseMedicalFragment.setOnPageChanged(new OnPageChanged() {
            @Override
            public void callBack() {
                viewPager.setCurrentItem(TAG_CAHT);
            }
        });
        initMed(chineseMedicalFragment);
        WithChineseMedicalFragment withChineseMedicalFragment = new WithChineseMedicalFragment();
        WesternFragment westernFragment = new WesternFragment();
        westernFragment.setOnWesternPageChanged(new OnWesternPageChanged() {
            @Override
            public void callBack() {
                viewPager.setCurrentItem(TAG_CAHT);
            }
        });
        initMed(westernFragment);
        HistoryFragment historyFragment = new HistoryFragment();
        // viewpager开始添加view
        viewContainter.add(chatFragment);
        viewContainter.add(diagnosisFragment);
        viewContainter.add(chineseMedicalFragment);
        viewContainter.add(withChineseMedicalFragment);
        viewContainter.add(westernFragment);
        viewContainter.add(historyFragment);

        rbChat.setOnClickListener(this);
        rbDiagnosis.setOnClickListener(this);
        rbChineseMedical.setOnClickListener(this);
        rbWithChineseMedical.setOnClickListener(this);
        rbWesternMedical.setOnClickListener(this);
        rbHistory.setOnClickListener(this);

        viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), viewContainter));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int id) {
                switch (id) {
                    case TAG_CAHT:
                        rbChat.setChecked(true);
                        rbDiagnosis.setChecked(false);
                        rbChineseMedical.setChecked(false);
                        rbWithChineseMedical.setChecked(false);
                        rbWesternMedical.setChecked(false);
                        rbHistory.setChecked(false);
                        break;
                    case TAG_DIAGNOSIS:
                        rbChat.setChecked(false);
                        rbDiagnosis.setChecked(true);
                        rbChineseMedical.setChecked(false);
                        rbWithChineseMedical.setChecked(false);
                        rbWesternMedical.setChecked(false);
                        rbHistory.setChecked(false);
                        break;
                    case TAG_CHINESE_MEDICAL:
                        rbChat.setChecked(false);
                        rbDiagnosis.setChecked(false);
                        rbChineseMedical.setChecked(true);
                        rbWithChineseMedical.setChecked(false);
                        rbWesternMedical.setChecked(false);
                        rbHistory.setChecked(false);
                        break;
                    case TAG_WITH_CHINESE_MEDICAL:
                        rbChat.setChecked(false);
                        rbDiagnosis.setChecked(false);
                        rbChineseMedical.setChecked(false);
                        rbWithChineseMedical.setChecked(true);
                        rbWesternMedical.setChecked(false);
                        rbHistory.setChecked(false);
                        break;
                    case TAG_WESTERN_MEDICAL:
                        rbChat.setChecked(false);
                        rbDiagnosis.setChecked(false);
                        rbChineseMedical.setChecked(false);
                        rbWithChineseMedical.setChecked(false);
                        rbWesternMedical.setChecked(true);
                        rbHistory.setChecked(false);
                        break;
                    case TAG_HISTORY:
                        rbChat.setChecked(false);
                        rbDiagnosis.setChecked(false);
                        rbChineseMedical.setChecked(false);
                        rbWithChineseMedical.setChecked(false);
                        rbWesternMedical.setChecked(false);
                        rbHistory.setChecked(true);
                        break;
                    default:
                        break;
                }
            }
        });
        viewPager.setCurrentItem(TAG_CAHT);
        rbChat.setChecked(true);
    }

    private void initChat(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("aiid", this.aiid);
        bundle.putString("userName", this.userName);
        bundle.putString("userId", this.helperId);
        bundle.putString("type", this.type1);
        bundle.putInt("single", this.single1);
        bundle.putString("img_doc", this.imgDoc);
        bundle.putString("img_pat", this.imgPat);
        bundle.putBoolean("input_flag", isInputVisible);
        fragment.setArguments(bundle);
    }

    private void initMed(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("aiid", this.aiid);
        bundle.putString("userId", this.helperId);
        bundle.putString("pat_name", this.patName);
        // 性别
        bundle.putString("pat_sex_name", this.patSexName);
        // 病人id
        bundle.putString("pat_id", this.patId);
        bundle.putBoolean("input_flag", isInputVisible);
        fragment.setArguments(bundle);
    }

    private void initDiagnosis(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", this.helperId);
        bundle.putString("aiid", this.aiid);
        bundle.putBoolean("input_flag", isInputVisible);
        fragment.setArguments(bundle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rb_ask:
                viewPager.setCurrentItem(TAG_CAHT);
                break;
            case R.id.rb_diagnosis:
                viewPager.setCurrentItem(TAG_DIAGNOSIS);
                break;
            case R.id.rb_chinese_medical:
                viewPager.setCurrentItem(TAG_CHINESE_MEDICAL);
                break;
            case R.id.rb_with_chinese_medical:
                viewPager.setCurrentItem(TAG_WITH_CHINESE_MEDICAL);
                break;
            case R.id.rb_western_medical:
                viewPager.setCurrentItem(TAG_WESTERN_MEDICAL);
                break;
            case R.id.rb_history:
                viewPager.setCurrentItem(TAG_HISTORY);
                break;
            default:
                break;
        }

    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> list;

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
            super(fm);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Fragment getItem(int arg0) {
            return list.get(arg0);
        }

    }

    private final class ErrCode {
        private static final String ErrCode_200 = "200";
        private static final String ErrCode_504 = "504";
    }

    /**
     * F27.APP.01.04 接诊
     */
    private void callReceiveApi(final EaseChatInputMenu inputMenu, final TextView tv) {
        OTRequest otRequest = new OTRequest(getActivity());
        // DATA
        DataModel data = new DataModel();
        data.setParam("aiid", aiid);
        otRequest.setDATA(data);
        // TN 接口辨别
        otRequest.setTN("F27.APP.01.04");

        NetTool.getInstance().startRequest(false, true, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map response, String resultCode) {
                if (ErrCode.ErrCode_200.equals(resultCode)) {
                    if (null != response) {
                        RightMainFragment.this.isInputVisible = isInputVisible;
                        tv.setText("已接诊");
                        inputMenu.setVisibility(View.VISIBLE);
                        if (null != onReceiveListener) {
                            onReceiveListener.onReceiveClicked();
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

    public OnReceivedListener onReceiveListener;

    public void setOnReceivedListener(OnReceivedListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    public interface OnReceivedListener {
        void onReceiveClicked();
    }
}
