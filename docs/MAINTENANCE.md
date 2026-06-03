# Maintenance Notes

This repository is maintained as a practical Android USB UVC camera sample collection.

## Current Maintenance Scope

- Keep `Project1`, `Project2`, and `Project3` buildable with current Android Studio tooling.
- Preserve working sample behavior before doing broad library refactors.
- Keep vendored UVC library changes small, documented, and attributable.
- Prefer documentation and build fixes over API churn.

## Known Constraints

- The samples target compile SDK 32 to preserve the original Android behavior.
- Runtime camera behavior depends on the Android device, USB host implementation, camera firmware, cable quality, and available USB bandwidth.
- Project3's multi-camera recording path is experimental. Multi-camera preview is the supported Project3 scenario.
- Native libraries are committed because the upstream UVC projects depend on prebuilt `.so` artifacts.

## Verification

Before publishing changes, run:

```powershell
cd Project1
.\gradlew.bat assembleDebug testDebugUnitTest

cd ..\Project2
.\gradlew.bat assembleDebug testDebugUnitTest

cd ..\Project3
.\gradlew.bat assembleDebug testDebugUnitTest
```

If Android SDK discovery fails, set:

```powershell
$env:ANDROID_HOME='C:\Users\<you>\AppData\Local\Android\Sdk'
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
```

## Roadmap

- Add device-tested compatibility notes for common USB UVC cameras.
- Add screenshots or short clips showing each sample in use.
- Document Android 10+ scoped storage behavior for capture and recording paths.
- Evaluate whether Project2 and Project3 can share a single maintained `libusbcamera` module without changing sample behavior.
