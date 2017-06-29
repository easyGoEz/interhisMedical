package com.witnsoft.interhis.mainpage;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Environment;

import android.os.Handler;
import android.os.Looper;
import android.service.carrier.CarrierMessagingService;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.model.ChineseDetailModel;
import com.witnsoft.interhis.inter.DialogListener;
import com.witnsoft.libnet.model.OTRequest;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import static java.lang.String.valueOf;
import static rx.plugins.RxJavaHooks.onError;


public class WritePadDialog extends Dialog {
	private static final String TAG = "WritePadDialog";
	private Activity act;
	Context context;
    static LayoutParams p;
	private OTRequest otRequest;
	private DialogListener dialogListener;
	List<ChineseDetailModel> list=new ArrayList<>();
	String str,acsm,zdsm,aiid,acid,signPath;

    private Handler handler = new Handler(Looper.getMainLooper());
    private OkHttpClient okHttpClient;
    private Gson gson=new Gson();

	public void setPath(String path) {
		this.signPath = path;
	}

	public WritePadDialog(String acid,List<ChineseDetailModel> list, String str, String acsm, String zdsm, String aiid, Activity a, Context context, int themeResId, DialogListener dialogListener) {
		super(context,themeResId);
		this.acid=acid;
		this.aiid=aiid;
		this.zdsm=zdsm;
        this.acsm=acsm;
		this.str=str;
		this.list=list;
		this.act=a;
		this.context = context;
		this.dialogListener = dialogListener;
        Log.e(TAG, "WritePadDialog: "+acid );
    }

	static final int BACKGROUND_COLOR = Color.WHITE;
	static final int BRUSH_COLOR = Color.BLACK;
	PaintView mView;
	/** The index of the current color to use. */
	int mColorIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.write);
		WindowManager wm = (WindowManager) getContext()
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.x=190;
		p.y=130;
		p.height = 500; //高度设置为屏幕的0.4
		p.width = 880; //宽度设置为屏幕的0.6
		p.alpha=0.8f;
		int statusBarHeight = getStatusBarHeight(getContext());
//		p.height = (int)(height - statusBarHeight + 0.5);   //减去系统的宽高
//		p.width = (int)(width + 0.5);
		getWindow().setAttributes(p); // 设置生效,全屏填充
		mView = new PaintView(getContext());
		FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
		frameLayout.addView(mView);
		mView.requestFocus();
		Button btnClear = (Button) findViewById(R.id.tablet_clear);
		btnClear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mView.clear();
			}
		});
		Button btnOk = (Button) findViewById(R.id.tablet_ok);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					dialogListener.refreshActivity(mView.getCachebBitmap());
					WritePadDialog.this.dismiss();
				}catch (Exception e){
					e.printStackTrace();
				}

				Intent intent=new Intent("CHUSHIHUA");
				getContext().sendBroadcast(intent);

				Log.e(TAG, "onClick##############: "+acid );
				updateHeadImg(signPath);

			}
		});

		okHttpClient = (new OkHttpClient.Builder()).retryOnConnectionFailure(true).connectTimeout(5L, TimeUnit.SECONDS)
				.cache(new Cache(Environment.getExternalStorageDirectory(), 10485760L)).build();
	}

	/**
	 * 获取通知栏的高度
	 * @param context
	 * @return
	 */
	public int getStatusBarHeight(Context context){
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}



	/**
	 * This view implements the drawing canvas.
	 *
	 * It handles all of the input events and drawing functions.
	 */
    public static class PaintView extends View {
		private Paint paint;
		private Canvas cacheCanvas;
		private Bitmap cachebBitmap;
		private Path path;

		public Bitmap getCachebBitmap() {
			return cachebBitmap;
		}

		public PaintView(Context context) {
			super(context);
			init();
		}

		private void init() {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setStrokeWidth(10);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			path = new Path();
			cachebBitmap = Bitmap.createBitmap(p.width, (int) (p.height * 0.8),
					Config.ARGB_8888);
			cacheCanvas = new Canvas(cachebBitmap);
			//设置背景为白色，不然点击缩略图查看的时候是全黑色
			//cacheCanvas.drawColor(R.color.transparent);
		}

		public void clear() {
			if (cacheCanvas != null) {
				paint.setColor(BACKGROUND_COLOR);
				cacheCanvas.drawPaint(paint);
				paint.setColor(Color.BLACK);
				cacheCanvas.drawColor(Color.WHITE);
				invalidate();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			// canvas.drawColor(BRUSH_COLOR);
			canvas.drawBitmap(cachebBitmap, 0, 0, null);
			canvas.drawPath(path, paint);
		}

		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			int curW = cachebBitmap != null ? cachebBitmap.getWidth() : 0;
			int curH = cachebBitmap != null ? cachebBitmap.getHeight() : 0;
			if (curW >= w && curH >= h) {
				return;
			}
			if (curW < w)
				curW = w;
			if (curH < h)
				curH = h;
			Bitmap newBitmap = Bitmap.createBitmap(curW, curH,
					Config.ARGB_8888);
			Canvas newCanvas = new Canvas();
			newCanvas.setBitmap(newBitmap);
			if (cachebBitmap != null) {
				newCanvas.drawBitmap(cachebBitmap, 0, 0, null);
			}
			cachebBitmap = newBitmap;
			cacheCanvas = newCanvas;
		}

		private float cur_x, cur_y;

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: {
					cur_x = x;
					cur_y = y;
					path.moveTo(cur_x, cur_y);
					break;
				}
				case MotionEvent.ACTION_MOVE: {
					path.quadTo(cur_x, cur_y, x, y);
					cur_x = x;
					cur_y = y;
					break;
				}
				case MotionEvent.ACTION_UP: {
					cacheCanvas.drawPath(path, paint);
					path.reset();
					break;
				}
			}
			invalidate();
			return true;
		}
	}

	public void updateHeadImg(final String path) {
		final String url = "http://zy.renyibao.com/FileUploadServlet";
		File file = new File(path);
		RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
		MultipartBody body = new MultipartBody.Builder()
				.setType(MultipartBody.FORM)
				.addPart(Headers.of("Content-Disposition", "multipart/form-data; name=\"file\"; filename=\"img.png\""), fileBody)
//                .addFormDataPart("file", path, fileBody)
				.addFormDataPart("keyid",acid)
				.addFormDataPart("fjlb", "ask_chinese")
				.build();

		Request request = new Request.Builder()
				.url(url)
				.post(body)
				.build();

		WritePadDialog.this.okHttpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				WritePadDialog.this.handler.post(new Runnable() {
					public void run() {
						Toast.makeText(act, "图片上传失败", Toast.LENGTH_SHORT).show();
				}
				});
				Log.e(TAG, "onFailure: "+e.getMessage());
						}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				if (response.isSuccessful()) {
					final String resp = response.body().string();
					if (!TextUtils.isEmpty(resp)) {
                        HashMap mapObj = new HashMap();
                        final Map map = (Map) gson.fromJson(resp, mapObj.getClass());
                        String errCode = "";
                        if (null != map.get("errcode")) {
                            try {
                                errCode = valueOf(map.get("errcode"));
                                if (!TextUtils.isEmpty(errCode) && "200".equals(errCode)) {
                                    Toast.makeText(act,WritePadDialog.this.getContext().getString(R.string.update_success), Toast.LENGTH_LONG).show();
                                    Log.e(TAG, "onResponse: "+"上传成功" );

                                } else if (!TextUtils.isEmpty(errCode)) {
                                    if (!TextUtils.isEmpty(valueOf(map.get("errcode")))) {
                                        Toast.makeText(act, valueOf(map.get("errmsg")), Toast.LENGTH_LONG).show();

                                    }
                                }
                            } catch (ClassCastException var11) {
                                ;
                            }
                        }
                    }
				}
			}
		});
	}



}




