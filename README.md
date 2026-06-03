# Android-UVC-Camera

Android USB UVC camera sample collection. The repository keeps three standalone Android projects that show progressively more complete UVC camera workflows: preview, picture capture, segmented recording, and multiple USB camera previews.

## Projects

| Project | Purpose | UVC base | Notes |
| --- | --- | --- | --- |
| `Project1` | Basic USB UVC preview with `TextureView` | Local `uvccamerasdk` imported from [`Liuguihong/AndroidUVCCamera`](https://github.com/Liuguihong/AndroidUVCCamera) | Smallest preview-only sample; self-contained instead of relying on JitPack artifact availability. |
| `Project2` | Single camera preview, picture capture, and segmented H.264 recording | [`jiangdongguo/AndroidUSBCamera`](https://github.com/jiangdongguo/AndroidUSBCamera) | Uses two output streams to reduce recording gaps between segments. |
| `Project3` | Multiple USB camera previews | Modified `Project2` local library | Makes `UVCCameraHelper` instance-based instead of singleton-based for multi-camera preview. |

See [docs/PROJECTS.md](docs/PROJECTS.md) for a more detailed guide.
See [docs/MAINTENANCE.md](docs/MAINTENANCE.md) for maintenance scope, verification commands, known constraints, and roadmap.

## Repository Layout

```text
.
├── Project1/          # Basic preview sample
├── Project2/          # Single-camera capture and segmented recording sample
├── Project3/          # Multi-camera preview sample
├── libusbcamera/      # Reference copy of the UVC helper library
├── docs/              # Project notes and maintenance docs
├── LICENSE
└── NOTICE
```

Each sample is an independent Android project with its own Gradle wrapper. Open one project at a time in Android Studio or run Gradle from that project directory. `Project1` includes `uvccamerasdk` locally because the historical JitPack artifact for `Liuguihong/AndroidUVCCamera:1.0.0` is no longer reliably resolvable.

## Requirements

- Android Studio with Android SDK Platform 32 installed
- JDK 17 or Android Studio's bundled JBR
- Android device with USB host support
- USB UVC camera and an OTG/USB adapter supported by the device

The samples currently use:

- Android Gradle Plugin 7.4.2
- Gradle 7.5.1
- compile SDK 32
- min SDK 25
- Java 8 source compatibility

## Build

From any sample directory:

```powershell
cd Project2
$env:ANDROID_HOME='C:\Users\<you>\AppData\Local\Android\Sdk'
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
.\gradlew.bat assembleDebug
```

Repeat from `Project1`, `Project2`, or `Project3` depending on the sample you want to build.

## Feature Status

| Capability | Project1 | Project2 | Project3 |
| --- | --- | --- | --- |
| USB camera permission flow | Yes | Yes | Yes |
| Preview | One camera | One camera | Multiple cameras |
| Picture capture | No | Yes | Per camera |
| Segmented H.264 recording | No | Yes | Experimental |
| Simultaneous multi-camera recording | No | No | Not a stable supported feature |

## Maintenance Notes

This repository is maintained as a practical reference for Android developers who need examples of external USB UVC camera integration. The current focus is:

- keeping the samples buildable with current Android Studio/JDK tooling;
- documenting the three sample paths clearly;
- preserving upstream UVC library attribution;
- making targeted fixes in sample code without rewriting the vendored UVC libraries.

## Attribution

This repository references and includes code derived from these projects:

- [`saki4510t/UVCCamera`](https://github.com/saki4510t/UVCCamera)
- [`jiangdongguo/AndroidUSBCamera`](https://github.com/jiangdongguo/AndroidUSBCamera)
- [`Liuguihong/AndroidUVCCamera`](https://github.com/Liuguihong/AndroidUVCCamera)

Source files with their own copyright or license headers retain those notices. See [NOTICE](NOTICE).

## License

This repository is licensed under the Apache License 2.0. See [LICENSE](LICENSE).
