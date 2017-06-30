package com.witnsoft.interhis.rightpage.chinesemedical;

import android.database.Cursor;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.libinterhis.utils.ClearEditText;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

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

    private View rootView;

    private List<ChineseDetailModel> searchList = new ArrayList<>();
    private ChineseMedSearchAdapter chineseMedSearchAdapter = null;

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
        tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count), "0"));
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
                searchData();
            }
        });
    }

    //固定位置显示药材名字
    private void initFixedSearchData() {
        String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
        for (int i = 0; i < fixedMed.length; i++) {
            ChineseDetailModel medical = new ChineseDetailModel();
            medical.setCmc(fixedMed[i]);
            searchList.add(medical);
        }
        chineseMedSearchAdapter = new ChineseMedSearchAdapter(getActivity(), searchList);
        gvSearch.setAdapter(chineseMedSearchAdapter);
        gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] medCountNum = getActivity().getResources().getStringArray(R.array.med_count_num);
                final MedicalCountDialog medicalCountDialog
                        = new MedicalCountDialog(ChineseMedicalFragment.this, searchList.get(position).getCmc(), medCountNum);
                medicalCountDialog.init();
            }
        });
    }

    private void searchData() {
        //根据拼音查询数据
        String pinyin = etSearch.getText().toString();
        String xmmc = null;
        List<String> asd = new ArrayList<>();
        Cursor cursor = DataHelper.getInstance(getContext()).getXMRJ(pinyin);
        if (pinyin.length() >= 1) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    xmmc = cursor.getString(cursor.getColumnIndex("xmmc"));
                    asd.add(xmmc);
                } while (cursor.moveToNext());
            }
            cursor.close();
            //显示搜索列表药名
            searchList.clear();
            for (String s : asd) {
                ChineseDetailModel chinese = new ChineseDetailModel();
                chinese.setCmc(s);
                searchList.add(chinese);
            }
            chineseMedSearchAdapter.notifyDataSetChanged();
        } else {
            searchList.clear();
            String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
            for (int i = 0; i < fixedMed.length; i++) {
                ChineseDetailModel medical = new ChineseDetailModel();
                medical.setCmc(fixedMed[i]);
                searchList.add(medical);
            }
            chineseMedSearchAdapter.notifyDataSetChanged();
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

    @Override
    public void setMedCount(String medCount) {
        // 选药数量回调
        Toast.makeText(getActivity(), medCount, Toast.LENGTH_LONG).show();
    }
}
