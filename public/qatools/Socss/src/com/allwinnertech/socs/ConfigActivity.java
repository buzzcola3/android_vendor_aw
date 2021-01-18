package com.allwinnertech.socs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.Manifest.permission;
import android.content.pm.PackageManager;

public class ConfigActivity extends Activity {

    private String TAG = "ConfigActivity";
    private static final int READ_TFCARD_REQUEST_CODE = 38;
    private static final int READ_USBHOST_REQUEST_CODE = 42;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 42;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 45;

    Context mContext;
    CheckBox mEnableDefConfig;
    CheckBox mAutoTestConfig;
    View mSelfConfig;
    Button mTfcard;
    Button mUsbhost;
    CheckBox mEnableEthtest;
    CheckBox mEnableCameraTest;
    CheckBox mEnableCpuid;
    Button mSave;
    TextView mtfcard_video_path ;
    TextView musbhost_video_path;
    TestsConfig testconfg;
    private String configFilePath;



    @Override
    public void onActivityResult(int requestCode, int resultCode,
            Intent resultData) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
            }
            DocUtils du = new DocUtils();
            String realpath;
            switch(requestCode){
            case READ_TFCARD_REQUEST_CODE:
                Log.i(TAG, "tfcard Uri: " + uri.toString());
                realpath = du.getPath(mContext, uri);
                testconfg.tfcard_video = realpath;
                mtfcard_video_path.setText(testconfg.tfcard_video);
                break;
            case READ_USBHOST_REQUEST_CODE:
                Log.i(TAG, "usbhost Uri: " + uri.toString());
                realpath = du.getPath(mContext, uri);
                testconfg.usbhost_video = realpath;
                musbhost_video_path.setText(testconfg.usbhost_video);
                break;
            }
        }
    }

    private boolean checkPermissions() {
        if (checkSelfPermission(permission.CAMERA)
                 != PackageManager.PERMISSION_GRANTED)
            return false;
        if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                 != PackageManager.PERMISSION_GRANTED)
            return false;
        return true;
    }

    private void requestAllPermissions() {
        if (checkSelfPermission(permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
        if (checkSelfPermission(permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length == 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                    return;
                }
                if (checkPermissions())
                    initConfig();
                else
                    requestAllPermissions();
                break;
            }
        }
    }


    private void initConfig(){
        setContentView(R.layout.activity_choose_config);
        mContext = this;
        configFilePath = mContext.getFilesDir() + "/config.json";
        testconfg = new TestsConfig();
        if (testconfg.checkConfigFileExist(configFilePath))
            testconfg.loadAllConfigFromFile(configFilePath);
        mEnableDefConfig = (CheckBox) this.findViewById(R.id.enable_default);
        mAutoTestConfig = (CheckBox) this.findViewById(R.id.auto_test_default);
        mSelfConfig = this.findViewById(R.id.self_config);
        if (testconfg.getHideConfig()) {
            mEnableDefConfig.setText(R.string.use_def_config);
            mSelfConfig.setVisibility(View.INVISIBLE);
        } else {
            mEnableDefConfig.setText(R.string.use_self_config);
            mSelfConfig.setVisibility(View.VISIBLE);
        }

        if (testconfg.getAutoTest()) {
            mAutoTestConfig.setText(R.string.auto_test_config);
            mAutoTestConfig.setChecked(true);
        } else {
            mAutoTestConfig.setText(R.string.manual_operation_test_config);
            mAutoTestConfig.setChecked(false);
        }
        mTfcard = (Button) this.findViewById(R.id.tfcard);
        mTfcard.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, READ_TFCARD_REQUEST_CODE);
            }

        });
        mtfcard_video_path = (TextView) this.findViewById(R.id.tfcard_video_path);
        mtfcard_video_path.setText(testconfg.tfcard_video);
        musbhost_video_path = (TextView) this.findViewById(R.id.usbhost_video_path);
        musbhost_video_path.setText(testconfg.usbhost_video);
        mUsbhost = (Button) this.findViewById(R.id.usbhost);
        mUsbhost.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("video/*");
                startActivityForResult(intent, READ_USBHOST_REQUEST_CODE);
            }

        });
        mEnableEthtest = (CheckBox) this.findViewById(R.id.enable_ethtest);
        mEnableEthtest.setChecked(testconfg.enable_ethernet_test);
        mEnableEthtest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    testconfg.enable_ethernet_test = true;
                    mEnableEthtest.setText(R.string.enable);
                } else {
                    testconfg.enable_ethernet_test = false;
                    mEnableEthtest.setText(R.string.disable);
                }
            }

        });
        mEnableCameraTest = (CheckBox) this.findViewById(R.id.enable_cameratest);
        mEnableCameraTest.setChecked(testconfg.enable_camera);
        mEnableCameraTest.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    testconfg.enable_camera = true;
                    mEnableCameraTest.setText(R.string.enable);
                } else {
                    testconfg.enable_camera = false;
                    mEnableCameraTest.setText(R.string.disable);
                }
            }

        });        
        mEnableCpuid = (CheckBox) this.findViewById(R.id.enable_cpuid);
        mEnableCpuid.setChecked(testconfg.enable_cpuid_test);
        mEnableCpuid.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    testconfg.enable_cpuid_test = true;
                    mEnableCpuid.setText(R.string.enable);
                } else {
                    testconfg.enable_cpuid_test =false;
                    mEnableCpuid.setText(R.string.disable);
                }
            }

        });
        mSave = (Button) this.findViewById(R.id.save);
        mSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.d(TAG,"save config to file");
                testconfg.saveAllConfigToFile(configFilePath);
                finish();
            }

        });
        mEnableDefConfig.setOnCheckedChangeListener(new OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {

                if (arg1) {
                    testconfg.hide_config = true;
                    mEnableDefConfig.setText(R.string.use_def_config);
                    mSelfConfig.setVisibility(View.INVISIBLE);
                } else {
                    testconfg.hide_config = false;
                    mEnableDefConfig.setText(R.string.use_self_config);
                    mSelfConfig.setVisibility(View.VISIBLE);
                }
            }

        });
        mAutoTestConfig.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                if (arg1) {
                    mAutoTestConfig.setText(R.string.auto_test_config);
                    testconfg.setAutoTest(true);
                } else {
                    mAutoTestConfig.setText(R.string.manual_operation_test_config);
                    testconfg.setAutoTest(false);
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkPermissions())
            initConfig();
        else
            requestAllPermissions();
    }
}
