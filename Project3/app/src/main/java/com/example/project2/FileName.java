package com.example.project2;

import android.annotation.SuppressLint;

import com.jiangdg.usbcamera.UVCCameraHelper;

public class FileName {
    public static final int PICTURE = 0;
    public static final int RECORD = 1;
    public static final int H264 = 2;
    // Path
    private static final String DIRECTORY_NAME = "USBCamera";
    private static final String PICTURE_NAME = "Pictures";
    private static final String RECORD_NAME = "Videos";

    @SuppressLint("DefaultLocale")
    static String generate(int mode) {
        String filePath = UVCCameraHelper.ROOT_PATH + DIRECTORY_NAME + "/";
        String fileName = "/";
        switch (mode) {
            case PICTURE:
                filePath += PICTURE_NAME;
                fileName += System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;
                break;
            case RECORD:
                filePath += RECORD_NAME;
                fileName += System.currentTimeMillis() + UVCCameraHelper.SUFFIX_MP4;
                break;
            case H264:
                filePath += RECORD_NAME;
                fileName += System.currentTimeMillis() + ".h264";
                break;
        }
        String path = filePath + fileName;
        return path;
    }

    static String generate(int mode, int num) {
        String filePath = UVCCameraHelper.ROOT_PATH + DIRECTORY_NAME + "/";
        String fileName = "/cam" + num + "_";
        switch (mode) {
            case PICTURE:
                filePath += PICTURE_NAME;
                fileName += System.currentTimeMillis() + UVCCameraHelper.SUFFIX_JPEG;
                break;
            case RECORD:
                filePath += RECORD_NAME;
                fileName += System.currentTimeMillis() + UVCCameraHelper.SUFFIX_MP4;
                break;
            case H264:
                filePath += RECORD_NAME;
                fileName += System.currentTimeMillis() + ".h264";
                break;
        }
        String path = filePath + fileName;
        return path;
    }
}
