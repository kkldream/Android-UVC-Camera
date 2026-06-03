# Contributing

Thanks for considering a contribution to Android-UVC-Camera.

## Scope

This repository is a collection of Android USB UVC camera samples. Changes should stay focused on:

- keeping the samples buildable with current Android Studio tooling;
- documenting camera, permission, and recording behavior clearly;
- fixing sample app bugs that make the demos harder to use;
- preserving third-party UVC library attribution and license headers.

## Development Setup

1. Install Android Studio and Android SDK Platform 32.
2. Use JDK 17 or the bundled Android Studio JBR.
3. Open one sample project at a time: `Project1`, `Project2`, or `Project3`.
4. Build from the sample directory:

```powershell
.\gradlew.bat assembleDebug
```

## Pull Request Checklist

- Explain which sample project is affected.
- Keep changes to vendored `libusbcamera` code minimal and document why they are needed.
- Run `assembleDebug` for every sample touched.
- Preserve existing copyright and license headers.
