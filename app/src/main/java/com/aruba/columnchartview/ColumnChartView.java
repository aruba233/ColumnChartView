package com.aruba.columnchartview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 柱状图表
 * Created by aruba on 2018/3/9.
 */

public class ColumnChartView extends View {
    private float maxValue = 110;
    private List<Item> items = new ArrayList<>();
    private List<ItemY> itemys = new ArrayList<>();
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
    private String x_describe = "日期";
    private String y_describe = "销售量/件";

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

    public void initItems(List<Item> items, List<ItemY> itemys) {
        this.items.clear();
        this.items.addAll(items);
        this.itemys.clear();
        this.itemys.addAll(itemys);

        int max_y_distance = 0;
        for (ItemY item : itemys) {
            float temp_x_distance = axisPaint.measureText(item.describe_Y);
            max_y_distance = temp_x_distance > max_y_distance ? (int) temp_x_distance : max_y_distance;
        }

        Paint.FontMetrics fontMetrics = axisPaint.getFontMetrics();
        axis_y_padding_distance = max_y_distance + axis_padding;
        axis_x_padding_distance = (int) (fontMetrics.bottom - fontMetrics.top) + axis_padding;

        postInvalidate();
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
            float disX = (endX - startX) / (items.size() + 1);

            Paint.FontMetrics columnPaintFontMetrics = columnPaint.getFontMetrics();
            float columnPaintHeight = (columnPaintFontMetrics.bottom - columnPaintFontMetrics.top);
            float columnPaintBaseline = (columnPaintFontMetrics.bottom - columnPaintFontMetrics.top) / 2 - columnPaintFontMetrics.bottom;
            //画item X坐标描述
            for (int i = 0; i < items.size(); i++) {
                float textWidth = axisPaint.measureText(items.get(i).describe_X);
                //- axis_padding /2 为稍微往前偏移点，美观
                float x = startX + disX * (i + 1) - axis_padding / 2;
                float y = getMeasuredHeight() - axis_x_padding_distance / 2 + baseline;
                canvas.drawText(items.get(i).describe_X, x - textWidth / 2, y, axisPaint);

                //顺便把柱状也画了
                float height = (startY - endY) * items.get(i).value / maxValue - baseline;
                LinearGradient linearGradient = new LinearGradient(x - columnWidth / 2, endY, x + columnWidth / 2, endY + height, new int[]{0xFF007991, 0xFF78f7ff}, null, Shader.TileMode.CLAMP);
                columnPaint.setShader(linearGradient);
                canvas.drawLine(x, endY, x, endY + height, columnPaint);

                //画值
                float columnTextWidth = columnTextPaint.measureText((int) items.get(i).value + "");
                LinearGradient mLinearGradient = new LinearGradient(x - columnTextWidth / 2, endY + height - columnPaintHeight,
                        x + columnTextWidth / 2, endY + height - columnPaintHeight + columnPaintBaseline, new int[]{0xFF78f7ff, 0xFF007991}, null, Shader.TileMode.CLAMP);
                columnTextPaint.setShader(mLinearGradient);
                canvas.drawText((int) items.get(i).value + "", x - columnTextWidth / 2, endY + height - columnPaintHeight + columnPaintBaseline, columnTextPaint);
            }

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
         * 值
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
