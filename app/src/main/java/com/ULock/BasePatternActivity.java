package com.ULock;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.zhanghai.android.patternlock.PatternView;


public class BasePatternActivity extends AppCompatActivity {

    private static final int CLEAR_PATTERN_DELAY_MILLI = 2000;

    protected TextView mMessageText;
    protected PatternView mPatternView;
    protected LinearLayout mButtonContainer;
    protected Button mLeftButton;
    protected Button mRightButton;
    public boolean startingActivity = false;
    private static final String TAG = "com.ULock.inappbilling";

 //base activity for calling the pattern activity
    private final Runnable clearPatternRunnable = new Runnable() {
        public void run() {
            // clearPattern() resets display mode to DisplayMode.Correct.
            mPatternView.clearPattern();
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity);
        mMessageText = (TextView)findViewById(R.id.pl_message_text);
        mPatternView = (PatternView)findViewById(R.id.pl_pattern);
        mButtonContainer = (LinearLayout)findViewById(R.id.pl_button_container);
        mLeftButton = (Button)findViewById(R.id.pl_left_button);
        mRightButton = (Button)findViewById(R.id.pl_right_button);
    }

//methods to keep the activity clear when the activity is started
    protected void removeClearPatternRunnable() {
        mPatternView.removeCallbacks(clearPatternRunnable);
    }

//clear the activity after every 2 seconds of patten attempts
    protected void postClearPatternRunnable() {
        removeClearPatternRunnable();
        mPatternView.postDelayed(clearPatternRunnable, CLEAR_PATTERN_DELAY_MILLI);
    }
    @Override
    public void onStop()
    {
        super.onStop();

    }
    //to clear all data if user leave the activity
    @Override
    protected void onUserLeaveHint()
    {
        if(startingActivity)
        {
            // Reset boolean for next time
            startingActivity = false;
        }
        else
        {
            // User is exiting to another application, do what you want here
            finish();
        }
    }

//restart activity when the lock button is turned Off/On in the same activity
    @Override
    public void startActivity(Intent intent)
    {
        startingActivity = true;
        super.startActivity(intent);
    }

// to read previous data when the lock is opened and start from where we left
    @Override
    public void startActivityForResult(Intent intent, int requestCode)
    {
        startingActivity = true;
        super.startActivityForResult(intent, requestCode);
    }
}