# AURIA Identify — recognition of music playing in another app

Status: **Android integration in `main`; APK/device testing pending. Windows loopback not yet implemented.**

## Desired experience

AURIA recognizes a short excerpt of a track that the user hears in another application, without making them copy a song title or import a file. The feature should let the user:

1. Select **Listen to this device** (primary action) or **Microphone**.
2. Approve Android's audio and playback-capture prompts explicitly; never record automatically.
3. See the new AURIA Halo listening surface and progress.
4. View track name, artist, artwork and available metadata, or an explicit no-match/failure.
5. Open/play the matched item in AURIA's supported catalog, search if no exact match is available, and use existing track actions.
6. Find prior discoveries in recognition history or launch the flow from Quick Settings.

## Present Android code

- `feature/recognition/DevicePlaybackCapture.kt`: AudioPlaybackCapture + MediaProjection on Android 10+.
- `feature/recognition/MicrophoneCapture.kt`: opt-in microphone fallback.
- `feature/recognition/RecognitionProjectionActivity.kt`: requests RECORD_AUDIO first, then Android MediaProjection consent.
- `feature/recognition/MusicRecognitionService.kt`: foreground recognition, notifications and shutdown.
- `feature/recognition/ShazamRecognitionProvider.kt`: legacy non-SDK network integration. This is **not official ShazamKit**; endpoint stability and provider terms require review.
- `feature/recognition/AudDRecognitionProvider.kt`: optional external recognition fallback, requires an account token and may incur API costs.
- `feature/recognition/RecognitionCatalogMatcher.kt`: maps a recognized track back to AURIA/YouTube Music search.
- `feature/recognition/RecognitionHistoryStore.kt`: on-device identification history.
- `ui/recognition/AuriaRecognitionHalo.kt` and `RecognitionScreen.kt`: AURIA-branded capture state and preferred device-capture action.
- `feature/recognition/MusicRecognitionTileService.kt`: Quick Settings shortcut.

## Android platform constraints

- Internal playback capture only works with apps that explicitly allow their audio to be captured, on Android 10+.
- `RECORD_AUDIO` and user-approved MediaProjection are both required. Permissions are requested only after the user taps a capture action; never start silently.
- DRM-protected apps, restrictive capture policies, different user profiles and unavailable audio will prevent internal recognition. Explain this instead of promising universal capture.
- Microphone mode works only when audio can physically be heard and is not blocked by the device/audio route.
- The primary recognition backend is a **legacy unofficial protocol**, so long-term reliability is not guaranteed. Audit against service policies or replace with a supported licensed SDK or provider before public release.
- Music metadata identification does not grant the right to download copyrighted audio. Playback and downloads remain subject to catalog availability and applicable rights.

## Remaining tasks before shipping

- **Android**: compile/tests, UI screenshots on phone/tablet, MediaProjection permission/cancellation handling on Android 10/13/14/15/16, silence/blocked-app error UX, quick-tile cold-start tests, no-match, recognition accuracy and saved history.
- **Providers**: test real audio against providers, check rate limits and privacy disclosures, decide whether to migrate to **official ShazamKit** or licensed API.
- **Windows**: implement opt-in WASAPI loopback capture for audio played by other apps (with explicit microphone fallback), shared recognition result model and matching UI; validate real devices.
- **Usability**: quick entry from AURIA Home, source selection, secure configuration for optional API credentials, distinct offline/online and permission messages, history persistence test.
- **Release**: no APK or Windows installer is declared ready by this document.

## Acceptance scenarios

- Play music in a compatible external app, tap device capture, grant permission, identify a known excerpt, open the catalog result, save to recognition history.
- Run the same test on an app that blocks internal capture; AURIA must **not** imply universal support and must offer the microphone option.
- Cancel consent, deny microphone permission, stop a projection during capture, toggle Quick Settings tile, rotate the phone, repeat recognition after a previous result.
