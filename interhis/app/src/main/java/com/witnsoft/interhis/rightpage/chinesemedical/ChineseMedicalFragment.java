package com.witnsoft.interhis.rightpage.chinesemedical;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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

import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.updatemodel.ChuFangChinese;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.interhis.mainpage.WritePadDialog;
import com.witnsoft.libinterhis.utils.ClearEditText;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

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

    private static final String TAG = "ChineseMedicalFragment";
    private static final String PKG = "com.witnsoft.interhis";
    public static final int REQUEST_PERMISSION = 100;
    private OnPageChanged onPageChanged;

    private View rootView;
    private List<ChineseDetailModel> searchList = new ArrayList<>();
    private List<ChineseDetailModel> medTopList = new ArrayList<>();
    private ChineseMedSearchAdapter chineseMedSearchAdapter = null;
    private ChineseMedicalTopAdapter chineseMedTopAdapter = null;
    private Gson gson;
    private PackageManager pm;

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
        aiid = getArguments().getString("aiid");
        tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count), "0"));
        gson = new Gson();
        initSearch();
        initData();
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
                            acsm = etUsage.getText().toString();
                            WritePadDialog writePadDialog = new WritePadDialog(null, null, null, null, null, null,
                                    ChineseMedicalFragment.this, R.style.SignBoardDialog, null);
                            writePadDialog.show();
                            writePadDialog.setCanceledOnTouchOutside(true);
                        }
                    }
                });
    }

    private void initSearch() {
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
        }
    }

    /**
     * 读取数据库(开具处方药)
     */
    private void initData() {
        Observable.create(new Observable.OnSubscribe<List<ChineseDetailModel>>() {
            @Override
            public void call(Subscriber<? super List<ChineseDetailModel>> subscriber) {
                try {
                    List<ChineseDetailModel> chineseTopList = HisDbManager.getManager().findChineseDetailModel(helperId);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(chineseTopList);
                        subscriber.onCompleted();
                        return;
                    }

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DbException e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<ChineseDetailModel>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "数据库读取异常（读取medTopList）", Toast.LENGTH_LONG).show();
                        initTopMed();
                    }

                    @Override
                    public void onNext(List<ChineseDetailModel> list) {
                        medTopList = list;
                        initTopMed();
                    }
                });
    }

    // 读取数据库该患者处方药品并显示到上方列表视图
    private void initTopMed() {
        if (null == chineseMedTopAdapter) {
            chineseMedTopAdapter = new ChineseMedicalTopAdapter(getActivity(), medTopList);
            gvMedTop.setAdapter(chineseMedTopAdapter);
            gvMedTop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            searchModel.setAccid(helperId);
            medTopList.add(searchModel);
            chineseMedTopAdapter.notifyDataSetChanged();
            amountShow();
        }
    }

    @Override
    public void onMedChange(ChineseDetailModel searchModel) {
        // 处方修改
        if (null != medTopList && 0 < medTopList.size()) {
            for (int i = 0; i < medTopList.size(); i++) {
                if (medTopList.get(i).getCmc().equals(searchModel.getCmc())) {
                    searchModel.setAccid(helperId);
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
        this.bitmap = bitmap;
        updateImg();
    }

    private Bitmap bitmap = null;

    private void updateImg() {
        pm = getActivity().getPackageManager();
        // 读写权限
        boolean readPermission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, PKG));
        boolean writePermission = (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, PKG));
        if (Build.VERSION.SDK_INT >= 23) {
            if (readPermission && writePermission) {
                startUpdate();
            } else {
                String[] PERMISSIONS_CAMERA = {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                };
                requestPermissions(PERMISSIONS_CAMERA, REQUEST_PERMISSION);
            }
        } else {
            startUpdate();
        }
    }

    private void startUpdate() {
        String signPath = saveBitmap(bitmap);
        ivSignature.setImageBitmap(bitmap);
        // 上传图片和药方
        callZdsmData(signPath);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            try {
                startUpdate();
            } catch (Exception e) {
                Toast.makeText(getActivity(), getResources().getString(R.string.none_permission), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 读取数据库(诊断说明)
     */
    private void callZdsmData(final String signPath) {
        Observable.create(new Observable.OnSubscribe<ChineseModel>() {
            @Override
            public void call(Subscriber<? super ChineseModel> subscriber) {
                try {
                    ChineseModel model = HisDbManager.getManager().findChineseModel(helperId);
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(model);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (DbException e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ChineseModel>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "数据库读取异常（读取诊断说明）", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(ChineseModel chineseModel) {
                        if (null != chineseModel && !TextUtils.isEmpty(chineseModel.getZdsm())) {
                            // 诊断说明
                            zdsm = chineseModel.getZdsm();
                        }
                        callUpdate(signPath);
                    }
                });
    }

    /**
     * 数据上传
     */
    private static final String TN_DOC_KAIYAO = "F27.APP.01.06";
    private OTRequest otRequest;
    // aiid
    private String aiid;
    // 诊断说明（肝热气滞症、脾胃不和症）
    private String zdsm;
    // 付数（2）
    private String acmxs;
    // 用法用量
    private String acsm;
    // 金额
    private String je;

    private void callUpdate(final String path) {
        //生成json串 并上传服务器
        final ChuFangChinese chufang = new ChuFangChinese();
        otRequest = new OTRequest(getActivity().getBaseContext());
        otRequest.setTN(TN_DOC_KAIYAO);
        final DataModel data = new DataModel();
        data.setDataJSONStr(String.valueOf(chufang.fromJSON(medTopList, aiid, zdsm, acmxs, acsm, je)));
        otRequest.setDATA(data);
        NetTool.getInstance().startRequest(false, true, getActivity(), null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map map, String s) {
                JSONObject json = new JSONObject(map);
                try {
                    String acid = json.getJSONObject("DATA").getString("acid");
                    callUpdateImg(acid, path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError!!!!!!!!!!!!!: " + "请求失败");
            }
        });
    }

    /**
     * 手签图片上传
     */
    private OkHttpClient okHttpClient;
    private Handler handler = new Handler(Looper.getMainLooper());

    private void callUpdateImg(String acid, String path) {
        final String url = "https://zy.renyibao.com/FileUploadServlet";
        File file = new File(path);
        okHttpClient = (new OkHttpClient.Builder()).retryOnConnectionFailure(true).connectTimeout(5L, TimeUnit.SECONDS)
                .cache(new Cache(Environment.getExternalStorageDirectory(), 10485760L)).build();
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name= \"file\"; filename=\"img.png\""), fileBody)
                .addFormDataPart("fjlb", "ask_chinese")
                .addFormDataPart("keyid", acid)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        this.okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ChineseMedicalFragment.this.handler.post(new Runnable() {
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getActivity(), "网络连接超时", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        final String resp = response.body().string();
                        if (!TextUtils.isEmpty(resp)) {
                            HashMap mapObj = new HashMap();
                            final Map map = (Map) gson.fromJson(resp, mapObj.getClass());
                            String errCode = "";
                            if (null != map.get("errcode")) {
                                try {
                                    errCode = String.valueOf(map.get("errcode"));
                                    if (!TextUtils.isEmpty(errCode) && "200".equals(errCode)) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getActivity(), getResources().getString(R.string.update_success), Toast.LENGTH_LONG).show();

                                                createMedical(helperId, "中药", aiid, acmxs, je);
                                                // 存储数据库
                                                saveData();
                                            }
                                        });
                                    } else if (!TextUtils.isEmpty(errCode)) {
                                        if (!TextUtils.isEmpty(String.valueOf(map.get("errmsg")))) {
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getActivity(), String.valueOf(map.get("errmsg")), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                } catch (ClassCastException var11) {
                                    ;
                                }
                            }
                            ChineseMedicalFragment.this.handler.post(new Runnable() {
                                public void run() {

                                }
                            });
                        }
                    } catch (IOException var4) {
                        ;
                    }
                }
            }
        });
    }

    /**
     * 上传服务器成功后转聊天
     */
    private void createMedical(String userName, String yaofangType, String yaofangNum, String yaoNum, String yaofangPrice) {
        EMMessage message = EMMessage.createTxtSendMessage("yaofang", helperId);
        message.setAttribute("type", "yaofang");
        message.setAttribute("userName", userName);
        message.setAttribute("yaofangType", yaofangType);
        message.setAttribute("yaofangNum", yaofangNum);
        message.setAttribute("yaoNum", yaoNum);
        message.setAttribute("yaofangPrice", yaofangPrice);
        EMClient.getInstance().chatManager().sendMessage(message);
        onPageChanged.callBack();
    }

    /**
     * 上传服务器成功后存数据库
     */
    private void saveData() {
        try {
            if (null != medTopList && 0 < medTopList.size()) {
                for (int i = 0; i < medTopList.size(); i++) {
                    if (TextUtils.isEmpty(medTopList.get(i).getTime())) {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                        String date = sDateFormat.format(new java.util.Date());
                        medTopList.get(i).setTime(date);
                    }
                }
            }
            HisDbManager.getManager().saveChineseDetailList(medTopList);
        } catch (DbException e) {

        }
    }

    /**
     * 创建手写签名文件
     */
    private static final String TMP_PATH = "/DCIM/Camera/";

    private String saveBitmap(Bitmap bitmap) {

        // 首先保存图片
        String dir = Environment.getExternalStorageDirectory().toString() + TMP_PATH;
        File appDir = new File(dir);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date()) + ".png";
        File file = new File(appDir, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {

        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), getResources().getString(R.string.card_fali_msg), Toast.LENGTH_LONG).show();
        }
        return dir + fileName;
    }

    /**
     * 固定位置显示药材名字
     */
    private void initFixedSearchData() {
        final String[] fixedMed = getActivity().getResources().getStringArray(R.array.chinese_fixed_list);
        Observable.create(new Observable.OnSubscribe<List<Map<String, String>>>() {
            @Override
            public void call(Subscriber<? super List<Map<String, String>>> subscriber) {
                try {
                    List<Map<String, String>> asd = new ArrayList<>();
                    for (String item : fixedMed) {
                        List<String> columnName = new ArrayList<>();
                        Cursor cursor = DataHelper.getInstance(getContext()).getFixedMed(item);
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

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(asd);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Map<String, String>>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "数据库读取异常", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<Map<String, String>> asd) {
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
                });


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
        // 付数
        acmxs = String.valueOf(numMedCount);
        //金额
        je = String.valueOf(moneyMedCount);
    }

    private Map<String, String> map = new HashMap<>();

    private void searchData(final String pinyin) {
        //根据拼音查询数据
        Observable.create(new Observable.OnSubscribe<List<Map<String, String>>>() {
            @Override
            public void call(Subscriber<? super List<Map<String, String>>> subscriber) {
                try {
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
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(asd);
                        subscriber.onCompleted();
                        return;
                    }
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {

                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onError(e);
                    }
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Map<String, String>>>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getActivity(), "数据库读取异常", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(List<Map<String, String>> asd) {
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
                });


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

    public void setOnPageChanged(OnPageChanged onPageChanged) {
        this.onPageChanged = onPageChanged;
    }
}
