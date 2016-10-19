package com.napster.cedar.sample.library;

import android.view.View;

public class Utils {

    public static void updateViewVisibility(boolean isVisible, View... views) {
        for(View view : views) {
            if(isVisible) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static int truncate(int val, int min, int max) {
        if(val < min) {
            return min;
        } else if(val > max) {
            return max;
        } else {
            return val;
        }
    }

}
