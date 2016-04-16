package com.example.altitudelabs.pkid;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;


//Implement SurfaceHolder interface to Play video
//Implement this interface to receive information about changes to the surface
public class OnBoardingActivity extends Activity implements SurfaceHolder.Callback {

    VideoView mVideoView;

    Button btnGetStarted;
    ImageButton btnCall;

    TextView tvOnboarding;
    TextView tvPlayWithYourChild;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        tvOnboarding = (TextView) findViewById(R.id.tvOnboarding);
        tvPlayWithYourChild = (TextView) findViewById(R.id.tvPlayWithYourChild);

        btnGetStarted = (Button) findViewById(R.id.btnGetStarted);
        btnGetStarted.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                btnGetStarted.setVisibility(View.GONE);
                btnCall.setVisibility(View.VISIBLE);
                tvOnboarding.setVisibility(View.VISIBLE);
                tvPlayWithYourChild.setVisibility(View.GONE);
            }
        });

        btnCall = (ImageButton) findViewById(R.id.btnCall);
        btnCall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO go to next.
            }
        });

        //Displays a video file.
        mVideoView = (VideoView) findViewById(R.id.videoview);

        String uriPath = "android.resource://com.example.altitudelabs.pkid/" + R.raw.video;
        Uri uri = Uri.parse(uriPath);
        mVideoView.setVideoURI(uri);
        mVideoView.requestFocus();
        mVideoView.start();

        //Video Loop
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mVideoView.start(); //need to make transition seamless.
            }
        });

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mVideoView.stopPlayback();
    }

    @Override
    public void onPause(){
        super.onPause();
        mVideoView.suspend();
    }

    @Override
    public void onResume(){
        super.onResume();
        mVideoView.resume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}