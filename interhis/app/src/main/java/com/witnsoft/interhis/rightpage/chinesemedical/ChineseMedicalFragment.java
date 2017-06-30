package com.witnsoft.interhis.rightpage.chinesemedical;

import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.libinterhis.utils.ClearEditText;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_chinese_medical)
public class ChineseMedicalFragment extends Fragment implements MedicalCountDialog.CallBackMedCount {

    @ViewInject(R.id.keyboard)
    private KeyboardView keyboardView;
    @ViewInject(R.id.et_search)
    private ClearEditText etSearch;
    @ViewInject(R.id.gv_search)
    private GridView gvSearch;
    @ViewInject(R.id.tv_med_count)
    private TextView tvMedCount;
    @ViewInject(R.id.gv_med_top)
    private GridView gvMedTop;

    private View rootView;

    private List<ChineseDetailModel> searchList = new ArrayList<>();
    private ChineseMedSearchAdapter chineseMedSearchAdapter = null;

    private List<ChineseModel> chineseTopList = new ArrayList<>();

    private String helperId;

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
        try {
            this.helperId = getArguments().getString("userId").toLowerCase();
        } catch (Exception e) {
            this.helperId = getArguments().getString("userId");
        }
        tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count), "0"));
        initTopMed();
        initFixedSearchData();
        initKeyBoard();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String pinyin = etSearch.getText().toString();
                if (pinyin.length() >= 1) {
                    // 从数据库拼音搜索
                    searchData(pinyin);
                } else {
                    // 从数据库按照固定药方搜索
                    initFixedSearchData();
                }
            }
        });
    }

    // 读取数据库该患者处方药品并显示到上方列表视图
    // TODO: 2017/6/30 初始化上方所选药品
    private void initTopMed() {
        try {
            chineseTopList = HisDbManager.getManager().findChineseMode(helperId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (null != chineseTopList && 0 < chineseTopList.size()) {
            for (int i = 0; i < chineseTopList.size(); i++) {

            }
        }
    }

    // 选择处方药品存数据库并刷新上方列表视图
    @Override
    public void setMedCount(ChineseDetailModel searchModel, String medCount) {
        // 选处方药回调
        Toast.makeText(getActivity(), medCount, Toast.LENGTH_LONG).show();
        if (!TextUtils.isEmpty(searchModel.getCmc()) && !TextUtils.isEmpty(medCount)) {
            // TODO: 2017/6/30 搜索选择返回存数据库并刷新视图
//            try {
//                ChineseModel chineseModel = new ChineseModel();
//                HisDbManager.getManager().saveAskChinese();
//            }catch (DbException e){
//
//            }
        }
    }

    //固定位置显示药材名字
    private void initFixedSearchData() {
        String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
        List<Map<String, String>> asd = new ArrayList<>();
        for (int j = 0; j < fixedMed.length; j++) {
            List<String> columnName = new ArrayList<>();
            Cursor cursor = DataHelper.getInstance(getContext()).getFixedMed(fixedMed[j]);
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                columnName.add(cursor.getColumnName(i));
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    map = new HashMap<>();
                    for (int i = 0; i < columnName.size(); i++) {
                        map.put(columnName.get(i), cursor.getString(cursor.getColumnIndex(columnName.get(i))));
                    }
                    asd.add(map);
                    break;
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        //显示搜索列表药名
        searchList.clear();
        for (Map<String, String> map : asd) {
            ChineseDetailModel chinese = new ChineseDetailModel();
            chinese.setCmc(map.get("xmmc"));
            // TODO: 2017/6/30 重新改表结构，将数据set进去
//                chinese.setCmc(map.get("bzjg"));
//                chinese.setCmc(map.get("sfxmbm"));
//                chinese.setCmc(map.get("sfdlbm"));
//                chinese.setCmc(map.get("xmrj"));
//                chinese.setCmc(map.get("yaoid"));
            searchList.add(chinese);
        }
        if (null == chineseMedSearchAdapter) {
            chineseMedSearchAdapter = new ChineseMedSearchAdapter(getActivity(), searchList);
            gvSearch.setAdapter(chineseMedSearchAdapter);
            gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[] medCountNum = getActivity().getResources().getStringArray(R.array.med_count_num);
                    final MedicalCountDialog medicalCountDialog
                            = new MedicalCountDialog(ChineseMedicalFragment.this, searchList.get(position), medCountNum);
                    medicalCountDialog.init();
                }
            });
        } else {
            chineseMedSearchAdapter.notifyDataSetChanged();
        }
    }
//    private void initFixedSearchData() {
//        String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
//        for (int i = 0; i < fixedMed.length; i++) {
//            ChineseDetailModel medical = new ChineseDetailModel();
//            medical.setCmc(fixedMed[i]);
//            // TODO: 2017/6/30 固定处方药其他元素
//            searchList.add(medical);
//        }
//        chineseMedSearchAdapter = new ChineseMedSearchAdapter(getActivity(), searchList);
//        gvSearch.setAdapter(chineseMedSearchAdapter);
//        gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String[] medCountNum = getActivity().getResources().getStringArray(R.array.med_count_num);
//                final MedicalCountDialog medicalCountDialog
//                        = new MedicalCountDialog(ChineseMedicalFragment.this, searchList.get(position), medCountNum);
//                medicalCountDialog.init();
//            }
//        });
//    }

    private Map<String, String> map = new HashMap<>();

    private void searchData(String pinyin) {
        //根据拼音查询数据
        String xmmc = null;
        List<Map<String, String>> asd = new ArrayList<>();
        List<String> columnName = new ArrayList<>();
        Cursor cursor = DataHelper.getInstance(getContext()).getXMRJ(pinyin);
        for (int i = 0; i < cursor.getColumnCount(); i++) {
            columnName.add(cursor.getColumnName(i));
        }
        if (cursor != null && cursor.moveToFirst()) {
            do {
                map = new HashMap<>();
                for (int i = 0; i < columnName.size(); i++) {
                    map.put(columnName.get(i), cursor.getString(cursor.getColumnIndex(columnName.get(i))));
                }
                asd.add(map);
//                    String []a = cursor.getColumnNames();
//                    xmmc = cursor.getString(cursor.getColumnIndex("xmmc"));
//                    asd.add(xmmc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //显示搜索列表药名
        searchList.clear();
        for (Map<String, String> map : asd) {
            ChineseDetailModel chinese = new ChineseDetailModel();
            chinese.setCmc(map.get("xmmc"));

            // TODO: 2017/6/30 测试！！！！！！！！！！！！！11
            String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
            for (int i = 0; i < fixedMed.length; i++) {
                if (map.get("xmmc").equals(fixedMed[i])) {
                    Test(map, "bzjg");
                    Test(map, "sfxmbm");
                    Test(map, "sfdlbm");
                    Test(map, "xmrj");
                    Test(map, "yaoid");
                    Test(map, "xmmc");
//                    Log.e("ChineseMedicalFragment", "bzjg:" + map.get("bzjg") + ";;sfxmbm:" + map.get("sfxmbm") + ";;sfdlbm:" + map.get("sfdlbm")
//                            + ";;xmrj:" + map.get("xmrj") + ";;yaoid:" + map.get("yaoid") + ";;xmmc:" + map.get("xmmc"));
                }
            }
            // TODO: 2017/6/30 重新改表结构，将数据set进去
//                chinese.setCmc(map.get("bzjg"));
//                chinese.setCmc(map.get("sfxmbm"));
//                chinese.setCmc(map.get("sfdlbm"));
//                chinese.setCmc(map.get("xmrj"));
//                chinese.setCmc(map.get("yaoid"));
            searchList.add(chinese);
        }
        chineseMedSearchAdapter.notifyDataSetChanged();
    }

    private void Test(Map<String, String> map, String key) {
        if (!TextUtils.isEmpty(map.get(key))) {
            Log.e("ChineseMedicalFragment", key + ":" + map.get(key));
        }
    }

    private Keyboard k1;//字母键盘
    private Keyboard k2;// 数字键盘
    public boolean isnun = false;// 是否数据键盘
    public boolean isupper = false;//是否大写

    private void initKeyBoard() {
        etSearch.setInputType(InputType.TYPE_NULL);
        k1 = new Keyboard(getActivity(), R.xml.qwerty);
        k2 = new Keyboard(getActivity(), R.xml.symbols);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Keyboard.Key> keylist = k1.getKeys();
        if (isupper) {//大写切换小写
            isupper = false;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {//小写切换大写
            isupper = true;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Editable editable = etSearch.getText();
            int start = etSearch.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {//删除
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {//切换数字键盘
                if (isnun) {
                    isnun = false;
                    keyboardView.setKeyboard(k1);
                } else {
                    isnun = true;
                    keyboardView.setKeyboard(k2);
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                changeKey();
                keyboardView.setKeyboard(k1);
            } else if (primaryCode == Keyboard.KEYCODE_CANCEL) {
//                initData();
            } else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };
}
