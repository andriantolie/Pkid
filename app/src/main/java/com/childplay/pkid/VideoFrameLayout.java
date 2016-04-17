package com.childplay.pkid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by altitudelabs on 16/4/2016.
 */
public class VideoFrameLayout extends FrameLayout {
    public VideoFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        final double previewWidthToHeightRatio = 9/ 16.0;
        final double widthToHeightRatio = width / (double) height;
        if (previewWidthToHeightRatio > widthToHeightRatio) {
            width =(int)( height * 9/ 16.0f);

        } else if (previewWidthToHeightRatio < widthToHeightRatio) {
            height = (int)(width * 16 /9.0f);
        }

        setMeasuredDimension(width, height);
    }
}
