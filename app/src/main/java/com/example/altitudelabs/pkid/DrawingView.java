package com.example.altitudelabs.pkid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

public class DrawingView extends View {
    private Path mDrawPath;
    private Paint mDrawPaint;
    private int mPaintColor = Color.WHITE;
    private float mPaintWidth = 10;
    private Canvas mDrawCanvas;
    private DrawingController mDrawingController;
    private DrawingListener mDrawingListener;
    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        mDrawingController = new DrawingController();
        mDrawPath = new Path();
        // Setup paint
        setupPaint();
    }

    private void setupPaint() {
        // Setup paint
        mDrawPaint = new Paint();
        mDrawPaint.setColor(mPaintColor);
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStrokeWidth(mPaintWidth);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mDrawPaint.setFilterBitmap(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Bitmap mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (DrawingPath dp: mDrawingController.getPaths()) {
            canvas.drawPath(dp.path, dp.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDrawPath = new Path();
                DrawingPath dp = new DrawingPath(mDrawPath, mDrawPaint);
                mDrawingController.addPath(dp);
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    public void setDrawingListener(DrawingListener listener) {
        mDrawingListener = listener;
    }
    public DrawingController getDrawingController() {
        return mDrawingController;
    }


    public class DrawingController extends Object {
        private Stack<DrawingPath> paths;
        public  DrawingController() {
            super();
            paths = new Stack<>();
        }
        public void addPath(DrawingPath path) {
            paths.add(path);
            if (mDrawingListener != null) {
                mDrawingListener.onNumOfPathChanged(paths.size());
            }
        }
        public Stack<DrawingPath> getPaths() {
            return paths;
        }
        public void undo() {
            if (!paths.empty()) {
                paths.pop();
                invalidate();
                if (mDrawingListener != null) {
                    mDrawingListener.onNumOfPathChanged(paths.size());
                }
            }
        }
        public void setPaintColor(int paintColor) {
            mPaintColor = paintColor;
            setupPaint();
        }
        public void setBrushThickness(float thickness) {
            mPaintWidth = thickness;
            setupPaint();
        }
    }

    public interface DrawingListener {
        void onNumOfPathChanged(int numOfPath);
    }

    // Private class to store each path and paint
    private class DrawingPath {
        Path path;
        Paint paint;
        DrawingPath(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }
}
