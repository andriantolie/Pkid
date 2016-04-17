package com.example.altitudelabs.pkid.SinchVideoCall;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.example.altitudelabs.pkid.DrawingView;
import com.example.altitudelabs.pkid.R;
import com.sinch.android.rtc.AudioController;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallEndCause;
import com.sinch.android.rtc.video.VideoCallListener;
import com.sinch.android.rtc.video.VideoController;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChildrenCallScreenActivity extends BaseActivity implements SinchService.StartFailedListener, View.OnClickListener {
    private ProgressDialog mSpinner;
    @Override
    public void onClick(View v) {
        mColorButton1.setImageResource(R.drawable.green_75);
        mColorButton2.setImageResource(R.drawable.orange_75);
        mColorButton3.setImageResource(R.drawable.blue_75);
        if (v.getId() == R.id.btn_color_1) {
            mColorButton1.setImageResource(R.drawable.green);
            mDrawingView.getDrawingController().setPaintColor(Color.GREEN);
        } else if (v.getId() == R.id.btn_color_2) {
            mColorButton2.setImageResource(R.drawable.orange);
            mDrawingView.getDrawingController().setPaintColor(Color.RED);
        }else if (v.getId() == R.id.btn_color_3) {
            mColorButton3.setImageResource(R.drawable.blue);
            mDrawingView.getDrawingController().setPaintColor(Color.BLUE);

//            try {
//                Utils.saveDrawingBitmapToFile(this, Utils.createBitmapFromView(mDrawingView),true);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }


    static final String TAG = CallScreenActivity.class.getSimpleName();
    static final String CALL_START_TIME = "callStartTime";
    static final String ADDED_LISTENER = "addedListener";

    private DrawingView mDrawingView;

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


    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }


    private class UpdateCallDurationTask extends TimerTask {

        @Override
        public void run() {
            ChildrenCallScreenActivity.this.runOnUiThread(new Runnable() {
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
        setContentView(R.layout.children_callscreen);

        mAudioPlayer = new AudioPlayer(this);

        mCallId = getIntent().getStringExtra(SinchService.CALL_ID);
        if (savedInstanceState == null) {
            mCallStart = System.currentTimeMillis();
        }
        mDrawingView = (DrawingView)findViewById(R.id.view_drawing);

        mColorButton1 = (ImageButton)findViewById(R.id.btn_color_1);
        mColorButton1.setOnClickListener(this);
        mColorButton2 = (ImageButton)findViewById(R.id.btn_color_2);
        mColorButton2.setOnClickListener(this);
        mColorButton3 = (ImageButton)findViewById(R.id.btn_color_3);
        mColorButton3.setOnClickListener(this);

        mSpinner = new ProgressDialog(ChildrenCallScreenActivity.this);
        mSpinner.setTitle("Loading");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
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

            FrameLayout view = (FrameLayout) findViewById(R.id.remoteVideo);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            view.setLayoutParams(lp);
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
//            Toast.makeText(ChildrenCallScreenActivity.this, endMsg, Toast.LENGTH_LONG).show();

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
}
