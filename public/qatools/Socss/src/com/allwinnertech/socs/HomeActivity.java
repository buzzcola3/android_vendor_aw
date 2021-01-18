package com.allwinnertech.socs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HomeActivity extends Activity implements OnClickListener {

    Context mContext;
    private TestsConfig mTestConfig;
    private boolean mAutoTest = false;
    private Handler mHandler = new Handler();


    private boolean checkAutoTestConfiged(){
        return mTestConfig.getAutoTest();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mContext = (Context) this;
        Button mStart = (Button)this.findViewById(R.id.start);
        Button mConfig = (Button)this.findViewById(R.id.config);
        Button mExit = (Button)this.findViewById(R.id.exit);
        Button mSetpTest = (Button)this.findViewById(R.id.step_test);
        mTestConfig = new TestsConfig();
        mTestConfig.loadAllConfigFromFile(this.getFilesDir() + "/config.json");
        mStart.setOnClickListener(this);
        mConfig.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mSetpTest.setOnClickListener(this);
        if (checkAutoTestConfiged())
            autoTestStart();
    }

    @Override
    public void onClick(View arg0) {
        switch(arg0.getId()){
        case R.id.start:
          autoTestStart();
          break;
        case R.id.config:
          startConfig();
          break;
        case R.id.step_test:
          stepTestStart();
          break;
        case R.id.exit:
          finish();
          break;
        }
    }

    private void startConfig() {
        Intent intent = new Intent();
        intent.setAction("com.allwinnertech.socs.ACTION_CONFIG");
        mContext.startActivity(intent);
    }

    private Runnable mNextTestRunnable = new Runnable() {

        @Override
        public void run() {
            stepTestStart();
        }
    };

    private void autoTestStart() {
    	mTestConfig.gTestPosition = 0;
        mAutoTest = true;
        mHandler.post(mNextTestRunnable);
    }

    private void stepTestStart() {
        if (mTestConfig.gTestPosition < mTestConfig.TESTCASES.size()) {
            Intent intent = new Intent();
            intent.setClass(this, mTestConfig.TESTCASES.get(mTestConfig.gTestPosition));
            startActivityForResult(intent, mTestConfig.gTestPosition);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mTestConfig.gTestPosition) {
            if (resultCode == RESULT_OK) {
            	mTestConfig.gTestPosition++;
                if (mAutoTest)
                    mHandler.post(mNextTestRunnable);
            }
        }
    }


}
