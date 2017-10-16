package io.luciferldy.bnimation.utils;

import android.content.Context;

/**
 * Created by lian_ on 2017/10/16.
 */

public class CommentUtils {

    private static float destiny = 0;

    /**
     * dip to px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (destiny == 0) {
            destiny = context.getResources().getDisplayMetrics().density;
        }
        return (int) (dipValue * destiny + 0.5f);
    }
}
