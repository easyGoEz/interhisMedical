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
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jakewharton.rxbinding.view.RxView;
import com.witnsoft.interhis.R;

import rx.functions.Action1;

import static android.graphics.Color.WHITE;


public class WritePadDialog extends Dialog {
    private static final String TAG = "WritePadDialog";
    private Context context;
    private static LayoutParams p;
    private CallBack callBack;


    public WritePadDialog(Fragment fragment, int themeResId) {
        super(fragment.getActivity(), themeResId);
        this.context = fragment.getActivity();
        callBack = (CallBack) fragment;
    }

    private static final int BACKGROUND_COLOR = WHITE;
    private PaintView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.write);
        p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.x = 190;
        p.y = 130;
        p.height = 500; //高度设置为屏幕的0.4
        p.width = 880; //宽度设置为屏幕的0.6
        p.alpha = 0.8f;
        getWindow().setAttributes(p); // 设置生效,全屏填充
        init();
    }

    private Button btnClear;
    private Button btnOk;

    private void init() {
        mView = new PaintView(getContext());
        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.tablet_view);
        frameLayout.addView(mView);
        mView.requestFocus();
        btnClear = (Button) findViewById(R.id.tablet_clear);
        btnOk = (Button) findViewById(R.id.tablet_ok);
        initClick();
    }

    private void initClick() {
        RxView.clicks(btnClear)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        mView.clear();
                    }
                });
        RxView.clicks(btnOk)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        try {
                            callBack.onHandImgOk(mView.getCachebBitmap());
                            WritePadDialog.this.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public interface CallBack {
        void onHandImgOk(Bitmap bitmap);
    }


    /**
     * This view implements the drawing canvas.
     * <p>
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
            cacheCanvas.drawColor(WHITE);
        }

        public void clear() {
            if (cacheCanvas != null) {
                paint.setColor(BACKGROUND_COLOR);
                cacheCanvas.drawPaint(paint);
                paint.setColor(Color.BLACK);
                cacheCanvas.drawColor(WHITE);
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
}




