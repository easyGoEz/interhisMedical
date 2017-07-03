package com.witnsoft.interhis.rightpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.hyphenate.easeui.ui.EaseChatFragment;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.rightpage.chinesemedical.ChineseMedicalFragment;
import com.witnsoft.interhis.rightpage.diagnosis.DiagnosisFragment;
import com.witnsoft.interhis.rightpage.history.HistoryFragment;
import com.witnsoft.interhis.rightpage.westernmedical.WesternFragment;
import com.witnsoft.interhis.rightpage.withchinesemedical.WithChineseMedicalFragment;
import com.witnsoft.interhis.setting.ChildBaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_right_main)
public class RightMainFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private ArrayList<Fragment> viewContainter = new ArrayList<Fragment>();

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
        Fragment chatFragment = new EaseChatFragment();
        initChat(chatFragment);
        Fragment diagnosisFragment = new DiagnosisFragment();
        initDiagnosis(diagnosisFragment);
        Fragment chineseMedicalFragment = new ChineseMedicalFragment();
        initChineseMed(chineseMedicalFragment);
        Fragment withChineseMedicalFragment = new WithChineseMedicalFragment();
        Fragment westernFragment = new WesternFragment();
        Fragment historyFragment = new HistoryFragment();
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
        viewPager.setCurrentItem(TAG_CAHT);

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
        fragment.setArguments(bundle);
    }

    private void initChineseMed(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("aiid", this.aiid);
        bundle.putString("userId", this.helperId);
        fragment.setArguments(bundle);
    }

    private void initDiagnosis(Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", this.helperId);
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
                viewPager.setCurrentItem(TAG_WESTERN_MEDICAL);
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
}
