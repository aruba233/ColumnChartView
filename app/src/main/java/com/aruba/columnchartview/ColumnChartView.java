package com.aruba.columnchartview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图表
 * Created by aruba on 2018/3/9.
 */

public class ColumnChartView extends View {
    public static final String TAG = ColumnChartView.class.getSimpleName();

    private float maxValue = 110;
    private List<Item> items = new ArrayList<>();
    private List<ItemY> itemys = new ArrayList<>();
    //Y轴步长
    private int step_y = 20;
    private Paint axisPaint;
    private Paint columnPaint;
    private Paint columnTextPaint;
    /**
     * 柱状的宽度
     */
    private int columnWidth;
    /**
     * 坐标轴字体大小
     */
    private int axisTextSize;
    /**
     * 余留给坐标轴字的距离（y轴）
     */
    private int axis_y_padding_distance;
    /**
     * 余留给坐标轴字的距离（x轴）
     */
    private int axis_x_padding_distance;
    private int axis_padding = 10;
    /**
     * x轴的描述
     */
    private String x_describe = "日期";
    /**
     * y轴的描述
     */
    private String y_describe = "销售量/件";
    /**
     * 渲染渐变值
     */
    private int[] colorGradient = new int[]{0xFF78f7ff, 0xFF007991};

    private int showItemSize = 8;

    public ColumnChartView(Context context) {
        this(context, null);
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColumnChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        axisPaint = new Paint();
        axisPaint.setAntiAlias(true);
        axisPaint.setStrokeWidth(1);
        axisPaint.setColor(Color.parseColor("#a2a1a1"));
//        axisPaint.setColor(Color.WHITE);
        axisTextSize = Tools.dpToPx(context, 10);
        axis_padding = Tools.dpToPx(context, 10);
        axisPaint.setTextSize(axisTextSize);

        columnPaint = new Paint();
        columnPaint.setAntiAlias(true);
        columnWidth = Tools.dpToPx(context, 24);
        columnPaint.setStrokeWidth(columnWidth);

        columnTextPaint = new Paint();
        columnTextPaint.setAntiAlias(true);
        columnTextPaint.setTextSize(Tools.dpToPx(context, 10));
    }

    public void setX_describe(String x_describe) {
        this.x_describe = x_describe;
    }

    public void setY_describe(String y_describe) {
        this.y_describe = y_describe;
    }

    public void setStep_y(int step_y) {
        this.step_y = step_y;
    }

    public void initItems(List<Item> items, float max_value) {
        this.items.clear();
        this.items.addAll(items);
        this.itemys.clear();

        //计算Y轴的item数
        int count = (int) (max_value / step_y);
        //加入后面的点
        for (int i = 1; i < count + 1; i++) {
            this.itemys.add(new ItemY(step_y * i + ""));
        }

        int max_y_distance = 0;
        for (ItemY item : itemys) {
            float temp_x_distance = axisPaint.measureText(item.describe_Y);
            max_y_distance = temp_x_distance > max_y_distance ? (int) temp_x_distance : max_y_distance;
        }

        Paint.FontMetrics fontMetrics = axisPaint.getFontMetrics();
        axis_y_padding_distance = max_y_distance + axis_padding;
        axis_x_padding_distance = (int) (fontMetrics.bottom - fontMetrics.top) + axis_padding;

        //获取最后一个值(即Y轴最大值)
        maxValue = max_value;
        //加上预留的一个 Y轴最大值 = 最后一个值 + 步长
        maxValue += step_y;

        if (anime) {
            startAnime();
        } else {
            postInvalidate();
        }
    }

    private boolean anime;

    public void setAnime(boolean anime) {
        this.anime = anime;
    }

    private float progress;

    /**
     * 柱状渲染的动画
     */
    private void startAnime() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progress = (float) animation.getAnimatedValue();

                postInvalidate();
            }
        });
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());

        valueAnimator.start();
    }

    private float currentX;
    private float scrollX;
    private float max_scrollX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //记录手指X平移
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                currentX = event.getX();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float x2 = event.getX();

                scrollX += x2 - currentX;

                if (scrollX > 0) {
                    scrollX = 0;
                } else if (Math.abs(scrollX) > max_scrollX) {
                    scrollX = -max_scrollX;
                } else {
                    postInvalidate();
                }

                currentX = x2;
            }
            break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (items.size() > 0) {
            //横线
            int x_describe_padding = (int) (axisPaint.measureText(x_describe) + axis_padding);

            //竖线
            int y_describe_padding = (int) (axis_x_padding_distance + axis_padding / 2);

            Paint.FontMetrics fontMetrics = axisPaint.getFontMetrics();
            float baseline = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            //画Y轴描述
            canvas.drawText(y_describe, axis_y_padding_distance - axisPaint.measureText(y_describe) / 2, y_describe_padding / 2 + baseline, axisPaint);
            //画X轴描述
            canvas.drawText(x_describe, getMeasuredWidth() - x_describe_padding + axis_padding / 2, getMeasuredHeight() - axis_x_padding_distance + baseline, axisPaint);

            //Y轴的起始点（上）与结束点（下）
            float startY = y_describe_padding;
            float endY = getMeasuredHeight() - axis_x_padding_distance;

            //每个刻度的距离
            float disY = (startY - endY) / (itemys.size() + 1);

            //画0
            canvas.drawText("  0", axis_padding / 2, endY + baseline, axisPaint);
            //画item Y坐标描述
            for (int i = 0; i < itemys.size(); i++) {
                canvas.drawText(itemys.get(i).describe_Y, axis_padding / 2, endY + disY * (i + 1) + baseline, axisPaint);
            }

            //X轴的起始点（左）与结束点（右）
            float startX = axis_y_padding_distance;
            float endX = getMeasuredWidth() - x_describe_padding;

            //每个刻度的距离
            float disX = (endX - startX) / ((items.size() > showItemSize ? showItemSize : items.size()) + 1);

            Paint.FontMetrics columnPaintFontMetrics = columnPaint.getFontMetrics();
            float columnPaintHeight = (columnPaintFontMetrics.bottom - columnPaintFontMetrics.top);
            float columnPaintBaseline = (columnPaintFontMetrics.bottom - columnPaintFontMetrics.top) / 2 - columnPaintFontMetrics.bottom;

            //数据区bitmap
            Bitmap bufferBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);//创建内存位图 
            Canvas cacheCanvas = new Canvas(bufferBitmap);//创建绘图画布  

            //画item X坐标描述 只画显示的柱状
            int startIndex = (int) (Math.abs(scrollX) / disX);
            Log.i(TAG, "startIndex :" + startIndex);
            for (int i = startIndex < 1 ? 0 : startIndex - 1; i < items.size() && i < startIndex + showItemSize + 1; i++) {
                float textWidth = axisPaint.measureText(items.get(i).describe_X);
                //- axis_padding /2 为稍微往前偏移点，美观
                float x = startX + disX * (i + 1) - axis_padding / 2;
                float y = getMeasuredHeight() - axis_x_padding_distance / 2 + baseline;
                cacheCanvas.drawText(items.get(i).describe_X, x - textWidth / 2 + scrollX, y, axisPaint);

                //顺便把柱状也画了
                float height = (startY - endY) * items.get(i).value / maxValue;
                float column_height = endY + height * progress;
                LinearGradient linearGradient = new LinearGradient(x - columnWidth / 2 + scrollX, endY,
                        x + columnWidth / 2 + scrollX, column_height, colorGradient, null, Shader.TileMode.CLAMP);
                columnPaint.setShader(linearGradient);
                cacheCanvas.drawLine(x + scrollX, endY, x + scrollX, column_height, columnPaint);

                //画值
                float columnTextWidth = columnTextPaint.measureText((int) items.get(i).value + "");
                LinearGradient mLinearGradient = new LinearGradient(x - columnTextWidth / 2 + scrollX,
                        endY + height * progress - columnPaintHeight,
                        x + columnTextWidth / 2 + scrollX,
                        endY + height - columnPaintHeight + columnPaintBaseline, colorGradient, null, Shader.TileMode.CLAMP);
                columnTextPaint.setShader(mLinearGradient);
                cacheCanvas.drawText((int) items.get(i).value + "",
                        x - columnTextWidth / 2 + scrollX,
                        endY + height * progress - columnPaintHeight + columnPaintBaseline,
                        columnTextPaint);
            }

            ImageCache.getInstance().addBitmapToMemoryCache(String.valueOf(getId()), bufferBitmap);

            //clip bitmap
            max_scrollX = disX * (items.size() - showItemSize);
            Rect src = new Rect((int) startX, 0, (int) (endX - disX + axis_padding), getMeasuredHeight());
            RectF dst = new RectF((int) startX, 0, (int) (endX - disX + axis_padding), getMeasuredHeight());
            canvas.drawBitmap(bufferBitmap, src, dst, axisPaint);

            //后画坐标，防止柱状遮挡

            //竖线
            canvas.drawLine(axis_y_padding_distance, getMeasuredHeight() - axis_x_padding_distance,
                    axis_y_padding_distance, y_describe_padding,
                    axisPaint);
            //箭头左边部分
            canvas.drawLine(axis_y_padding_distance - axis_padding / 2, y_describe_padding + axis_padding / 2,
                    axis_y_padding_distance, y_describe_padding,
                    axisPaint);
            //箭头右边部分
            canvas.drawLine(axis_y_padding_distance + axis_padding / 2, y_describe_padding + axis_padding / 2,
                    axis_y_padding_distance, y_describe_padding,
                    axisPaint);

            //横线
            canvas.drawLine(axis_y_padding_distance, getMeasuredHeight() - axis_x_padding_distance,
                    getMeasuredWidth() - x_describe_padding, getMeasuredHeight() - axis_x_padding_distance,
                    axisPaint);
            //箭头上边部分
            canvas.drawLine(getMeasuredWidth() - x_describe_padding - axis_padding / 2, getMeasuredHeight() - axis_x_padding_distance - axis_padding / 2,
                    getMeasuredWidth() - x_describe_padding, getMeasuredHeight() - axis_x_padding_distance,
                    axisPaint);
            //箭头下边部分
            canvas.drawLine(getMeasuredWidth() - x_describe_padding - axis_padding / 2, getMeasuredHeight() - axis_x_padding_distance + axis_padding / 2,
                    getMeasuredWidth() - x_describe_padding, getMeasuredHeight() - axis_x_padding_distance,
                    axisPaint);
        }

    }

    /**
     * 柱状Item
     */
    public static class Item {
        /**
         * x轴描述
         */
        public String describe_X;

        /**
         * 值(对应Y轴的值)
         */
        public float value;

        public Item(String describe_X, float value) {
            this.describe_X = describe_X;
            this.value = value;
        }
    }

    /**
     * Y轴Item
     */
    public static class ItemY {
        /**
         * y轴描述
         */
        public String describe_Y;

        public ItemY(String describe_Y) {
            this.describe_Y = describe_Y;
        }
    }
}
