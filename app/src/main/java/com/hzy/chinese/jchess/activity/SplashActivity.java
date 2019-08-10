package com.hzy.chinese.jchess.activity;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.blankj.utilcode.util.ActivityUtils;
import com.hzy.chinese.jchess.R;
import com.hzy.chinese.jchess.game.GameConfig;
import com.hzy.chinese.jchess.xqwlight.Position;

import java.io.InputStream;

/**
 * Created by HZY on 2018/3/6.
 */

public class SplashActivity extends Activity {

    private static boolean mDataLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (mDataLoaded) {
            startGame();
        } else {
            loadBookAndStartGame();
        }
    }

    private void loadBookAndStartGame() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // do some loading job
                    InputStream is = getAssets().open(GameConfig.DAT_ASSETS_PATH);
                    Position.loadBook(is);
                    mDataLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startGame();
            }
        }.start();
    }

    private void startGame() {
        ActivityUtils.startActivity(MainActivity.class);
        finish();
    }
}
