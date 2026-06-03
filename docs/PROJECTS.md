# Project Guide

`Android-UVC-Camera` is organized as three standalone Android sample projects. Open and build one project at a time from its own directory.

## Project1: Basic UVC Preview

- Path: `Project1/`
- App module: `Project1/app`
- Main class: `com.example.project1.MainActivity`
- Local library module: `Project1/uvccamerasdk`
- UVC base: [`Liuguihong/AndroidUVCCamera`](https://github.com/Liuguihong/AndroidUVCCamera)

This sample demonstrates the smallest path from USB device attachment to camera preview. It requests USB permission, opens the camera, sets a `640x480` preview size, and renders into a `TextureView`.

The `uvccamerasdk` module is kept locally because the original JitPack artifact for `Liuguihong/AndroidUVCCamera:1.0.0` is not reliably available anymore.

## Project2: Single Camera Capture and Segmented Recording

- Path: `Project2/`
- App module: `Project2/app`
- Local library module: `Project2/libusbcamera`
- Main class: `com.example.project2.MainActivity`
- UVC base: [`jiangdongguo/AndroidUSBCamera`](https://github.com/jiangdongguo/AndroidUSBCamera)

This sample adds picture capture and H.264 stream recording. The recording demo alternates two `BufferedOutputStream` instances so a new segment can start before the previous segment is closed, reducing gaps between saved video chunks.

## Project3: Multi-Camera Preview

- Path: `Project3/`
- App module: `Project3/app`
- Local library module: `Project3/libusbcamera`
- Main class: `com.example.project2.MainActivity`
- UVC base: `Project2` plus a non-singleton `UVCCameraHelper`

This sample demonstrates multiple USB camera previews by creating separate `UVCCameraHelper` instances. Multi-camera preview is the main supported behavior. Picture capture is available per camera. Simultaneous multi-camera recording remains experimental and depends on device bandwidth, encoder support, and USB host stability.

## Root `libusbcamera`

The root-level `libusbcamera/` directory is retained as a reference copy of the UVC helper library. The runnable samples use the libraries inside their own project directories.

## Build Notes

- Compile SDK: 32
- Min SDK: 25
- Java language level: 8
- Recommended JDK for building: 17
- Recommended IDE: current Android Studio with Android SDK Platform 32 installed
