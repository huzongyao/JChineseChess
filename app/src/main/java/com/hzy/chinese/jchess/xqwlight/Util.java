package com.hzy.chinese.jchess.xqwlight;

import java.io.IOException;
import java.io.InputStream;

public class Util {
    public static int MIN_MAX(int min, int mid, int max) {
        return mid < min ? min : mid > max ? max : mid;
    }

    public static int readShort(InputStream in) throws IOException {
        int b0 = in.read();
        int b1 = in.read();
        if (b0 == -1 || b1 == -1) {
            throw new IOException();
        }
        return b0 | (b1 << 8);
    }

    public static int readInt(InputStream in) throws IOException {
        int b0 = in.read();
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        if (b0 == -1 || b1 == -1 || b2 == -1 || b3 == -1) {
            throw new IOException();
        }
        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    public static int binarySearch(int vl, int[] vls, int from, int to) {
        int low = from;
        int high = to - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            if (vls[mid] < vl) {
                low = mid + 1;
            } else if (vls[mid] > vl) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }

    private static final int[] SHELL_STEP = {0, 1, 4, 13, 40, 121, 364, 1093};

    public static void shellSort(int[] mvs, int[] vls, int from, int to) {
        int stepLevel = 1;
        while (SHELL_STEP[stepLevel] < to - from) {
            stepLevel++;
        }
        stepLevel--;
        while (stepLevel > 0) {
            int step = SHELL_STEP[stepLevel];
            for (int i = from + step; i < to; i++) {
                int mvBest = mvs[i];
                int vlBest = vls[i];
                int j = i - step;
                while (j >= from && vlBest > vls[j]) {
                    mvs[j + step] = mvs[j];
                    vls[j + step] = vls[j];
                    j -= step;
                }
                mvs[j + step] = mvBest;
                vls[j + step] = vlBest;
            }
            stepLevel--;
        }
    }
}