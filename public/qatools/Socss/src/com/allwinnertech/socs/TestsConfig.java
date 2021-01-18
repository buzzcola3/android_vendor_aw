package com.allwinnertech.socs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class TestsConfig {
    private static final String TAG = "TestsConfig";
    public String tfcard_video;
    public String usbhost_video;
    public boolean enable_ethernet_test;
    public boolean enable_cpuid_test;
    public boolean hide_config;
    private boolean auto_test;
    public boolean enable_camera;

    /*public static Class<?> TESTCASES[] = {
        VideoTestActivity.class,
        CameraTestActivity.class,
        MainTestActivity.class,
    };*/
    
    public List<Class<?>> TESTCASES = new ArrayList<>();

    public int gTestPosition;

    public TestsConfig() {
        tfcard_video = "";
        usbhost_video = "";
        enable_ethernet_test = true;
        enable_cpuid_test = true;
        hide_config = false;
        auto_test = true;
        updateTestCase();
    }
    
    public void updateTestCase(){
    	TESTCASES.clear();
        TESTCASES.add(VideoTestActivity.class);
        if(enable_camera){
        	Log.d("Debug","enable camera add camera class");
            TESTCASES.add(CameraTestActivity.class);
        }
        TESTCASES.add(MainTestActivity.class);
    }

    public String getTfcardVideoPath(){
        return tfcard_video;
    }
    public void setTfcardVideoPath(String path){
         tfcard_video = path;
    }
    public String getUsbhostVideoPath(){
        return usbhost_video;
    }
    public void setUsbhostVideoPath(String path){
        usbhost_video = path;
    }
    public boolean getEnableEthTest(){
        return enable_ethernet_test;
    }
    public void setEnableEthTest(boolean enable){
        enable_ethernet_test = enable;
    }
    public boolean getEnableCpuidTest(){
        return enable_cpuid_test;
    }
    public void setEnableCpuidTest(boolean enable){
        enable_cpuid_test = enable;
    }
    public boolean getHideConfig(){
        return hide_config;
    }
    public void setHideConfig(boolean hide){
        hide_config = hide;
    }
    public boolean getAutoTest() {
        return auto_test;
    }
    public void setAutoTest(boolean auto) {
        auto_test = auto;
    }
    
    public void setEnableCameraTest(boolean enable){
    	enable_camera = enable;
    	
    }
    public boolean getEnableCameraTest(boolean enable){
    	return enable_camera;
    }
    public void saveAllConfigToFile(String configpath){

        File configFile = new File(configpath);
        JSONObject configObject = new JSONObject();
        try {
            configObject.put("tfcard_video_path", this.tfcard_video);
            configObject.put("usbhost_video_path", this.usbhost_video);
            configObject.put("enable_ethernet_test", this.enable_ethernet_test);
            configObject.put("enable_cpuid_test", this.enable_cpuid_test);
            configObject.put("hide_config", this.hide_config);
            configObject.put("auto_test", this.auto_test);
            configObject.put("enable_camera", this.enable_camera);
            Log.d(TAG,configObject.toString());
            FileWriter fw = new FileWriter(configFile);
            fw.write(configObject.toString());
            fw.flush();
            fw.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkConfigFileExist(String configpath){
        File configFilePath = new File(configpath);
        return configFilePath.exists();
    }

    public void loadAllConfigFromFile(String configpath){
        File configFile = new File(configpath);
        try {
            FileReader fr  = new FileReader(configFile);
            BufferedReader br = new BufferedReader(fr);
            JSONObject configObject = new JSONObject(br.readLine());
            Log.d(TAG,configObject.toString());
            this.tfcard_video = configObject.getString("tfcard_video_path");
            this.usbhost_video = configObject.getString("usbhost_video_path");
            this.enable_ethernet_test = configObject.getBoolean("enable_ethernet_test");
            this.enable_cpuid_test = configObject.getBoolean("enable_cpuid_test");
            this.hide_config = configObject.getBoolean("hide_config");
            this.auto_test = configObject.getBoolean("auto_test");
            this.enable_camera = configObject.getBoolean("enable_camera");
            updateTestCase();
        } catch (JSONException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
