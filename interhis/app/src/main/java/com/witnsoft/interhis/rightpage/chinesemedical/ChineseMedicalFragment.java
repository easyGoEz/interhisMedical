package com.witnsoft.interhis.rightpage.chinesemedical;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.interhis.mainpage.WritePadDialog;
import com.witnsoft.libinterhis.utils.ClearEditText;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

/**
 * Created by zhengchengpeng on 2017/6/29.
 */

@ContentView(R.layout.fragment_chinese_medical)
public class ChineseMedicalFragment extends Fragment implements MedicalCountDialog.CallBackMedCount, WritePadDialog.CallBack {

    @ViewInject(R.id.keyboard)
    private KeyboardView keyboardView;
    @ViewInject(R.id.et_search)
    private ClearEditText etSearch;
    @ViewInject(R.id.gv_search)
    private GridView gvSearch;
    @ViewInject(R.id.tv_med_count)
    private TextView tvMedCount;
    @ViewInject(R.id.tv_med_money)
    private TextView tvMedMoney;
    @ViewInject(R.id.gv_med_top)
    private GridView gvMedTop;
    @ViewInject(R.id.btn_save)
    private Button btnSave;
    @ViewInject(R.id.et_usage)
    private EditText etUsage;
    @ViewInject(R.id.iv_signature)
    private ImageView ivSignature;

    private View rootView;
    private static final String TAG = "ChineseMedicalFragment";
    private List<ChineseDetailModel> searchList = new ArrayList<>();
    private List<ChineseDetailModel> medTopList = new ArrayList<>();
    private ChineseMedSearchAdapter chineseMedSearchAdapter = null;
    private ChineseMedicalTopAdapter chineseMedTopAdapter = null;

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
        initClick();
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

    private void initClick() {
        RxView.clicks(btnSave)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (TextUtils.isEmpty(etUsage.getText().toString())) {
                            Toast.makeText(getActivity(), getResources().getString(R.string.pleas_enter_usage),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            WritePadDialog writePadDialog = new WritePadDialog(null, null, null, null, null, null,
                                    ChineseMedicalFragment.this, R.style.SignBoardDialog, null);
                            writePadDialog.show();
                            writePadDialog.setCanceledOnTouchOutside(true);
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
        // TODO: 2017/6/30 medToList未初始化
        if (null == chineseMedTopAdapter) {
            chineseMedTopAdapter = new ChineseMedicalTopAdapter(getActivity(), medTopList);
            gvMedTop.setAdapter(chineseMedTopAdapter);
            gvMedTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO: 2017/6/30 编辑弹出框
                    String[] medCountNum = getActivity().getResources().getStringArray(R.array.med_count_num);
                    final MedicalCountDialog medicalCountDialog
                            = new MedicalCountDialog(ChineseMedicalFragment.this, medTopList.get(position), medCountNum);
                    medicalCountDialog.init(true);
                }
            });
        } else {
            chineseMedTopAdapter.notifyDataSetChanged();
        }
        amountShow();
    }

    // 选择处方药品存数据库并刷新上方列表视图
    @Override
    public void onMedAdd(ChineseDetailModel searchModel) {
        // 处方增加
        if (null != searchModel) {
            medTopList.add(searchModel);
            chineseMedTopAdapter.notifyDataSetChanged();
            amountShow();
            // TODO: 2017/6/30 搜索选择返回存数据库并刷新视图
//            try {
//                ChineseModel chineseModel = new ChineseModel();
//                chineseModel.setChineseDetailModel(medTopList);
//                HisDbManager.getManager().saveAskChinese(chineseModel);
//            } catch (DbException e) {
//
//            }
        }
    }

    @Override
    public void onMedChange(ChineseDetailModel searchModel) {
        // 处方修改
        if (null != medTopList && 0 < medTopList.size()) {
            for (int i = 0; i < medTopList.size(); i++) {
                if (medTopList.get(i).getCmc().equals(searchModel.getCmc())) {
                    medTopList.set(i, searchModel);
                    break;
                }
            }
            chineseMedTopAdapter.notifyDataSetChanged();
            amountShow();
        }
    }

    @Override
    public void onMedDelete(ChineseDetailModel searchModel) {
        // 处方删除
        if (null != medTopList && 0 < medTopList.size()) {
            for (int i = 0; i < medTopList.size(); i++) {
                if (medTopList.get(i).getCmc().equals(searchModel.getCmc())) {
                    medTopList.remove(i);
                    break;
                }
            }
            chineseMedTopAdapter.notifyDataSetChanged();
            amountShow();
        }
    }

    @Override
    public void onHandImgOk(Bitmap bitmap) {
        // 手写签名返回
        String signPath = createFile(bitmap);
//        Bitmap zoombm = getCompressBitmap(signPath);
        ivSignature.setImageBitmap(bitmap);
        Log.e(TAG, "path for hand = " + signPath);
    }

    /**
     * 创建手写签名文件
     *
     * @return
     */
    private static final String TMP_PATH = "/DCIM/Camera/";

    private String createFile(Bitmap mSignBitmap) {
        ByteArrayOutputStream baos = null;
        String _path = null;
        try {
            //创建目录
            String sign_dir = Environment.getExternalStorageDirectory().toString() + TMP_PATH
                    + File.separator;
            File localFile = new File(sign_dir);
            if (!localFile.exists()) {
                localFile.mkdir();
            }
            //拼接好文件路径和名称
            File finalImageFile = new File(localFile, System.currentTimeMillis() + "img.png");
            if (finalImageFile.exists()) {
                finalImageFile.delete();
            }
            try {
                finalImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //文件的读取
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(finalImageFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            if (mSignBitmap == null) {
                Toast.makeText(getActivity(), "图片不存在", Toast.LENGTH_SHORT).show();
            }

            _path = sign_dir + System.currentTimeMillis() + "img.png";
            baos = new ByteArrayOutputStream();
            mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.e(TAG, "createFile: " + finalImageFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] photoBytes = baos.toByteArray();
            if (photoBytes != null) {
                new FileOutputStream(new File(_path)).write(photoBytes);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _path;
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
            // 药品名称
            chinese.setCmc(map.get("xmmc"));
            // 单价
            chinese.setDj(map.get("bzjg"));
            // 中药代码
            chinese.setCdm(map.get("sfxmbm"));
            searchList.add(chinese);
        }
        if (null == chineseMedSearchAdapter) {
            chineseMedSearchAdapter = new ChineseMedSearchAdapter(getActivity(), searchList);
            gvSearch.setAdapter(chineseMedSearchAdapter);
            gvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    boolean isSame = false;
                    if (null != medTopList && 0 < medTopList.size()) {
                        for (int i = 0; i < medTopList.size(); i++) {
                            if (medTopList.get(i).getCmc().equals(searchList.get(position).getCmc())) {
                                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.med_has_been_chosen),
                                        Toast.LENGTH_LONG).show();
                                isSame = true;
                            }
                        }
                    }
                    if (!isSame) {
                        String[] medCountNum = getActivity().getResources().getStringArray(R.array.med_count_num);
                        final MedicalCountDialog medicalCountDialog
                                = new MedicalCountDialog(ChineseMedicalFragment.this, searchList.get(position), medCountNum);
                        medicalCountDialog.init(false);
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.med_has_been_chosen)
                                , Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            chineseMedSearchAdapter.notifyDataSetChanged();
        }
    }

    // 金额和数量显示
    private void amountShow() {
        int numMedCount = 0;
        double moneyMedCount = 0.0;
        if (null != medTopList && 0 < medTopList.size()) {
            numMedCount = medTopList.size();
            for (int i = 0; i < medTopList.size(); i++) {
                int numCount = 0;
                double moneyCount = 0.0;
                if (!TextUtils.isEmpty(medTopList.get(i).getSl())) {
                    try {
                        numCount = Integer.parseInt(medTopList.get(i).getSl());
                    } catch (Exception e) {
                        numCount = 0;
                    }
                } else {
                    numCount = 0;
                }
                if (!TextUtils.isEmpty(medTopList.get(i).getDj())) {
                    try {
                        moneyCount = Double.parseDouble(medTopList.get(i).getDj());
                    } catch (Exception e) {
                        moneyCount = 0.0;
                    }
                } else {
                    moneyCount = 0.0;
                }
                moneyMedCount = moneyMedCount + (numCount * moneyCount);
            }
        } else {
            numMedCount = 0;
            moneyMedCount = 0.0;
        }
        tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count),
                String.valueOf(numMedCount)));
        tvMedMoney.setText(String.valueOf(moneyMedCount) + getResources().getString(R.string.yuan));
    }

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
            } while (cursor.moveToNext());
        }
        cursor.close();
        //显示搜索列表药名
        searchList.clear();
        for (Map<String, String> map : asd) {
            ChineseDetailModel chinese = new ChineseDetailModel();
            // 药品名称
            chinese.setCmc(map.get("xmmc"));
            // 单价
            chinese.setDj(map.get("bzjg"));
            // 中药代码
            chinese.setCdm(map.get("sfxmbm"));
            searchList.add(chinese);
        }
        chineseMedSearchAdapter.notifyDataSetChanged();
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
