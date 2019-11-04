package com.hzy.chinese.jchess.game;


import com.hzy.chinese.jchess.R;
import com.hzy.chinese.jchess.xqwlight.Position;
import com.hzy.chinese.jchess.xqwlight.Search;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.hzy.chinese.jchess.game.GameConfig.RESP_CAPTURE;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_CAPTURE2;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_CHECK;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_CHECK2;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_CLICK;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_DRAW;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_ILLEGAL;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_LOSS;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_MOVE;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_MOVE2;
import static com.hzy.chinese.jchess.game.GameConfig.RESP_WIN;

public class GameLogic implements Runnable {

    private final ExecutorService mExecutor;
    private IGameView mGameView;
    private String currentFen;
    private int sqSelected, mvLast;
    private volatile boolean thinking = false;
    private boolean flipped = false;
    private int level = 0;
    private Position pos = new Position();
    private Search search = new Search(pos, 16);
    private Deque<String> mHistoryList = new ArrayDeque<>();
    private IGameCallback mGameCallback;
    private volatile boolean mDrawBoardFinish;

    public GameLogic(IGameView gameView) {
        this(gameView, null);
    }

    public GameLogic(IGameView gameView, IGameCallback callback) {
        mGameCallback = callback;
        mGameView = gameView;
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setCallback(IGameCallback callback) {
        this.mGameCallback = callback;
    }

    public void drawGameBoard() {
        for (int x = Position.FILE_LEFT; x <= Position.FILE_RIGHT; x++) {
            for (int y = Position.RANK_TOP; y <= Position.RANK_BOTTOM; y++) {
                int sq = Position.COORD_XY(x, y);
                sq = (flipped ? Position.SQUARE_FLIP(sq) : sq);
                int xx = x - Position.FILE_LEFT;
                int yy = y - Position.RANK_TOP;
                int pc = pos.squares[sq];
                if (pc > 0) {
                    mGameView.drawPiece(pc, xx, yy);
                }
                if (sq == sqSelected || sq == Position.SRC(mvLast) ||
                        sq == Position.DST(mvLast)) {
                    mGameView.drawSelected(xx, yy);
                }
            }
        }
        mDrawBoardFinish = true;
    }

    public String getCurrentFen() {
        return currentFen;
    }

    public void restart() {
        restart(false, 0);
    }

    public void restart(boolean flipped, String newFen) {
        if (!thinking) {
            this.flipped = flipped;
            currentFen = newFen;
            mHistoryList.clear();
            startPlay();
        }
    }

    public void restart(boolean flipped, int handicap) {
        if (!thinking) {
            this.flipped = flipped;
            int index = (handicap >= Position.STARTUP_FEN.length || handicap < 0) ? 0 : handicap;
            currentFen = Position.STARTUP_FEN[index];
            mHistoryList.clear();
            startPlay();
        }
    }

    public void retract() {
        if (!thinking) {
            String fen = popHistory();
            if (fen != null) {
                currentFen = fen;
                startPlay();
            }
        }
    }

    private void startPlay() {
        pos.fromFen(currentFen);
        sqSelected = mvLast = 0;
        if (flipped && pos.sdPlayer == 0) {
            thinking();
        } else {
            mGameView.postRepaint();
        }
    }

    /**
     * Do not call this function in main thread
     * it will block the process util UI updated
     */
    private void blockRepaint() {
        mDrawBoardFinish = false;
        mGameView.postRepaint();
        while (!mDrawBoardFinish) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void clickSquare(int sq_) {
        if (thinking) {
            return;
        }
        int sq = (flipped ? Position.SQUARE_FLIP(sq_) : sq_);
        int pc = pos.squares[sq];
        if ((pc & Position.SIDE_TAG(pos.sdPlayer)) != 0) {
            if (sqSelected > 0) {
                drawSquare(sqSelected);
            }
            if (mvLast > 0) {
                drawMove(mvLast);
                mvLast = 0;
            }
            sqSelected = sq;
            drawSquare(sq);
            playSound(RESP_CLICK);
            mGameView.postRepaint();
        } else if (sqSelected > 0) {
            int mv = Position.MOVE(sqSelected, sq);
            if (!pos.legalMove(mv)) {
                return;
            }
            if (!pos.makeMove(mv)) {
                playSound(RESP_ILLEGAL);
                return;
            }
            int response = pos.inCheck() ? RESP_CHECK :
                    pos.captured() ? RESP_CAPTURE : RESP_MOVE;
            if (pos.captured()) {
                pos.setIrrev();
            }
            mvLast = mv;
            sqSelected = 0;
            drawMove(mv);
            playSound(response);
            if (!getResult()) {
                thinking();
            } else {
                mGameView.postRepaint();
            }
        }
    }

    private void drawSquare(int sq_) {
        int sq = (flipped ? Position.SQUARE_FLIP(sq_) : sq_);
        int x = Position.FILE_X(sq) - Position.FILE_LEFT;
        int y = Position.RANK_Y(sq) - Position.RANK_TOP;
        //canvas.postRepaint(x, y, SQUARE_SIZE, SQUARE_SIZE);
    }

    private void drawMove(int mv) {
        //drawSquare(Position.SRC(mv));
        //drawSquare(Position.DST(mv));
    }

    private void playSound(int response) {
        if (mGameCallback != null) {
            mGameCallback.postPlaySound(response);
        }
    }

    private void showMessage(String message) {
        if (mGameCallback != null) {
            mGameCallback.postShowMessage(message);
        }
    }

    private void showMessage(int stringResId) {
        if (mGameCallback != null) {
            mGameCallback.postShowMessage(stringResId);
        }
    }

    @Override
    public void run() {
        thinking = true;
        mGameCallback.postStartThink();
        int mv = mvLast;
        search.prepareSearch();
        blockRepaint();
        mvLast = search.searchMain(100 << level);
        pos.makeMove(mvLast);
        drawMove(mv);
        drawMove(mvLast);
        int response = pos.inCheck() ? RESP_CHECK2 :
                pos.captured() ? RESP_CAPTURE2 : RESP_MOVE2;
        if (pos.captured()) {
            pos.setIrrev();
        }
        getResult(response);
        thinking = false;
        mGameView.postRepaint();
        mGameCallback.postEndThink();
    }

    private void thinking() {
        mExecutor.submit(this);
    }

    private boolean getResult() {
        return getResult(-1);
    }

    private boolean getResult(int response) {
        if (pos.isMate()) {
            playSound(response < 0 ? RESP_WIN : RESP_LOSS);
            showMessage(response < 0 ?
                    R.string.congratulations_you_win : R.string.you_lose_and_try_again);
            return true;
        }
        int vlRep = pos.repStatus(3);
        if (vlRep > 0) {
            vlRep = (response < 0 ? pos.repValue(vlRep) : -pos.repValue(vlRep));
            playSound(vlRep > Position.WIN_VALUE ? RESP_LOSS :
                    vlRep < -Position.WIN_VALUE ? RESP_WIN : RESP_DRAW);
            showMessage(vlRep > Position.WIN_VALUE ?
                    R.string.play_too_long_as_lose : vlRep < -Position.WIN_VALUE ?
                    R.string.pc_play_too_long_as_lose : R.string.standoff_as_draw);
            return true;
        }
        if (pos.moveNum > 100) {
            playSound(RESP_DRAW);
            showMessage(R.string.both_too_long_as_draw);
            return true;
        }
        if (response >= 0) {
            playSound(response);
            pushHistory(currentFen);
            currentFen = pos.toFen();
        }
        return false;
    }

    private void pushHistory(String fen) {
        if (mHistoryList.size() >= GameConfig.MAX_HISTORY_SIZE) {
            mHistoryList.poll();
        }
        mHistoryList.offer(fen);
    }

    private String popHistory() {
        if (mHistoryList.size() == 0) {
            showMessage(R.string.no_more_histories);
            playSound(RESP_ILLEGAL);
            return null;
        }
        playSound(RESP_MOVE2);
        return mHistoryList.pollLast();
    }
}
