package com.example.project2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.common.AbstractUVCCameraHandler;
import com.serenegiant.usb.encoder.RecordParams;
import com.serenegiant.usb.widget.CameraViewInterface;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TAG";
    private static final int VIDEO_SAVE_TIME = 5000;
    // Permissions
    private static final String[] REQUIRED_PERMISSION_LIST = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    private static final int REQUEST_CODE = 1;
    private List<String> mMissPermissions = new ArrayList<>();
    // Path
    public static final String DIRECTORY_NAME = "USBCamera";
    public static final String PICTURE_NAME = "Pictures";
    public static final String RECORD_NAME = "Videos";
    // UVC
    UVCCameraHelper mCameraHelper_1, mCameraHelper_2;
    CameraViewInterface mUVCCameraView_1, mUVCCameraView_2;

    boolean isRequest_1 = false;
    boolean isPreview_1 = false;
    boolean isRequest_2 = false;
    boolean isPreview_2 = false;
    int times = 0;
    long last_timestamp = 0;
    BufferedOutputStream bufferedOutputStream_0;
    BufferedOutputStream bufferedOutputStream_1;
    BufferedOutputStream bufferedOutputStream_3;
    BufferedOutputStream bufferedOutputStream_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "--------------------------------------------------");
        Log.d(TAG, "[Main] onCreate");
        checkAndRequestPermissions();
        ((Button)findViewById(R.id.button_take_picture)).setOnClickListener(this);
        ((Button)findViewById(R.id.button_record)).setOnClickListener(this);
        mUVCCameraView_1 = findViewById(R.id.camera_view_1);
        mUVCCameraView_1.setCallback(mCallback_1);
        mCameraHelper_1 = new UVCCameraHelper();
//        mCameraHelper = UVCCameraHelper.getInstance();
        mCameraHelper_1.setDefaultPreviewSize(640,480);
        mCameraHelper_1.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper_1.initUSBMonitor(this, mUVCCameraView_1, mDevConnectListener_1);

        mUVCCameraView_2 = findViewById(R.id.camera_view_2);
        mUVCCameraView_2.setCallback(mCallback_2);
        mCameraHelper_2 = new UVCCameraHelper();
        mCameraHelper_2.setDefaultPreviewSize(640,480);
        mCameraHelper_2.setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
        mCameraHelper_2.initUSBMonitor(this, mUVCCameraView_2, mDevConnectListener_2);

        List<DeviceInfo> infoList = getUSBDevInfo();
        for(DeviceInfo dev : infoList) {
            String str = "Device：PID_" + dev.getPID() + " & " + "VID_" + dev.getVID();
            Log.d(TAG, "[infoList] " + str);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraHelper_1 != null) {
            Log.d(TAG, "[Life] registerUSB1");
            mCameraHelper_1.registerUSB();
        }
        if (mCameraHelper_2 != null) {
            Log.d(TAG, "[Life] registerUSB2");
            mCameraHelper_2.registerUSB();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCameraHelper_1 != null) {
            Log.d(TAG, "[Life] unregisterUSB1");
            mCameraHelper_1.unregisterUSB();
        }
        if (mCameraHelper_2 != null) {
            Log.d(TAG, "[Life] unregisterUSB2");
            mCameraHelper_2.unregisterUSB();
        }
    }

    private List<DeviceInfo> getUSBDevInfo() {
        if(mCameraHelper_1 == null)
            return null;
        List<DeviceInfo> devInfos = new ArrayList<>();
        List<UsbDevice> list = mCameraHelper_1.getUsbDeviceList();
        for(UsbDevice dev : list) {
            DeviceInfo info = new DeviceInfo();
            info.setPID(dev.getVendorId());
            info.setVID(dev.getProductId());
            devInfos.add(info);
        }
        return devInfos;
    }

    private CameraViewInterface.Callback mCallback_1 = new CameraViewInterface.Callback(){
        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            Log.d(TAG, "[View1] onSurfaceCreated: isPreview = " + isPreview_1 + ", isCameraOpened = " + mCameraHelper_1.isCameraOpened());
            if (!isPreview_1 && mCameraHelper_1.isCameraOpened()) {
                mCameraHelper_1.startPreview(mUVCCameraView_1);
                isPreview_1 = true;
            }
        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
            Log.d(TAG, "[View1] onSurfaceChanged: " + width + "x" + height);
        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            Log.d(TAG, "[View1] onSurfaceDestroy");
            // must have
            if (isPreview_1 && mCameraHelper_1.isCameraOpened()) {
                mCameraHelper_1.stopPreview();
                isPreview_1 = false;
            }
        }
    };

    private CameraViewInterface.Callback mCallback_2 = new CameraViewInterface.Callback(){
        @Override
        public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
            Log.d(TAG, "[View2] onSurfaceCreated: isPreview = " + isPreview_2 + ", isCameraOpened = " + mCameraHelper_2.isCameraOpened());
            // must have
            if (!isPreview_2 && mCameraHelper_2.isCameraOpened()) {
                mCameraHelper_2.startPreview(mUVCCameraView_2);
                isPreview_2 = true;
            }
        }

        @Override
        public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
            Log.d(TAG, "[View2] onSurfaceChanged: " + width + "x" + height);
        }

        @Override
        public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
            Log.d(TAG, "[View2] onSurfaceDestroy");
            // must have
            if (isPreview_2 && mCameraHelper_2.isCameraOpened()) {
                mCameraHelper_2.stopPreview();
                isPreview_2 = false;
            }
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener mDevConnectListener_1 = new UVCCameraHelper.OnMyDevConnectListener() {
        @Override
        public void onAttachDev(UsbDevice device) {
            Log.d(TAG, "[Dev1] onAttachDev: " + device.getDeviceName() + ", isRequest = " + isRequest_1);
            if (!isRequest_1) {
                isRequest_1 = true;
                if (mCameraHelper_1 != null) {
                    mCameraHelper_1.requestPermission(0);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            Log.d(TAG, "[Dev1] onDettachDev: " + device.getDeviceName());
            showShortMsg("onDettachDev");
            if (isRequest_1) {
                isRequest_1 = false;
                mCameraHelper_1.closeCamera();
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            Log.d(TAG, "[Dev1] onConnectDev: " + device.getDeviceName() + ", isConnected = " + isConnected);
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            Log.d(TAG, "[Dev1] onDisConnectDev: " + device.getDeviceName());
        }
    };

    private UVCCameraHelper.OnMyDevConnectListener mDevConnectListener_2 = new UVCCameraHelper.OnMyDevConnectListener() {
        @Override
        public void onAttachDev(UsbDevice device) {
            Log.d(TAG, "[Dev2] onAttachDev: " + device.getDeviceName());
            // request open permission(must have)
            if (!isRequest_2) {
                isRequest_2 = true;
                if (mCameraHelper_2 != null) {
                    mCameraHelper_2.requestPermission(1);
                }
            }
        }

        @Override
        public void onDettachDev(UsbDevice device) {
            Log.d(TAG, "[Dev2] onDettachDev: " + device.getDeviceName());
            showShortMsg("onDettachDev");
            // close camera(must have)
            if (isRequest_2) {
                isRequest_2 = false;
                mCameraHelper_2.closeCamera();
            }
        }

        @Override
        public void onConnectDev(UsbDevice device, boolean isConnected) {
            Log.d(TAG, "[Dev2] onConnectDev: " + device.getDeviceName() + ", isConnected = " + isConnected);
            showShortMsg("onConnectDev");
        }

        @Override
        public void onDisConnectDev(UsbDevice device) {
            Log.d(TAG, "[Dev2] onDisConnectDev: " + device.getDeviceName());
            showShortMsg("onDisConnectDev");
        }
    };

    private void showShortMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mMissPermissions.clear();
            for (String permission : REQUIRED_PERMISSION_LIST) {
                int result = ContextCompat.checkSelfPermission(this, permission);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.add(permission);
                }
            }
            if (!mMissPermissions.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        mMissPermissions.toArray(new String[mMissPermissions.size()]),
                        REQUEST_CODE);
                Log.d(TAG, "[Main] checkAndRequestPermissions: fail");
            } else {
                Log.d(TAG, "[Main] checkAndRequestPermissions: success");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            for (int i = grantResults.length - 1; i >= 0; i--) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    mMissPermissions.remove(permissions[i]);
                }
            }
        }
        if (!mMissPermissions.isEmpty()) {
            Toast.makeText(this, "get permissions failed,exiting...",Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        if (mCameraHelper_1 == null || !mCameraHelper_1.isCameraOpened()) {
            showShortMsg("sorry,camera open failed");
        }
        switch (view.getId()) {
            case R.id.button_take_picture: // take picture
                String fileName = FileName.generate(FileName.PICTURE);
                mCameraHelper_1.capturePicture(fileName, new AbstractUVCCameraHandler.OnCaptureListener() {
                    @Override
                    public void onCaptureResult(String path) {
                        Log.d(TAG,"[Picture] onRecordResult: " + path);
                        if(TextUtils.isEmpty(path)) {
                            return;
                        }
                        runOnUiThread(() -> {
                            Toast.makeText(MainActivity.this, "save path:"+path, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
                break;
            case R.id.button_record:
                if (!mCameraHelper_1.isPushing()) { // start record
                    RecordParams params = new RecordParams();
//                    String recPath = FileName.generate(FileName.RECORD);
//                    params.setRecordPath(recPath);
//                    params.setRecordDuration(0); // auto divide saved,default 0 means not divided
//                    params.setVoiceClose(false);
//                    params.setSupportOverlay(true); // overlay only support armeabi-v7a & arm64-v8a
//                    mCameraHelper.startPusher(params, new AbstractUVCCameraHandler.OnEncodeResultListener() {
                    mCameraHelper_1.startPusher(new AbstractUVCCameraHandler.OnEncodeResultListener() {
                        @Override
                        public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                            if (times == 0 || timestamp - last_timestamp >= VIDEO_SAVE_TIME * 2) { // 0s C0
                                last_timestamp = timestamp;
                                times++;
                                String fileName = FileName.generate(FileName.H264);
                                Log.d(TAG, "[Record] Create[0]: " + fileName);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                                    bufferedOutputStream_0 = new BufferedOutputStream(fileOutputStream);
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, "[Record] Create[0]: " + e);
                                    e.printStackTrace();
                                }
                            }
                            if (timestamp - last_timestamp >= 1000 && bufferedOutputStream_1 != null && times % 2 == 1) { // 1s F1
                                try {
                                    Log.d(TAG, "[Record] Finish[1]");
                                    bufferedOutputStream_1.flush();
                                    bufferedOutputStream_1 =  null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (timestamp - last_timestamp >= VIDEO_SAVE_TIME && times % 2 == 1) { // 5s C1
                                times++;
                                String fileName = FileName.generate(FileName.H264);
                                Log.d(TAG, "[Record] Create[1]: " + fileName);
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                                    bufferedOutputStream_1 = new BufferedOutputStream(fileOutputStream);
                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, "[Record] Create[1]: " + e);
                                    e.printStackTrace();
                                }
                            }
                            if (timestamp - last_timestamp >= VIDEO_SAVE_TIME + 1000 && bufferedOutputStream_0 != null) { // 6s F0
                                try {
                                    Log.d(TAG, "[Record] Finish[0]");
                                    bufferedOutputStream_0.flush();
                                    bufferedOutputStream_0 =  null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
//                            Log.d(TAG, "[Record] type = " + type + ", length = " + length + " ,timestamp = " + timestamp);
                            if (type == 1) { // type = 1, h264 video stream
                                try {
                                    if (bufferedOutputStream_0 != null)
                                        bufferedOutputStream_0.write(data, offset, length);
                                    if (bufferedOutputStream_1 != null)
                                        bufferedOutputStream_1.write(data, offset, length);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (type == 0) { // type = 0, aac audio stream

                            }
                        }

                        @Override
                        public void onRecordResult(String videoPath) {
                            Log.d(TAG,"[Record] onRecordResult: " + videoPath);
                        }
                    });
                    showShortMsg("start record...");
                } else { // stop record
                    try {
                        if (bufferedOutputStream_0 != null)
                            bufferedOutputStream_0.flush();
                        if (bufferedOutputStream_1 != null)
                            bufferedOutputStream_1.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mCameraHelper_1.stopPusher();
                    showShortMsg("stop record...");
                }
                break;
        }
    }
}