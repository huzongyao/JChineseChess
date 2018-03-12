package com.hzy.chinese.jchess.activity;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.SnackbarUtils;
import com.hzy.chinese.jchess.R;
import com.hzy.chinese.jchess.game.GameConfig;
import com.hzy.chinese.jchess.game.GameLogic;
import com.hzy.chinese.jchess.game.IGameCallback;
import com.hzy.chinese.jchess.view.GameBoardView;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements IGameCallback {

    @BindView(R.id.game_board)
    GameBoardView mGameBoard;
    private SoundPool mSoundPool;
    private LinkedList<Integer> mSoundList;
    private GameLogic mGameLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSoundPool();
        initGameLogic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSoundPool != null) {
            mSoundPool.release();
        }
    }

    private void initSoundPool() {
        mSoundList = new LinkedList<>();
        int poolSize = GameConfig.SOUND_RES_ARRAY.length;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mSoundPool = new SoundPool.Builder().setMaxStreams(poolSize).build();
        } else {
            mSoundPool = new SoundPool(poolSize, AudioManager.STREAM_MUSIC, 0);
        }
        for (int res : GameConfig.SOUND_RES_ARRAY) {
            mSoundList.add(mSoundPool.load(this, res, 1));
        }
    }

    private void initGameLogic() {
        mGameLogic = mGameBoard.getGameLogic();
        mGameLogic.setCallback(this);
        mGameLogic.restart();
    }

    @Override
    public void postPlaySound(final int soundIndex) {
        if (mSoundPool != null) {
            int soundId = mSoundList.get(soundIndex);
            mSoundPool.play(soundId, 1, 1, 0, 0, 1);
        }
    }

    @Override
    public void postShowMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarUtils.with(mGameBoard).setMessage(message).show();
            }
        });
    }

    @Override
    public void postShowMessage(int messageId) {
        postShowMessage(getString(messageId));
    }
}
