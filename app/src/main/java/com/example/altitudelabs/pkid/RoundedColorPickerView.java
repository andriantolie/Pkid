package com.example.altitudelabs.pkid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedColorPickerView extends ImageView {
    private Path mClipPath;
    private Paint mBorderPaint;
    private RectF mRectClipBoundary;
    private RectF mRectBorderBoundary;
    private float mBorderWidth;

    public RoundedColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setImageResource(R.drawable.ui_editor_colorpicker);

        mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBorderPaint.setColor(Color.WHITE);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderWidth = getResources().getDimension(R.dimen.color_picker_border_width);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mClipPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectClipBoundary = new RectF(0,0,w,h);
        mRectBorderBoundary = new RectF(mBorderWidth/2, mBorderWidth/2, w-mBorderWidth/2, h-mBorderWidth/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Clip the path
        float radius = getResources().getDimension(R.dimen.color_picker_corner_radius);
        mClipPath.reset();
        mClipPath.addRoundRect(mRectClipBoundary, radius, radius, Path.Direction.CW);
        canvas.clipPath(mClipPath);

        // Draw image
        super.onDraw(canvas);

        // Draw rounded corner border
        canvas.drawRoundRect(mRectBorderBoundary, radius, radius, mBorderPaint);



    }
}
