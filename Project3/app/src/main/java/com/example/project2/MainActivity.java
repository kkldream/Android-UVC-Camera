package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.hardware.usb.UsbDevice;
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
    // UVC
    private static final int CAMERA_NUM = 2;
    UVCCameraHelper[] mCameraHelper = new UVCCameraHelper[CAMERA_NUM];
    CameraViewInterface[] mUVCCameraView = new CameraViewInterface[CAMERA_NUM];
    boolean[] isPreview = new boolean[CAMERA_NUM];
    boolean[] isRequest = new boolean[CAMERA_NUM];
    BufferedOutputStream[][] bufferedOutputStreams = new BufferedOutputStream[CAMERA_NUM][2];
    int times = 0;
    long last_timestamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "--------------------------------------------------");
        Log.d(TAG, "[Main] onCreate");
        initView();
        for (int i = 0; i < CAMERA_NUM; i++) {
            mUVCCameraView[i].setCallback(generateCameraViewInterfaceCallback(i));
            mCameraHelper[i] = new UVCCameraHelper();
            mCameraHelper[i].setDefaultPreviewSize(640, 480);
            mCameraHelper[i].setDefaultFrameFormat(UVCCameraHelper.FRAME_FORMAT_MJPEG);
            mCameraHelper[i].initUSBMonitor(this, mUVCCameraView[i], generateOnMyDevConnectListener(i));
        }
        showAllUsbDev();
        for (int i = 0; i < CAMERA_NUM; i++) {
            int finalI = i;
            mCameraHelper[i].setOnPreviewFrameListener(new AbstractUVCCameraHandler.OnPreViewResultListener() {
                @Override
                public void onPreviewResult(byte[] nv21Yuv) {
                    Log.d(TAG, "[Main " + finalI + "] onPreviewResult: " + nv21Yuv.length);
                }
            });
        }
    }

    void initView() {
        ((Button)findViewById(R.id.button_take_picture)).setOnClickListener(this);
        ((Button)findViewById(R.id.button_record)).setOnClickListener(this);
        mUVCCameraView[0] = findViewById(R.id.camera_view_1);
        mUVCCameraView[1] = findViewById(R.id.camera_view_2);
    }

    void showAllUsbDev() {
        if(mCameraHelper[0] == null)  return;
        List<UsbDevice> usbDeviceListlist = mCameraHelper[0].getUsbDeviceList();
        for (int i = 0; i < usbDeviceListlist.size(); i++) {
            String str = "Device：PID_" + usbDeviceListlist.get(i).getProductId() + " & " + "VID_" + usbDeviceListlist.get(i).getVendorId();
            Log.d(TAG, "[USB devs " + (i++) + "] " + str);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (int i = 0; i < CAMERA_NUM; i++) {
            if (mCameraHelper[i] != null) {
                Log.d(TAG, "[Life] registerUSB" + i);
                mCameraHelper[i].registerUSB();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (int i = 0; i < CAMERA_NUM; i++) {
            if (mCameraHelper[i] != null) {
                Log.d(TAG, "[Life] unregisterUSB" + i);
                mCameraHelper[i].unregisterUSB();
            }
        }
    }

    private CameraViewInterface.Callback generateCameraViewInterfaceCallback(int num) {
        CameraViewInterface.Callback mCallback = new CameraViewInterface.Callback(){
            @Override
            public void onSurfaceCreated(CameraViewInterface view, Surface surface) {
                Log.d(TAG, "[View" + num + "] onSurfaceCreated: isPreview = " + isPreview[num] + ", isCameraOpened = " + mCameraHelper[num].isCameraOpened());
                if (!isPreview[num] && mCameraHelper[num].isCameraOpened()) {
                    mCameraHelper[num].startPreview(mUVCCameraView[num]);
                    isPreview[num] = true;
                }
            }

            @Override
            public void onSurfaceChanged(CameraViewInterface view, Surface surface, int width, int height) {
                Log.d(TAG, "[View" + num + "] onSurfaceChanged: " + width + "x" + height);
            }

            @Override
            public void onSurfaceDestroy(CameraViewInterface view, Surface surface) {
                Log.d(TAG, "[View" + num + "] onSurfaceDestroy");
                if (isPreview[num] && mCameraHelper[num].isCameraOpened()) {
                    mCameraHelper[num].stopPreview();
                    isPreview[num] = false;
                }
            }
        };
        return mCallback;
    }

    private UVCCameraHelper.OnMyDevConnectListener generateOnMyDevConnectListener(int num) {
        UVCCameraHelper.OnMyDevConnectListener mDevConnectListener = new UVCCameraHelper.OnMyDevConnectListener() {
            @Override
            public void onAttachDev(UsbDevice device) {
                Log.d(TAG, "[Dev" + num + "] onAttachDev: " + device.getDeviceName() + ", isRequest = " + isRequest[num]);
                if (!isRequest[num]) {
                    isRequest[num] = true;
                    if (mCameraHelper[num] != null) {
                        mCameraHelper[num].requestPermission(num);
                    }
                }
            }

            @Override
            public void onDettachDev(UsbDevice device) {
                Log.d(TAG, "[Dev" + num + "] onDettachDev: " + device.getDeviceName());
                showShortMsg("onDettachDev");
                if (isRequest[num]) {
                    isRequest[num] = false;
                    mCameraHelper[num].closeCamera();
                }
            }

            @Override
            public void onConnectDev(UsbDevice device, boolean isConnected) {
                Log.d(TAG, "[Dev" + num + "] onConnectDev: " + device.getDeviceName() + ", isConnected = " + isConnected);
            }

            @Override
            public void onDisConnectDev(UsbDevice device) {
                Log.d(TAG, "[Dev" + num + "] onDisConnectDev: " + device.getDeviceName());
            }
        };
        return mDevConnectListener;
    }

    private void showShortMsg(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_take_picture: // take picture
                for (int i = 0; i < CAMERA_NUM; i++) {
                    String fileName = FileName.generate(FileName.PICTURE, i);
                    take_picture(i, fileName);
                }
                break;
            case R.id.button_record:
                for (int i = 0; i < CAMERA_NUM; i++) {
                    record(i);
                }
                break;
        }
    }

    private void take_picture(int num, String fileName) {
        Log.d(TAG,"[Picture " + num + "] take_picture: " + fileName);
        if (mCameraHelper[num] == null || !mCameraHelper[num].isCameraOpened()) {
            Log.d(TAG, "[Picture " + num + "] camera open failed");
            return;
        }
        mCameraHelper[num].capturePicture(fileName, path -> {
            Log.d(TAG,"[Picture " + num + "] onRecordResult: " + path);
        });
    }

    private void record(int num) {
        if (mCameraHelper[num] == null || !mCameraHelper[num].isCameraOpened()) {
            Log.d(TAG, "[Record " + num + "] camera open failed");
            return;
        }
        if (!mCameraHelper[num].isPushing()) { // start record
            Log.d(TAG,"[Record " + num + "] start record");
            mCameraHelper[num].startPusher(new AbstractUVCCameraHandler.OnEncodeResultListener() {
                @Override
                public void onEncodeResult(byte[] data, int offset, int length, long timestamp, int type) {
                    if (times == 0 || timestamp - last_timestamp >= VIDEO_SAVE_TIME * 2) { // 0s C0
                        last_timestamp = timestamp;
                        times++;
                        String fileName = FileName.generate(FileName.H264, num);
                        Log.d(TAG, "[Record " + num + "] Create[0]: " + fileName);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                            bufferedOutputStreams[num][0] = new BufferedOutputStream(fileOutputStream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (timestamp - last_timestamp >= 1000 && bufferedOutputStreams[num][1] != null && times % 2 == 1) { // 1s F1
                        try {
                            Log.d(TAG, "[Record " + num + "] Finish[1]");
                            bufferedOutputStreams[num][1].flush();
                            bufferedOutputStreams[num][1] = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (timestamp - last_timestamp >= VIDEO_SAVE_TIME && times % 2 == 1) { // 5s C1
                        times++;
                        String fileName = FileName.generate(FileName.H264, num);
                        Log.d(TAG, "[Record " + num + "] Create[1]: " + fileName);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
                            bufferedOutputStreams[num][1] = new BufferedOutputStream(fileOutputStream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (timestamp - last_timestamp >= VIDEO_SAVE_TIME + 1000 && bufferedOutputStreams[num][0] != null) { // 6s F0
                        try {
                            Log.d(TAG, "[Record " + num + "] Finish[0]");
                            bufferedOutputStreams[num][0].flush();
                            bufferedOutputStreams[num][0] =  null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "[Record " + num + "] type = " + type + ", length = " + length + " ,timestamp = " + timestamp);
                    if (type == 1) { // type = 1, h264 video stream
                        try {
                            if (bufferedOutputStreams[num][0] != null)
                                bufferedOutputStreams[num][0].write(data, offset, length);
                            if (bufferedOutputStreams[num][1] != null)
                                bufferedOutputStreams[num][1].write(data, offset, length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (type == 0) { // type = 0, aac audio stream

                    }
                }

                @Override
                public void onRecordResult(String videoPath) {
                    Log.d(TAG,"[Record " + num + "] onRecordResult: " + videoPath);
                }
            });
        } else { // stop record
            Log.d(TAG,"[Record " + num + "] stop record");
            try {
                if (bufferedOutputStreams[0][1] != null)
                    bufferedOutputStreams[0][1].flush();
                if (bufferedOutputStreams[0][1] != null)
                    bufferedOutputStreams[0][1].flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCameraHelper[num].stopPusher();
        }
    }
}