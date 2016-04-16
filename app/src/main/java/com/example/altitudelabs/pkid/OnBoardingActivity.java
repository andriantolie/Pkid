package com.example.altitudelabs.pkid;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.altitudelabs.pkid.SinchVideoCall.BaseActivity;
import com.example.altitudelabs.pkid.SinchVideoCall.SinchService;
import com.sinch.android.rtc.SinchError;


//Implement SurfaceHolder interface to Play video
//Implement this interface to receive information about changes to the surface
public class OnBoardingActivity extends BaseActivity implements SurfaceHolder.Callback ,SinchService.StartFailedListener {

    VideoView mVideoView;

    Button btnGetStarted;
    ImageButton btnCall;

    TextView tvOnboarding;
    TextView tvPlayWithYourChild;

    View include_share_dialog;

    RelativeLayout rlShare;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);

        getWindow().setFormat(PixelFormat.UNKNOWN);

        rlShare = (RelativeLayout) findViewById(R.id.include_share_dialog);

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
                rlShare.setVisibility(View.VISIBLE);
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

        ImageView whatsappButton = (ImageView)rlShare.findViewById(R.id.btnWhatsapp);
        whatsappButton.setClickable(true);
        whatsappButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
//                browserIntent.setData(Uri.parse("http://familytime.com/1a2b3c"));
//                startActivity(browserIntent);
                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.setPackage("com.whatsapp");
                share.putExtra(Intent.EXTRA_TEXT, "http://familytime.com/1a2b3c");

                startActivity(share);
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
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        mVideoView.stopPlayback();
    }

    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient("parent");
        } else {
            getSinchServiceInterface().stopClient();
            getSinchServiceInterface().startClient("parent");
        }
    }


    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {
    }

}