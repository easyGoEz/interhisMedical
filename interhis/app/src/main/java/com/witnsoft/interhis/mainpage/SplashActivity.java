package com.witnsoft.interhis.mainpage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.trello.rxlifecycle.components.RxActivity;
import com.witnsoft.interhis.R;
import com.witnsoft.libinterhis.utils.ImageUtility;
import com.witnsoft.libinterhis.utils.ThriftPreUtils;

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

    //
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //  token登录
                        if (!TextUtils.isEmpty(ThriftPreUtils.getToken(SplashActivity.this))) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                });
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
