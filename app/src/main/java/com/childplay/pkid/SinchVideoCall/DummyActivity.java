package com.childplay.pkid.SinchVideoCall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.childplay.pkid.R;

/**
 * Created by altitudelabs on 16/4/2016.
 */
public class DummyActivity extends AppCompatActivity {
    private Button mParentButton;
    private Button mChildButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dummy);

        mParentButton = (Button)findViewById(R.id.btn_parent);
        mParentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DummyActivity.this, ParentMainActivity.class);
                startActivity(intent);
            }
        });
        mChildButton = (Button)findViewById(R.id.btn_child);
        mChildButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DummyActivity.this, ChildrenMainActivity.class);
                startActivity(intent);
            }
        });
    }
}
