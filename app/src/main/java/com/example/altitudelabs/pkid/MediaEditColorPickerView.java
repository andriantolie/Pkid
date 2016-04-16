package com.example.altitudelabs.pkid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedDrawable;

import de.hdodenhof.circleimageview.CircleImageView;

public class MediaEditColorPickerView extends RelativeLayout implements View.OnTouchListener{
    private ImageView mColorRangeImageView;
    private View mColorPickerStick;
    private CircleImageView mColorPickerCircle;
    private MediaEditColorPickerListener mMediaEditColorPickerListener;
    private Bitmap mColorPickerBitmap;


    public MediaEditColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_media_edit_color_picker, this);

        initProperties();
        // Set attributes
        setAttributes(context, attrs);

    }

    private void initProperties() {
        // Get reference
        mColorRangeImageView = (ImageView)findViewById(R.id.iv_color_picker);
        mColorPickerStick = findViewById(R.id.view_handle_stick);
        mColorPickerCircle = (CircleImageView) findViewById(R.id.view_handle_circle);
        mColorPickerBitmap = RoundedDrawable.drawableToBitmap(mColorRangeImageView.getDrawable());

        // Add observer when layout done, i.e. set default color
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                double whiteLocationInPercentage = 0.865;
                int defaultLocation = (int)( mColorRangeImageView.getWidth() * whiteLocationInPercentage);
                moveHandler(defaultLocation);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        setOnTouchListener(this);

    }
    private void setAttributes(Context context, AttributeSet attrs) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int X = (int) event.getRawX();

        // Translate absolute coordinate to local coordinate
        final int[] colorPickerLocation = new int[2];
        getLocationInWindow(colorPickerLocation);
        X = X - colorPickerLocation[0];

        moveHandler(X);
        return true;
    }

    private void moveHandler(int X) {
        int sliderWidth = mColorRangeImageView.getWidth();
        int circleWidth = mColorPickerCircle.getWidth();
        int stickWidth = mColorPickerStick.getWidth();
        float colorPickerMargin = getResources().getDimension(R.dimen.color_picker_margin);
        float colorPickerBorder = getResources().getDimension(R.dimen.color_picker_border_width);
        float colorPickerCornerRadius = getResources().getDimension(R.dimen.color_picker_corner_radius);

        float minX = colorPickerBorder + colorPickerCornerRadius/2;
        float maxX = sliderWidth - colorPickerBorder - stickWidth - colorPickerCornerRadius/2;

        // translate X to the center
        X = Math.round(X-stickWidth/2.0f);

        X = Math.min(X, Math.round(maxX));
        X = Math.max(X, Math.round(minX));
        LayoutParams stickLayoutParams = (LayoutParams) mColorPickerStick.getLayoutParams();
        stickLayoutParams.leftMargin = X;
        mColorPickerStick.setLayoutParams(stickLayoutParams);


        LayoutParams circleLayoutParams = (LayoutParams) mColorPickerCircle.getLayoutParams();
        circleLayoutParams.leftMargin = (int)(X - circleWidth/2.0f + stickWidth/2.0f + colorPickerMargin);
        mColorPickerCircle.setLayoutParams(circleLayoutParams);
        // Set the color
        setColor(X);
        invalidate();
    }

    private void setColor(int X){
        // Get the color
        int sliderHeight = mColorRangeImageView.getHeight();
        int sliderWidth = mColorRangeImageView.getWidth();
        int bitmapWidth = mColorPickerBitmap.getWidth();
        double bitmapToSliderWidthRatio = bitmapWidth/ (double)sliderWidth;
        X = (int) (X * bitmapToSliderWidthRatio);

        int color = mColorPickerBitmap.getPixel(X, sliderHeight / 2);
        // Set the color
        ColorDrawable colorDrawable = new ColorDrawable(color);
        mColorPickerCircle.setImageDrawable(colorDrawable);
        // Call the listener
        if (mMediaEditColorPickerListener != null) {
            mMediaEditColorPickerListener.onColorChanged(this, color);
        }

    }

    public void setMediaEditColorPickerListener(MediaEditColorPickerListener listener) {
        mMediaEditColorPickerListener = listener;
    }

    public interface MediaEditColorPickerListener {
        void onColorChanged(View v, int color);
    }


}
