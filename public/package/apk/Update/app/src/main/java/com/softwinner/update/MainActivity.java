package com.softwinner.update;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.RelativeLayout;
import com.softwinner.shared.FileSearch;
import com.softwinner.shared.ImageSearch;

/**
 * Created by huihuixu on 2019/7/5.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    private RelativeLayout mDynamicBtn, mOtaBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_select);
        mDynamicBtn = (RelativeLayout) findViewById(R.id.dynamic_system_update);
        mOtaBtn = (RelativeLayout) findViewById(R.id.ota_system_update);
        mDynamicBtn.setOnClickListener(this);
        mOtaBtn.setOnClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == mOtaBtn) {
            Intent intent = new Intent(MainActivity.this, FileSearch.class);
            MainActivity.this.startActivity(intent);
        }else if (view == mDynamicBtn) {
            Intent intent = new Intent(MainActivity.this, ImageSearch.class);
            MainActivity.this.startActivity(intent);
        }
    }
}
