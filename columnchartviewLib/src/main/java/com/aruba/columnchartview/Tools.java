package com.aruba.columnchartview;

import android.content.Context;

/**
 * 作者　　: aruba
 * 创建时间:2017/6/6　10:54
 * <p>
 * 功能介绍：
 */
public class Tools {
    /****
     * @param context
     * @param size
     * @return
     * @function dp  changeTo  px
     */

    public static int dpToPx(Context context, int size) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (size * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
