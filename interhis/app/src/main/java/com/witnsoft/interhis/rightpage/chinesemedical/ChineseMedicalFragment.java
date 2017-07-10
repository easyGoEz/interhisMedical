package com.witnsoft.interhis.rightpage.chinesemedical;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.BaseV4Fragment;
import com.witnsoft.interhis.updatemodel.ChuFangChinese;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.interhis.mainpage.WritePadDialog;
import com.witnsoft.interhis.utils.ui.HisKeyboardView;
import com.witnsoft.libinterhis.utils.ClearEditText;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.json.JSONArray;
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
public class ChineseMedicalFragment extends BaseV4Fragment implements MedicalCountDialog.CallBackMedCount, WritePadDialog.CallBack, MedCountDialog.CallBackMedCount {

    @ViewInject(R.id.kb)
    private HisKeyboardView kb;
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
    @ViewInject(R.id.tv_empty)
    private TextView tvEmpty;
    @ViewInject(R.id.ll_search)
    private AutoScaleLinearLayout llSearch;

    private static final String TAG = "ChineseMedicalFragment";
    private static final String PKG = "com.witnsoft.interhis";
    private static final int REQUEST_PERMISSION = 100;
    private OnPageChanged onPageChanged;

    private View rootView;
    private List<ChineseDetailModel> searchList = new ArrayList<>();
    private List<ChineseDetailModel> medTopList = new ArrayList<>();
    private List<ChineseDetailModel> deleteList = new ArrayList<>();
    private ChineseMedSearchAdapter chineseMedSearchAdapter = null;
    private ChineseMedicalTopAdapter chineseMedTopAdapter = null;
    private Gson gson;
    private PackageManager pm;

    private String helperId;
    private String patName;
    private String patSexName;
    private String patId;

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
        initSign();
        initClick();
    }

    private void init() {
        try {
            this.helperId = getArguments().getString("userId").toLowerCase();
        } catch (Exception e) {
            this.helperId = getArguments().getString("userId");
        }
        this.patName = getArguments().getString("pat_name");
        this.aiid = getArguments().getString("aiid");
        this.patSexName = getArguments().getString("pat_sex_name");
        this.patId = getArguments().getString("pat_id");
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
                if (1 <= pinyin.length()) {
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
                            String[] medCount = getActivity().getResources().getStringArray(R.array.med_count);
                            final MedCountDialog medCountDialog
                                    = new MedCountDialog(ChineseMedicalFragment.this, medCount);
                            medCountDialog.init();
                        }
                    }
                });
    }

    @Override
    public void onCount(int count) {
        acmxs = String.valueOf(count);
        tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count), acmxs));
        amountShow();
        acsm = etUsage.getText().toString();
        WritePadDialog writePadDialog = new WritePadDialog(ChineseMedicalFragment.this, R.style.SignBoardDialog);
        writePadDialog.show();
        writePadDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * 初始化签名
     */
    private ChineseModel chineseModel = null;

    private void initSign() {
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
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(ChineseModel model) {
                        chineseModel = model;
                        if (null != model) {
                            // 诊断说明
                            if (!TextUtils.isEmpty(model.getZdsm())) {
                                zdsm = model.getZdsm();
                            }
                            // 用法用量
                            if (!TextUtils.isEmpty(model.getAcSm())) {
                                acsm = model.getAcSm();
                                etUsage.setText(acsm);
                            }
                            // 金额
                            if (!TextUtils.isEmpty(model.getJe())) {
                                je = model.getJe();
                                try {
                                    double value = convert(Double.parseDouble(je));
                                    tvMedMoney.setText(String.valueOf(value) + getResources().getString(R.string.yuan));
                                } catch (Exception e) {
                                    tvMedMoney.setText("");
                                }
                            }
                            // 处方明细数量
                            if (!TextUtils.isEmpty(model.getAcMxs())) {
                                acmxs = model.getAcMxs();
                                tvMedCount.setText(String.format(getActivity().getResources().getString(R.string.medical_count), acmxs));
                            }
                        }
                        if (null != model && !TextUtils.isEmpty(model.getDocQm())) {
                            try {
                                Glide.with(getActivity())
                                        .load(model.getDocQm())
                                        .into(ivSignature);
                            } catch (Exception e) {

                            }
                        }
                    }
                });
    }

    /**
     * 初始化搜索列表
     */
    private void initSearch() {
        if (null == chineseMedSearchAdapter) {
            chineseMedSearchAdapter = new ChineseMedSearchAdapter(getActivity(), searchList);
            gvSearch.setAdapter(chineseMedSearchAdapter);
            gvSearch.setEmptyView(tvEmpty);
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
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
                        initTopMed();
                    }

                    @Override
                    public void onNext(List<ChineseDetailModel> list) {
                        medTopList.clear();
                        if (null != list && 0 < list.size()) {
                            for (int i = 0; i < list.size(); i++) {
                                medTopList.add(list.get(i));
                            }
                        }
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
            deleteList.add(searchModel);
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
//        ivSignature.setImageBitmap(bitmap);
        // 上传图片和药方
        callUpdate(signPath);
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
        data.setDataJSONStr(String.valueOf(chufang.fromJSONChinese(medTopList, aiid, zdsm, acmxs, acsm, je)));
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

    private void callUpdateImg(final String acid, String path) {
        showWaitingDialogCannotCancel();
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
                                hideWaitingDialog();
                                Toast.makeText(getActivity(), getResources().getString(R.string.net_error), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                hideWaitingDialog();
                if (response.isSuccessful()) {
                    try {
                        final String resp = response.body().string();
                        if (!TextUtils.isEmpty(resp)) {
                            ChineseMedicalFragment.this.handler.post(new Runnable() {
                                public void run() {
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
                                                        createMedical(helperId, "中药", aiid, acmxs, je, acsm, acid, medTopList);
                                                        String picUrl = "";
                                                        if (null != map.get("fjlj")) {
                                                            try {
                                                                picUrl = String.valueOf(map.get("fjlj"));
                                                                Glide.with(getActivity())
                                                                        .load(picUrl)
                                                                        .into(ivSignature);
                                                            } catch (Exception e) {
                                                                Toast.makeText(getActivity(), getResources().getString(R.string.pic_error), Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                        // 存储数据库
                                                        saveData(picUrl);
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
    private void createMedical(String userName, String yaofangType, String yaofangNum,
                               String yaoNum, String yaofangPrice, String acsm, String acid, List<ChineseDetailModel> list) {
        EMMessage message = EMMessage.createTxtSendMessage("yaofang", helperId);
        message.setAttribute("type", "yaofang");
        message.setAttribute("userName", userName);
        message.setAttribute("yaofangType", yaofangType);
        message.setAttribute("aiid", yaofangNum);
        // 数量（中药付数，西药天数）
        message.setAttribute("yaoNum", yaoNum);
        message.setAttribute("yaofangPrice", yaofangPrice);
        message.setAttribute("name", patName);
        message.setAttribute("acsm", acsm);
        // 患者性别
        message.setAttribute("pat_sex_name", patSexName);
        // 患者id
        message.setAttribute("pat_id", patId);
        // 药方号
        message.setAttribute("acid", acid);
        try {
            JSONArray jsonArray = getJson(list);
            message.setAttribute("med_json", jsonArray);
        } catch (Exception e) {

        }
        EMClient.getInstance().chatManager().sendMessage(message);
        onPageChanged.callBack();
    }

    public JSONArray getJson(List<ChineseDetailModel> list) {
        JSONArray jsonArray = new JSONArray();
        if (null != list && 0 < list.size()) {
            for (ChineseDetailModel model : list) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject
                            // 药品名
                            .put("cmc", model.getCmc())
                            // 药品数量（中药"g"，西药"天"）
                            .put("sl", model.getSl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 上传服务器成功后存数据库
     */
    private void saveData(String picUrl) {
        try {
            if (null != medTopList && 0 < medTopList.size()) {
                for (int i = 0; i < medTopList.size(); i++) {
                    if (TextUtils.isEmpty(medTopList.get(i).getTime())) {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                        String date = sDateFormat.format(new java.util.Date());
                        medTopList.get(i).setTime(date + String.valueOf(i));
                    }
                }
            }
            // 删除的药方
            if (null != deleteList && 0 < deleteList.size()) {
                try {
                    HisDbManager.getManager().deleteChineseDetailList(deleteList);
                } catch (DbException e) {

                }
            }
            HisDbManager.getManager().saveChineseDetailList(medTopList);
            saveSignUrl(picUrl);
        } catch (DbException e) {

        }
    }

    /**
     * 签名存储数据库
     */
    private void saveSignUrl(String picUrl) {
        if (null != chineseModel) {
            // 数据库有数据，更新表
            try {
                chineseModel.setDocQm(picUrl);
                chineseModel.setJe(je);
                chineseModel.setAcMxs(acmxs);
                chineseModel.setAcSm(acsm);
                HisDbManager.getManager().upDateChinese(chineseModel, "DOCQM", "JE", "ACMXS", "ACSM");
            } catch (DbException e) {

            }
        } else {
            // 数据库没有数据，创建表
            try {
                ChineseModel chineseModel = new ChineseModel();
                SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String date = sDateFormat.format(new java.util.Date());
                chineseModel.setTime(date);
                chineseModel.setAccId(helperId);
                chineseModel.setDocQm(picUrl);
                chineseModel.setJe(je);
                chineseModel.setAcMxs(acmxs);
                chineseModel.setAiId(aiid);
                chineseModel.setAcSm(acsm);
                HisDbManager.getManager().saveAskChinese(chineseModel);
            } catch (DbException e) {

            }
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
        showSearchWaiting();
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
                        hideSearchWaiting();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideSearchWaiting();
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
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

    /**
     * 金额和数量显示
     */
    private void amountShow() {
        double moneyMedCount = 0.0;
        if (null != medTopList && 0 < medTopList.size()) {
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
            moneyMedCount = 0.0;
        }
        int acmxsInt = 0;
        if (!TextUtils.isEmpty(acmxs)) {
            acmxsInt = Integer.parseInt(acmxs);
        } else {
            acmxsInt = 0;
        }
        moneyMedCount = moneyMedCount * acmxsInt;
        double value = convert(moneyMedCount);
        tvMedMoney.setText(String.valueOf(value) + getResources().getString(R.string.yuan));
        //金额
        je = String.valueOf(moneyMedCount);
    }

    static double convert(double value) {
        long l1 = Math.round(value * 100);
        double ret = l1 / 100.00;
        return ret;
    }

    private Map<String, String> map = new HashMap<>();

    private void searchData(final String pinyin) {
        //根据拼音查询数据
        showSearchWaiting();
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
                        hideSearchWaiting();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideSearchWaiting();
                        Toast.makeText(getActivity(), getResources().getString(R.string.data_error), Toast.LENGTH_LONG).show();
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

    private void initKeyBoard() {
        etSearch.setInputType(InputType.TYPE_NULL);
        kb.init(getActivity());
        kb.setOnKeyboardActionListener(new HisKeyboardView.OnKeyboardActionListener() {
            @Override
            public void onKey(String str) {
                Editable editable = etSearch.getText();
                int start = etSearch.getSelectionStart();

                editable.insert(start, str);
            }

            @Override
            public void onDelete() {
                Editable editable = etSearch.getText();
                int start = etSearch.getSelectionStart();
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            }
        });
    }

    public void setOnPageChanged(OnPageChanged onPageChanged) {
        this.onPageChanged = onPageChanged;
    }

    protected ProgressBar progressBar;

    private void showSearchWaiting() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.gravity = Gravity.CENTER;
                if (null == progressBar) {
                    progressBar = new ProgressBar(getActivity());
                    progressBar.setLayoutParams(lp);
                } else {
                    llSearch.removeView(progressBar);
                }
                llSearch.addView(progressBar);
            }
        });
    }

    private void hideSearchWaiting() {
        llSearch.removeView(progressBar);
    }
}
