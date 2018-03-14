package com.hzy.chinese.jchess.game;

import com.hzy.chinese.jchess.R;

/**
 * Created by HZY on 2018/3/8.
 */

public class GameConfig {

    public static final String DAT_ASSETS_PATH = "book.dat";
    public static final int SPLASH_DELAY_MILLISECONDS = 500;
    public static final int MAX_HISTORY_SIZE = 512;
    public static final String PREF_LAST_FEN = "PREF_LAST_FEN";

    public static final int RESP_CLICK = 0;
    public static final int RESP_ILLEGAL = 1;
    public static final int RESP_MOVE = 2;
    public static final int RESP_MOVE2 = 3;
    public static final int RESP_CAPTURE = 4;
    public static final int RESP_CAPTURE2 = 5;
    public static final int RESP_CHECK = 6;
    public static final int RESP_CHECK2 = 7;
    public static final int RESP_WIN = 8;
    public static final int RESP_DRAW = 9;
    public static final int RESP_LOSS = 10;

    public static final int PIECE_THEME_CARTOON = 0;
    public static final int PIECE_THEME_WOOD = 1;

    public static final int[] SOUND_RES_ARRAY = {
            R.raw.click, R.raw.illegal, R.raw.move,
            R.raw.move2, R.raw.capture, R.raw.capture2,
            R.raw.check, R.raw.check2, R.raw.win,
            R.raw.draw, R.raw.loss
    };

    public static final int[] PIECE_RES_WOOD = {
            R.drawable.rk, R.drawable.ra, R.drawable.rb,
            R.drawable.rn, R.drawable.rr, R.drawable.rc,
            R.drawable.rp, R.drawable.bk, R.drawable.ba,
            R.drawable.bb, R.drawable.bn, R.drawable.br,
            R.drawable.bc, R.drawable.bp, R.drawable.selected
    };

    public static final int[] PIECE_RES_CARTOON = {
            R.drawable.rk2, R.drawable.ra2, R.drawable.rb2,
            R.drawable.rn2, R.drawable.rr2, R.drawable.rc2,
            R.drawable.rp2, R.drawable.bk2, R.drawable.ba2,
            R.drawable.bb2, R.drawable.bn2, R.drawable.br2,
            R.drawable.bc2, R.drawable.bp2, R.drawable.selected2
    };
}
