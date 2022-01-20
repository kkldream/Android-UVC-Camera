package com.example.project2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jiangdg.usbcamera.UVCCameraHelper;
import com.serenegiant.usb.widget.CameraViewInterface;

public class MainActivity extends AppCompatActivity {
    UVCCameraHelper mCameraHelper;
    CameraViewInterface mUVCCameraView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}