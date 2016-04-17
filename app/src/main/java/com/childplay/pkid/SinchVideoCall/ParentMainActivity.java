package com.childplay.pkid.SinchVideoCall;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;

/**
 * Created by altitudelabs on 16/4/2016.
 */
public class ParentMainActivity extends BaseActivity implements SinchService.StartFailedListener{
    private ProgressDialog mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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
    protected void onPause() {
        super.onPause();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    private void openParentCallScreenActivity() {
//        Call call = getSinchServiceInterface().callUserVideo("parent");
//        String callId = call.getCallId();
//
//        Intent callScreen = new Intent(this, ParentCallScreenActivity.class);
//        callScreen.putExtra(SinchService.CALL_ID, callId);
//        startActivity(callScreen);
    }



    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openParentCallScreenActivity();
    }



    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }



}
