package com.witnsoft.interhis.splash;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.trello.rxlifecycle.components.RxActivity;
import com.witnsoft.interhis.R;
import com.witnsoft.interhis.db.OnDbOpened;
import com.witnsoft.interhis.db.YaoListDBHelper;
import com.witnsoft.interhis.login.LoginActivity;
import com.witnsoft.interhis.mainpage.MainActivity;
import com.witnsoft.libinterhis.utils.ImageUtility;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;
import com.witnsoft.libinterhis.utils.ui.AutoScaleLinearLayout;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by zhengchengpeng on 2017/5/12.
 */

@ContentView(R.layout.activity_splash)
public class SplashActivity extends RxActivity implements Animation.AnimationListener {
    private static final String TAG = "SplashActivity";
    private static final int ANIMATION_DURATION = 2000;

    @ViewInject(R.id.iv_start)
    private ImageView ivStart;
    @ViewInject(R.id.ll_init)
    private AutoScaleLinearLayout llInit;
    @ViewInject(R.id.pg_init)
    private ProgressBar pgInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        //splashAnimation();
        init();
    }

    // 直接启动
    private void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(ANIMATION_DURATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                YaoListDBHelper yaoListDBHelper = new YaoListDBHelper(SplashActivity.this, null, 1);
                yaoListDBHelper.setOnDbOpened(new OnDbOpened() {
                    @Override
                    public void onDbOpened() {
                        // 数据库已经被打开
                        Log.d(TAG, "running for onDbOpened back");
                        //  token登录
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                llInit.setVisibility(View.GONE);
                            }
                        });
                        if (!TextUtils.isEmpty(ThriftPreUtils.getToken(SplashActivity.this))) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }

                    @Override
                    public void onDbCreate() {
                        // 数据库建表开始
                        Log.d(TAG, "running for onDbCreate back");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                llInit.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                });
                try {
                    yaoListDBHelper.getWritableDatabase();
                } catch (SQLiteException exception) {
                    yaoListDBHelper.getReadableDatabase();
                }

            }
        }).start();
    }

    // 图片动画启动
    private void splashAnimation() {
        ImageUtility.loadImage(this.ivStart,
                ".png",
                R.drawable.splash);

        AlphaAnimation animation = new AlphaAnimation(0.1f, 1.0f);
        animation.setDuration(2000);
        animation.setAnimationListener(this);
        this.ivStart.startAnimation(animation);
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //
    }
}
