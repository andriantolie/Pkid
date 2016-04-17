package com.example.altitudelabs.pkid.SinchVideoCall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.altitudelabs.pkid.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ParentCallScreenActivity extends BaseActivity implements OnClickListener {
    private ProgressDialog mSpinner;
    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private ImageButton mTransparantButton;
    private AudioPlayer mAudioPlayer;
    private Timer mTimer;
    private UpdateCallDurationTask mDurationTask;

    private String mCallId;
    private long mCallStart = 0;
    private boolean mAddedListener = false;
    private boolean mVideoViewsAdded = false;

    private ImageButton mColorButton1;
    private ImageButton mColorButton2;
    private ImageButton mColorButton3;

    private ImageView mHiddenLayer;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_color_1) {

        } else if (v.getId() == R.id.btn_color_2) {
            endCall();
        }else if (v.getId() == R.id.btn_color_3) {
            takeScreenshotAndShare();
        }
    }


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            ParentCallScreenActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateCallDuration();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putLong(CALL_START_TIME, mCallStart);
        savedInstanceState.putBoolean(ADDED_LISTENER, mAddedListener);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mCallStart = savedInstanceState.getLong(CALL_START_TIME);
        mAddedListener = savedInstanceState.getBoolean(ADDED_LISTENER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_callscreen);

        mAudioPlayer = new AudioPlayer(this);


        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
        mColorButton1 = (ImageButton)findViewById(R.id.btn_color_1);
        mColorButton1.setOnClickListener(this);
        mColorButton2 = (ImageButton)findViewById(R.id.btn_color_2);
        mColorButton2.setOnClickListener(this);
        mColorButton3 = (ImageButton)findViewById(R.id.btn_color_3);
        mColorButton3.setOnClickListener(this);

        mHiddenLayer = (ImageView)findViewById(R.id.view_hidden_layer);
        mTransparantButton = (ImageButton)findViewById(R.id.btn_transparent);
        mTransparantButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mHiddenLayer.bringToFront();
                mHiddenLayer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onServiceConnected() {
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            if (!mAddedListener) {
                call.addCallListener(new SinchCallListener());
                mAddedListener = true;
            }
        } else {
            Log.e(TAG, "Started with invalid callId, aborting.");
            finish();
        }

        updateUI();
    }

    private void updateUI() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mDurationTask.cancel();
        mTimer.cancel();
        removeVideoViews();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mTimer = new Timer();
        mDurationTask = new UpdateCallDurationTask();
        mTimer.schedule(mDurationTask, 0, 500);
        updateUI();
    }

    @Override
    public void onBackPressed() {
        // User should exit activity by ending call, not by going back.
    }

    private void endCall() {
        mAudioPlayer.stopProgressTone();
        Call call = getSinchServiceInterface().getCall(mCallId);
        if (call != null) {
            call.hangup();
        }
        finish();
    }

    private String formatTimespan(long timespan) {
        long totalSeconds = timespan / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updateCallDuration() {
        if (mCallStart > 0) {
//            mCallDuration.setText(format//Timespan(System.currentTimeMillis() - mCallStart));
        }
    }

    private void addVideoViews() {
        if (mVideoViewsAdded || getSinchServiceInterface() == null) {
            return; //early
        }

        final VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            FrameLayout localView = (FrameLayout) findViewById(R.id.localVideo);
            localView.addView(vc.getLocalView());
            localView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    vc.toggleCaptureDevicePosition();
                }
            });

            FrameLayout view = (FrameLayout) findViewById(R.id.remoteVideo);
            view.addView(vc.getRemoteView());
            mVideoViewsAdded = true;
        }
    }

    private void removeVideoViews() {
        if (getSinchServiceInterface() == null) {
            return; // early
        }

        VideoController vc = getSinchServiceInterface().getVideoController();
        if (vc != null) {
            FrameLayout view = (FrameLayout) findViewById(R.id.remoteVideo);
            view.removeView(vc.getRemoteView());

            FrameLayout localView = (FrameLayout) findViewById(R.id.localVideo);
            localView.removeView(vc.getLocalView());
            mVideoViewsAdded = false;
        }
    }


    private class SinchCallListener implements VideoCallListener {

        @Override
        public void onCallEnded(Call call) {
            CallEndCause cause = call.getDetails().getEndCause();
            Log.d(TAG, "Call ended. Reason: " + cause.toString());
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            String endMsg = "Call ended: " + call.getDetails().toString();
//            Toast.makeText(ParentCallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

            endCall();
        }

        @Override
        public void onCallEstablished(Call call) {
            Log.d(TAG, "Call established");
            mAudioPlayer.stopProgressTone();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
            AudioController audioController = getSinchServiceInterface().getAudioController();
            audioController.enableSpeaker();
            mCallStart = System.currentTimeMillis();
            Log.d(TAG, "Call offered video: " + call.getDetails().isVideoOffered());
            if (mSpinner != null) {
                mSpinner.dismiss();
            }
        }

        @Override
        public void onCallProgressing(Call call) {
            Log.d(TAG, "Call progressing");
            mAudioPlayer.playProgressTone();
            mSpinner = new ProgressDialog(ParentCallScreenActivity.this);
            mSpinner.setTitle("Loading");
            mSpinner.setMessage("Please wait...");
            mSpinner.show();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
            // Send a push through your push provider here, e.g. GCM
        }

        @Override
        public void onVideoTrackAdded(Call call) {
            Log.d(TAG, "Video track added");
            addVideoViews();
        }
    }

    private void takeScreenshotAndShare() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();


            final Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(imageFile));
            startActivity(Intent.createChooser(shareIntent, "Share image using"));

//            openScreenshot(imageFile);
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
    }
}
