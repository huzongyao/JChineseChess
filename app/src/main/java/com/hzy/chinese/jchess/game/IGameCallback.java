package com.hzy.chinese.jchess.game;

/**
 * Created by HZY on 2018/3/9.
 */

public interface IGameCallback {
    void postPlaySound(int soundIndex);

    void postShowMessage(String message);

    void postShowMessage(int messageId);

    void postStartThink();

    void postEndThink();
}
