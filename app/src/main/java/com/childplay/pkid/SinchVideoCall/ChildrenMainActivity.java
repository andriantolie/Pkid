package com.childplay.pkid.SinchVideoCall;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;

/**
 * Created by altitudelabs on 16/4/2016.
 */
public class ChildrenMainActivity extends BaseActivity implements SinchService.StartFailedListener{
    private ProgressDialog mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    protected void onServiceConnected() {
        getSinchServiceInterface().setStartListener(this);
        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient("children");
            mSpinner = new ProgressDialog(this);
            mSpinner.setTitle("Loading");
            mSpinner.setMessage("Please wait...");
            mSpinner.show();
        } else {
            getSinchServiceInterface().stopClient();
            getSinchServiceInterface().startClient("children");
            mSpinner = new ProgressDialog(this);
            mSpinner.setTitle("Logging in");
            mSpinner.setMessage("Please wait...");
            mSpinner.show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    private void openChildrenCallScreenActivity() {
        Call call = getSinchServiceInterface().callUserVideo("parent");
        String callId = call.getCallId();

        Intent callScreen = new Intent(this, ChildrenCallScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        startActivity(callScreen);
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
        openChildrenCallScreenActivity();
    }



    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }



}
