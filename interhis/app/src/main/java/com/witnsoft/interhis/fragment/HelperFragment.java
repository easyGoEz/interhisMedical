package com.witnsoft.interhis.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.witnsoft.interhis.Chufang.ChuFangChinese;
import com.witnsoft.interhis.R;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.witnsoft.interhis.adapter.Chinese_Fixed_Adapter;
import com.witnsoft.interhis.adapter.Chinese_ListView_Adapter;
import com.witnsoft.interhis.adapter.Chinese_RecycleView_Adapter;
import com.witnsoft.interhis.adapter.Western_ListView_Adapter;
import com.witnsoft.interhis.adapter.Western_RecycleView_Adapter;
import com.witnsoft.interhis.db.DataHelper;
import com.witnsoft.interhis.db.HisDbManager;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.db.model.ChineseModel;
import com.witnsoft.interhis.db.model.WesternDetailModel;
import com.witnsoft.interhis.inter.DialogListener;
import com.witnsoft.interhis.inter.FilterListener;
import com.witnsoft.interhis.inter.OnClick;
import com.witnsoft.interhis.inter.OnFixClick;
import com.witnsoft.interhis.mainpage.ACMXSDialog;
import com.witnsoft.interhis.mainpage.MedicalDetailsActivity;
import com.witnsoft.interhis.mainpage.WritePadDialog;
import com.witnsoft.interhis.mainpage.DialogActivity;
import com.witnsoft.interhis.mainpage.SecondDialogActivity;
import com.witnsoft.interhis.setting.ChildBaseFragment;
import com.witnsoft.interhis.tool.KeyboardUtil;
import com.witnsoft.libnet.model.DataModel;
import com.witnsoft.libnet.model.OTRequest;
import com.witnsoft.libnet.net.CallBack;
import com.witnsoft.libnet.net.NetTool;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by ${liyan} on 2017/5/8.
 */
@ContentView(R.layout.fragment_helper)
public class HelperFragment extends ChildBaseFragment implements View.OnClickListener, OnClick, OnFixClick {
    View rootView;
    private static final String TAG = "HelperFragment";
    //手写签名
    private Bitmap mSignBitmap;
    private String signPath;
    private ChuFangChinese chufang;
    private WritePadDialog writePadDialog;
    private OTRequest otRequest;
    public static final String TMP_PATH = "/DCIM/Camera/";
    private static final String TN_DOC_KAIYAO = "F27.APP.01.06";

    //RadioButton
    @ViewInject(R.id.fragment_helper_radioButton_ask)
    private RadioButton ask;

    @ViewInject(R.id.fragment_helper_radioButton_chat)
    private RadioButton chat;

    @ViewInject(R.id.fragment_helper_radioButton_chinese)
    private RadioButton chinese;

    @ViewInject(R.id.fragment_helper_radioButton_western)
    private RadioButton western;

    @ViewInject(R.id.fragment_helper_ask_linearLayout)
    private FrameLayout ask_linearLayout;
    //所对应布局
    @ViewInject(R.id.fragment_helper_chinese_linearLayout)
    private LinearLayout chinese_linearLayout;

    @ViewInject(R.id.fragment_helper_western_medical_linearLayout)
    private LinearLayout western_linearLayout;

    @ViewInject(R.id.fragment_helper_diagnosis_linearLayout)
    private LinearLayout chat_linearLayout;

    @ViewInject(R.id.fragment_helper_chinese_linearLayout_linearLayout)
    private LinearLayout chinese_linearLayout_linearLayout;

    @ViewInject(R.id.fragment_helper_western_linearLayout_linearLayout)
    private LinearLayout western_linearLayout_linearLayout;

    @ViewInject(R.id.fragment_helper_chinese_medical_allNumber)
    private LinearLayout chinese_medical_allNumber_linearLayout;

    @ViewInject(R.id.fragment_helper_chinese_linearLayout_linearLayout_image)
    private ImageView chinese_img;

    @ViewInject(R.id.fragment_helper_western_linearLayout_linearLayout_image)
    private ImageView western_img;

    @ViewInject(R.id.fragment_helper_chinese_chahao)
    private ImageView chahao;

    private ImageView western_chahao;

    //中西药显示部分
    @ViewInject(R.id.fragment_helper_chinese_linearLayout_recycleView)
    private RecyclerView chinese_recyclerView;

    @ViewInject(R.id.fragment_helper_western_linearLayout_recycleView)
    private RecyclerView western_recycleView;
    private Chinese_RecycleView_Adapter chinese_adapter;
    private Western_RecycleView_Adapter western_adapter;
    private List<ChineseDetailModel> data;
    private List<ChineseModel> chineseModelList;
    private List<WesternDetailModel> western_data;

    //中西药按钮
    @ViewInject(R.id.fragment_helper_chinese_button)
    private Button chinese_button;
    @ViewInject(R.id.fragment_helper_western_button)
    private Button western_button;

    //中西药搜索框
    @ViewInject(R.id.fragment_helper_chinese_edittext)
    private EditText chinese_edittext;

    @ViewInject(R.id.fragment_helper_western_edittext)
    private EditText  western_edittext;

    //中药嘱咐edittext
    @ViewInject(R.id.fragment_helper_chinese_advice)
    private EditText chinese_advice;
    private Chinese_ListView_Adapter adapter = null;
    private Western_ListView_Adapter western_listView_adapter = null;
    @ViewInject(R.id.fragment_helper_chinese_listview)
    private ListView chinese_listView;

    @ViewInject(R.id.fragment_helper_western_listview)
    private ListView  western_listView;

    private List<ChineseDetailModel> list = new ArrayList<>();
    private List<WesternDetailModel> western_list = new ArrayList<>();

    //中药数量
    @ViewInject(R.id.fragment_helper_chinese_medical_number)
    private TextView chinese_medical_number;

    private ChineseModel chineseModel;
    private ChineseDetailModel chineseDetailModel;
    //固定药方显示部分
    @ViewInject(R.id.fragment_helper_chinese_fixed_recycleview)
    private RecyclerView chinese_fixed;

    @ViewInject(R.id.fragment_helper_western_fixed_recycleview)
    private RecyclerView western_fixed;
    private Chinese_Fixed_Adapter fixed_adapter;
    private List<ChineseDetailModel> fix_data;

    //医生诊断
    @ViewInject(R.id.fragment_helper_diagnosis_editText)
    private EditText diagnosis_edittext;

    private String userName;
    private String type1;
    private int single1;

    private String helperId, aiid;
    private String pinyin, price, amount, medical_id,date;
    // TODO: 2017/6/22
    private EaseChatFragment chatFragment;
    private Bundle bundle;
    private Context ctx;
    private Activity act;
    //动态广播
    private Receiver receiver;
    private Refresh refresh;
    private KeyboarrReceiver keyboarrReceiver;
    private ReStartReceiver reStartReceiver;
    private MedicalDetails medicalDetails;

    private String chinese_number, advice, diagnosis, count,acid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = x.view().inject(this, inflater, container);
        }

        ctx = getActivity().getBaseContext();
        act = getActivity();

        chineseDetailModel = new ChineseDetailModel();

        return rootView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ask.setOnClickListener(this);
        chat.setOnClickListener(this);
        chinese.setOnClickListener(this);
        western.setOnClickListener(this);
        chinese_button.setOnClickListener(this);
        chinese_advice.setOnClickListener(this);
        chahao.setOnClickListener(this);
        chinese_medical_number.setOnClickListener(this);
        chinese_medical_allNumber_linearLayout.setOnClickListener(this);

        //显示药方的地方
        chinese_adapter = new Chinese_RecycleView_Adapter(getActivity().getBaseContext());
        data = new ArrayList<>();
        chinese_adapter.setList(data);
        chinese_adapter.setOnClick(this);
        chinese_recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(), 4));
        chinese_recyclerView.setAdapter(chinese_adapter);

        western_adapter = new Western_RecycleView_Adapter(getActivity().getBaseContext());
        western_data = new ArrayList<>();
        western_adapter.setList(western_data);
        western_adapter.setOnClick(this);
        western_recycleView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        western_recycleView.setAdapter(western_adapter);


        //固定药方
        fix_data = new ArrayList<>();
        initData();

        fixed_adapter = new Chinese_Fixed_Adapter(getActivity().getBaseContext());
        chinese_fixed.setLayoutManager(new GridLayoutManager(getActivity().getBaseContext(), 3));
        fixed_adapter.setList(fix_data);
        fixed_adapter.setOnFixClick(this);
        chinese_fixed.setAdapter(fixed_adapter);

        //点击edittext实现自定义软键盘
        chinese_edittext.setInputType(InputType.TYPE_NULL);
        new KeyboardUtil(act, ctx, chinese_edittext).showKeyboard();

        western_edittext.setInputType(InputType.TYPE_NULL);
        new KeyboardUtil(act, ctx, western_edittext).showKeyboard();


        //实现搜索功能
        setData();//给listview设置adapter
        setListener();//给listview设置监听

        // TODO: 2017/6/23 崩溃！！！ 
//        EventBus.getDefault().register(this);
        //动态广播
        receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter("shanchu");
        getActivity().registerReceiver(receiver, intentFilter);

        refresh = new Refresh();
        IntentFilter intentRefresh = new IntentFilter("SHUAXIN");
        getActivity().registerReceiver(refresh, intentRefresh);

        keyboarrReceiver = new KeyboarrReceiver();
        IntentFilter intentKey = new IntentFilter("RUANJIANPAN");
        getActivity().registerReceiver(keyboarrReceiver, intentKey);

        reStartReceiver = new ReStartReceiver();
        IntentFilter intentReStart = new IntentFilter("CHUSHIHUA");
        getActivity().registerReceiver(reStartReceiver, intentReStart);
        initChat();

        medicalDetails = new MedicalDetails();
        IntentFilter intentFilterDetails = new IntentFilter("MingXi");
        getActivity().registerReceiver(medicalDetails, intentFilterDetails);



    }

    private void setListener() {
        //没有进行搜索的时候，也要添加对listview的item单击监听
        setItemClick(list);

    }

    private void setData() {
        initData();//初始化搜索数据

        //这里面创建adapter的时候，构造方法参数传了一个接口对象，回调借口中的方法来实现对过滤的数据的获取
        adapter = new Chinese_ListView_Adapter(list, getActivity().getBaseContext(), new FilterListener() {
            @Override
            public void getFilterData(List<String> datas) {
                setItemClick(list);
            }
        });
        chinese_listView.setAdapter(adapter);
    }

    //搜索行点击时间
    private void setItemClick(final List<ChineseDetailModel> datas) {
        chinese_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(MainActivity.this, filter_lists.get(position), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), DialogActivity.class);
                intent.putExtra("medical_name", list.get(position).getCmc());
                intent.putExtra("accid", helperId);
                intent.putExtra("dj", price);
                intent.putExtra("cdm", medical_id);
                Log.e(TAG, "onItemClick: " + medical_id + list.get(position).getCmc());
                startActivity(intent);

            }
        });
    }
    //固定位置显示药材名字
    private void initData() {
        ChineseDetailModel a = new ChineseDetailModel();
        ChineseDetailModel b = new ChineseDetailModel();
        ChineseDetailModel c = new ChineseDetailModel();
        ChineseDetailModel d = new ChineseDetailModel();
        ChineseDetailModel e = new ChineseDetailModel();
        ChineseDetailModel f = new ChineseDetailModel();
        ChineseDetailModel g = new ChineseDetailModel();
        ChineseDetailModel h = new ChineseDetailModel();
        ChineseDetailModel i = new ChineseDetailModel();
        ChineseDetailModel j = new ChineseDetailModel();
        ChineseDetailModel k = new ChineseDetailModel();
        ChineseDetailModel l = new ChineseDetailModel();
        ChineseDetailModel m = new ChineseDetailModel();
        ChineseDetailModel n = new ChineseDetailModel();
        ChineseDetailModel o = new ChineseDetailModel();
        ChineseDetailModel p = new ChineseDetailModel();
        ChineseDetailModel q = new ChineseDetailModel();
        ChineseDetailModel r = new ChineseDetailModel();

        a.setCmc("车前子");
        b.setCmc("人参");
        c.setCmc("卜芥");
        d.setCmc("儿茶");
        e.setCmc("八角");
        f.setCmc("丁香");
        g.setCmc("刀豆");
        h.setCmc("三七");
        i.setCmc("三棱");
        j.setCmc("当归");
        k.setCmc("鹿茸");
        l.setCmc("大黄");
        m.setCmc("山药");
        n.setCmc("川乌");
        o.setCmc("天麻");
        p.setCmc("马宝");
        q.setCmc("马勃");
        r.setCmc("天冬");

        fix_data.add(a);
        fix_data.add(b);
        fix_data.add(c);
        fix_data.add(d);
        fix_data.add(e);
        fix_data.add(f);
        fix_data.add(g);
        fix_data.add(h);
        fix_data.add(i);
        fix_data.add(j);
        fix_data.add(k);
        fix_data.add(l);
        fix_data.add(m);
        fix_data.add(n);
        fix_data.add(o);
        fix_data.add(p);
        fix_data.add(q);
        fix_data.add(r);

    }

    //radiobutton点击事件
    @Override
    public void onClick(View v) {
        chineseModel = new ChineseModel();
        chinese_medical_number.setText("0");
        String number = chinese_medical_number.getText().toString();
        switch (v.getId()) {
            //聊天界面
            case R.id.fragment_helper_radioButton_ask:
                playAskVeiw();
                chatFragment = new EaseChatFragment();
                bundle = new Bundle();
                bundle.putString("userName", userName);
                bundle.putString("userId", helperId);
                bundle.putString("type", type1);
                bundle.putInt("single", single1);
                bundle.putString("img_doc", this.imgDoc);
                bundle.putString("img_pat", this.imgPat);
                chatFragment.setArguments(bundle);
                getChildFragmentManager().beginTransaction().add(R.id.fragment_helper_ask_linearLayout, chatFragment).commit();

                break;
            //诊断界面
            case R.id.fragment_helper_radioButton_chat:
                playChatView();
                break;

            case R.id.fragment_helper_diagnosis_button:
                diagnosis = diagnosis_edittext.getText().toString();
                chineseModel.setAcId(helperId);
                chineseModel.setZdsm(diagnosis);
                try {

                    HisDbManager.getManager().saveAskChinese(chineseModel);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Toast.makeText(ctx, "已保存", Toast.LENGTH_SHORT).show();
                break;
            //中药界面
            case R.id.fragment_helper_radioButton_chinese:

                playChineseView();

                try {
                    chineseModelList = HisDbManager.getManager().findChineseMode(helperId);
                } catch (DbException e) {
                    e.printStackTrace();
                }

                    Log.e(TAG, "onClick: " + chineseDetailModel.isUploadSever());
                    playChineseView();
                    if (chineseDetailModel.isUploadSever() == false) {
                        //查询数据库
                        try {
                            data = HisDbManager.getManager().findChineseDeatilModel(helperId);
                        } catch (DbException e1) {
                            e1.printStackTrace();
                        }
                        chinese_adapter.setList(data);
                        chinese_adapter.notifyDataSetChanged();
                        //往json串传值
                        chufang = new ChuFangChinese();
                        try {
                            chufang.setList(data);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        chinese_adapter.ReSrart();
                        chinese_adapter.notifyDataSetChanged();

                    }

                    for (int i = 0; i < chineseModelList.size(); i++) {
                        if (chineseModelList.get(i).isUploadSever() == false || chineseModelList == null) {
                            try {
                                data = HisDbManager.getManager().findChineseDeatilModel(helperId);
                            } catch (DbException e1) {
                                e1.printStackTrace();
                            }
                            chinese_adapter.setList(data);
                            chinese_adapter.notifyDataSetChanged();
                            //往json串传值
                            chufang = new ChuFangChinese();
                            try {
                                chufang.setList(data);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        } else {
                            chinese_adapter.ReSrart();
                            chinese_adapter.notifyDataSetChanged();
                        }
                    }

                    break;
                    //西药界面

                    case R.id.fragment_helper_radioButton_western:
                        playWesternView();
                        break;
                    //中药医嘱
                    case R.id.fragment_helper_chinese_advice:

                        break;
                    //中药edittext当中叉号
                    case R.id.fragment_helper_chinese_chahao:
                        chinese_edittext.setText(null);
                        chinese_listView.setVisibility(View.GONE);
                        chinese_fixed.setVisibility(View.VISIBLE);
                        break;

                    //中药保存按钮
                    case R.id.fragment_helper_chinese_button:
                        //将中药界面的医嘱存入数据库当中
                        advice = chinese_advice.getText().toString();
                        chineseModel.setAcId(helperId);
                        chineseModel.setAcSm(advice);
                        chineseModel.setAcMxs(chinese_number);
                        chineseModel.setChineseDetailModel(data);
                        try {
                            HisDbManager.getManager().saveAskChinese(chineseModel);
                        } catch (DbException el) {
                            el.printStackTrace();
                        }

                        chufang = new ChuFangChinese();
                        try {
                            chufang.setAcsm(advice);
                        } catch (Exception el) {
                            el.printStackTrace();
                        }

                        if (advice.equals("")) {
                            Toast.makeText(ctx, "请输入用法用量", Toast.LENGTH_SHORT).show();
                        } else {
                            chinese_button.setOnClickListener(signListener);
                        }

                        upLoadMessage();

                        break;
                    //点击几付药
                    case R.id.fragment_helper_chinese_medical_allNumber:
                        Intent intent = new Intent(getActivity(), ACMXSDialog.class);
                        intent.putExtra("accid", helperId);
                        startActivity(intent);


        }
    }
    //西药弹出dialog
//    private View.OnClickListener signListenerWestern = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            writePadDialog = new WritePadDialog(signPath,helperId,data, chinese_number, advice, diagnosis, aiid, getActivity(),
//                    getActivity().getBaseContext(), R.style.SignBoardDialog, new DialogListener() {
//                public void refreshActivity(Object object) {
//                    mSignBitmap = (Bitmap) object;
//                    signPath = createFile();
//                    //对图片进行压缩
//                            /*BitmapFactory.Options options = new BitmapFactory.Options();
//                            options.inSampleSize = 15;
//							options.inTempStorage = new byte[5 * 1024];
//							Bitmap zoombm = BitmapFactory.decodeFile(signPath, options);
//*/
//                    Bitmap zoombm = getCompressBitmap(signPath);
//                    western_img.setImageBitmap(zoombm);
//                }
//            });
//            writePadDialog.show();
//        }
//    };
    //中药弹出dialog
    private View.OnClickListener signListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            writePadDialog = new WritePadDialog(acid,data, chinese_number, advice, diagnosis, aiid,
                    getActivity(),getActivity(), R.style.SignBoardDialog, new DialogListener() {
                public void refreshActivity(Object object) {
                    mSignBitmap = (Bitmap) object;
                    signPath = createFile();
                    //对图片进行压缩
                            /*BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 15;
							options.inTempStorage = new byte[5 * 1024];
							Bitmap zoombm = BitmapFactory.decodeFile(signPath, options);
*/
                    writePadDialog.setPath(signPath);
                    Bitmap zoombm = getCompressBitmap(signPath);
                    chinese_img.setImageBitmap(zoombm);

                }
            });
            writePadDialog.show();
        }
    };

    /**
     * 创建手写签名文件
     *
     * @return
     */
    private String createFile() {
        ByteArrayOutputStream baos = null;
        String _path = null;
        try {
            //创建目录
            String sign_dir = Environment.getExternalStorageDirectory().toString()+TMP_PATH
                    + File.separator;
            File localFile=new File(sign_dir);
            if (!localFile.exists()){
                localFile.mkdir();
            }
            //拼接好文件路径和名称
            File finalImageFile=new File(localFile,System.currentTimeMillis()+"img.png");
            if (finalImageFile.exists()){
                finalImageFile.delete();
            }
            try {
                finalImageFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            
            //文件的读取
            FileOutputStream fileOutputStream=null;
            try {
                fileOutputStream=new FileOutputStream(finalImageFile);
            }catch (FileNotFoundException e){
                e.printStackTrace();
            }
            if (mSignBitmap==null){
                Toast.makeText(ctx, "图片不存在", Toast.LENGTH_SHORT).show();
            }

            _path = sign_dir + System.currentTimeMillis() + "img.png";
            baos = new ByteArrayOutputStream();
            mSignBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            try {
                fileOutputStream.flush();
                fileOutputStream.close();
                Log.e(TAG, "createFile: "+finalImageFile.getAbsolutePath() );
            }catch (IOException e){
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

    /**
     * 根据图片路径获取图片的压缩图
     *
     * @param filePath
     * @return
     */
    public Bitmap getCompressBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options); //此时返回bm为空
        if (bitmap == null) {
        }
        //计算缩放比
        int simpleSize = (int) (options.outHeight / (float) 200);
        if (simpleSize <= 0)
            simpleSize = 1;
        options.inSampleSize = simpleSize;
        options.inJustDecodeBounds = false;
        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
        bitmap = BitmapFactory.decodeFile(filePath, options);
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        System.out.println(w + "   " + h);
        return bitmap;
    }

    //显示聊天界面
    public void playAskVeiw() {
        ask_linearLayout.setVisibility(View.VISIBLE);
        chat_linearLayout.setVisibility(View.GONE);
        chinese_linearLayout.setVisibility(View.GONE);
        western_linearLayout.setVisibility(View.GONE);
    }
    //显示
    public void playChatView() {
        chat_linearLayout.setVisibility(View.VISIBLE);
        ask_linearLayout.setVisibility(View.GONE);
        chinese_linearLayout.setVisibility(View.GONE);
        western_linearLayout.setVisibility(View.GONE);
    }

    public void playChineseView() {
        chinese_linearLayout.setVisibility(View.VISIBLE);
        ask_linearLayout.setVisibility(View.GONE);
        chat_linearLayout.setVisibility(View.GONE);
        western_linearLayout.setVisibility(View.GONE);
    }

    public void playWesternView() {
        western_linearLayout.setVisibility(View.VISIBLE);
        ask_linearLayout.setVisibility(View.GONE);
        chat_linearLayout.setVisibility(View.GONE);
        chinese_linearLayout.setVisibility(View.GONE);
    }

//    public void setContent(String Aiid,String userName, String userId, String type, int single) {
//        llContent.setVisibility(View.VISIBLE);
//
//        tvNoData.setVisibility(View.GONE);
//        aiid=Aiid;
//        helperId = userId;
//        this.userName = userName;
//        type1 = type;
//        single1 = single;
//
//        chatFragment = new EaseChatFragment();
//        bundle = new Bundle();
//        bundle.putString("aiid",Aiid);
//        bundle.putString("userName", userName);
//        bundle.putString("userId", userId);
//        bundle.putString("type", type);
//        bundle.putInt("single", single);
//        chatFragment.setArguments(bundle);
//        getChildFragmentManager().beginTransaction().add(R.id.fragment_helper_ask_linearLayout, chatFragment).commit();
//
//    }

    private String imgDoc = "";
    private String imgPat = "";

    private void initChat() {
        playAskVeiw();
        this.chatFragment = new EaseChatFragment();
        this.bundle = new Bundle();
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

        bundle.putString("aiid", this.aiid);
        bundle.putString("userName", this.userName);
        bundle.putString("userId", this.helperId);
        bundle.putString("type", this.type1);
        bundle.putInt("single", this.single1);
        bundle.putString("img_doc", this.imgDoc);
        bundle.putString("img_pat", this.imgPat);
        chatFragment.setArguments(bundle);
        getChildFragmentManager().beginTransaction().add(R.id.fragment_helper_ask_linearLayout, chatFragment).commit();
    }


    public void createYaoFang(String userName, String yaofangType, String yaofangNum, String yaoNum, String yaofangPrice) {
        EMMessage message = EMMessage.createTxtSendMessage("yaofang", helperId);

        message.setAttribute("type", "yaofang");
        message.setAttribute("userName", userName);
        message.setAttribute("yaofangType", yaofangType);
        message.setAttribute("yaofangNum", yaofangNum);
        message.setAttribute("yaoNum", yaoNum);
        message.setAttribute("yaofangPrice", yaofangPrice);

        EMClient.getInstance().chatManager().sendMessage(message);

    }


    //显示药名地方点击事件
    @Override
    public void onIteClick(int position) {
        Intent intent = new Intent(getActivity(), SecondDialogActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("chinese_name", data.get(position).getCmc());
        intent.putExtra("accid", helperId);
        intent.putExtra("sl", count);
        Log.e(TAG, "onIteClick:############ " + data.get(position).getCmc() + helperId + count);
        startActivity(intent);
        chinese_adapter.deleteTextView(position);
//        western_adapter.deleteTextView(position);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getData(ChineseDetailModel numberBean) {
        String chinese_name = numberBean.getCmc();
        count = numberBean.getSl();
        ChineseDetailModel a = new ChineseDetailModel();
        a.setSl(count);
        a.setCmc(chinese_name);
        chinese_adapter.addTextView(a);
        chinese_adapter.notifyDataSetChanged();

        chinese_edittext.setText(null);
        chinese_listView.setVisibility(View.GONE);
        chinese_fixed.setVisibility(View.VISIBLE);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getNumber(ChineseModel chineseModel) {
        chinese_number = chineseModel.getAcMxs();
        chinese_medical_number.setText(chinese_number);
        //将付数传回
        ChuFangChinese chuFangBase = new ChuFangChinese();
        try {
            chuFangBase.setAcmxs(chinese_number);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(receiver);
        getActivity().unregisterReceiver(refresh);
    }

    @Override
    public void OnFixItemClick(int position) {

        if (data.size() == 0) {
            Intent intent = new Intent(getActivity(), DialogActivity.class);
            intent.putExtra("medical_name", fix_data.get(position).getCmc());
            intent.putExtra("accid", helperId);
            intent.putExtra("dj", price);
            intent.putExtra("cdm", medical_id);
            startActivity(intent);
        }

        boolean isSame = false;
        for (int i = 0; i < data.size(); i++) {
            if (fix_data.get(position).getCmc().equals(data.get(i).getCmc())) {
                Toast.makeText(ctx, "该药已开过", Toast.LENGTH_SHORT).show();
                isSame = true;
                break;
            }
        }

        if (isSame) {
            Toast.makeText(ctx, "该药已开过", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(getActivity(), DialogActivity.class);
            intent.putExtra("medical_name", fix_data.get(position).getCmc());
            intent.putExtra("accid", helperId);
            intent.putExtra("dj", price);
            intent.putExtra("cdm", medical_id);
            startActivity(intent);
        }
    }

    public void upLoadMessage(){
        //生成json串 并上传服务器
        final ChuFangChinese chufang=new ChuFangChinese();
        chufang.fromJSON(list,chinese_number,advice,diagnosis,aiid);
        otRequest=new OTRequest(getActivity().getBaseContext());
        otRequest.setTN(TN_DOC_KAIYAO);
        final DataModel data = new DataModel();
        data.setDataJSONStr(String.valueOf(chufang.fromJSON(list,chinese_number,advice,diagnosis,aiid)));
        otRequest.setDATA(data);
        NetTool.getInstance().startRequest(false, true , act , null, otRequest, new CallBack<Map, String>() {
            @Override
            public void onSuccess(Map map, String s) {

                JSONObject json=new JSONObject(map);

                try {
                    acid=json.getJSONObject("DATA").getString("acid");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e(TAG, "onSuccess: "+chufang.fromJSON(list,chinese_number,advice,diagnosis,aiid));
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "onError!!!!!!!!!!!!!: "+"请求失败" );
            }
        });
    }


    class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String name = intent.getStringExtra("name");
            chinese_adapter.notifyDataSetChanged();
            try {
                HisDbManager.getManager().deleteAskChinese(name);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    class Refresh extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            playAskVeiw();
            ask.setChecked(true);
        }
    }

    class KeyboarrReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //根据拼音查询数据
            pinyin = chinese_edittext.getText().toString();
            String xmmc = null;
            List<String> asd = new ArrayList<>();
            Cursor cursor = DataHelper.getInstance(getContext()).getXMRJ(pinyin);
            if (pinyin.length() >= 2) {
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        xmmc = cursor.getString(cursor.getColumnIndex("xmmc"));
                        price = cursor.getString(cursor.getColumnIndex("bzjg"));
                        medical_id = cursor.getString(cursor.getColumnIndex("sfxmbm"));
                        asd.add(xmmc);
                    } while (cursor.moveToNext());
                }
                cursor.close();
                //显示搜索列表药名
                if (adapter != null) {
                    adapter.delete();
                }
                for (String s : asd) {
                    ChineseDetailModel chinese = new ChineseDetailModel();
                    chinese.setCmc(s);
                    list.add(chinese);
                }
                adapter.notifyDataSetChanged();
                chinese_listView.setVisibility(View.VISIBLE);
                chinese_fixed.setVisibility(View.GONE);
            }
        }
    }

    class ReStartReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            playAskVeiw();
            chinese_advice.setText(null);
            diagnosis_edittext.setText(null);
            ask.setChecked(true);

            chinese_medical_number.setText("0");
            chinese_adapter.ReSrart();
            chinese_adapter.notifyDataSetChanged();
            //将时间存入数据库中
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            date = sDateFormat.format(new java.util.Date());
            chineseModel.setTime(date);
            chineseModel.setAcId(helperId);


            //更改数据库状态
            try {
                chineseModel = HisDbManager.getManager().findIsUpLoad(helperId, false);
                chineseModel.setUploadSever(true);

            } catch (DbException e) {
                e.printStackTrace();
            }

            try {
                HisDbManager.getManager().upDateIsUpLoad(chineseModel);
            } catch (DbException e) {
                e.printStackTrace();
            }

            try {
                HisDbManager.getManager().deleteAskNumbwe(helperId);
            } catch (DbException e) {
                e.printStackTrace();
            }

            createYaoFang(helperId, "中药", aiid, chinese_number, "1300");
            initChat();

        }
    }

    class MedicalDetails extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context, "查看详情", Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "onReceive: "+1111111111 );
            Intent intentDetails=new Intent(getActivity(),MedicalDetailsActivity.class);
            intentDetails.putExtra("acid", helperId);
            intentDetails.putExtra("aiid", aiid);
            intentDetails.putExtra("advice", advice);
            intentDetails.putExtra("number", chinese_number);
            intentDetails.putExtra("time", date);
            Bundle bundle=new Bundle();
            bundle.putSerializable("list", (Serializable) data);
            intentDetails.putExtras(bundle);
            Log.e(TAG, "onReceive: "+helperId+aiid+chinese_number+date+advice+data);
            startActivity(intentDetails);
        }
    }
}
