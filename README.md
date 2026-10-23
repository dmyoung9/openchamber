# Android native read-aloud verification

Tested 2026-10-23 on the official Google APIs Android 15 / API 35 x86_64 system image, revision 9, with emulator 37.2.12 and KVM acceleration. The image has Google WebView 124.0.6367.219. No Google account, personal media, real provider credentials, or production server was used.

## Builds

- Foreground-only auto-read PR #5511, HEAD `16d7f9db1a7c17abb9134535c258dea7177caab3`: full debug APK build passed with JDK 21 and SDK 35.
- Android audio-focus PR #5512, HEAD `f8214d0934cdc4e4d642a1e470e8a8fd5bedfc0d`: full debug APK build passed, including native plugin compilation.

For each APK, a temporary asset-only React fixture imported the branch's actual `useMessageTTS` and `useAutoReadAloud` hooks. It ran inside the real Capacitor Activity with the real native plugins. The synthesis HTTP response was replaced with a generated WAV; AudioContext decoding, playback scheduling, native focus requests, and OS lifecycle events were real. Fixture assets are absent from the PRs and pristine APKs.

## Foreground-only auto-read

`foreground-results.json` records the native lifecycle and actual non-silent AudioBufferSourceNode start counts.

- A visible automatic reply started audio.
- A reply delivered after native inactive/document hidden triggered no synthesis or audio start, and did not replay on return.
- Backgrounding and returning during delayed synthesis invalidated its automatic start. The response arrived, but no audio was scheduled.
- A reading that had already started scheduled its second and third chunks while backgrounded and completed normally.
- Manual reading retained its existing behavior.

An initial fixture attempt delivered the background reply before Android's Home animation had finished. The final assertions wait for both native inactive state and hidden document visibility before delivery.

## Android audio focus

- Manual and automatic readings paused an independently packaged cooperating AudioTrack media player. That player resumed on reading stop/completion.
- Established reading retained focus across app switches and chunk boundaries.
- New competing media and a transient MAY_DUCK request stopped speech.
- A simulated phone call's established locked focus returned `granted:false`; no reading began and active state cleared. A separate incoming-call transition interrupted an established reading.
- Chrome kept playing during delayed synthesis, paused when the reading's first audio became ready, stopped advancing its video clock, and resumed on focus release.
- Synthesis failure cleared the reading without interrupting Chrome. Process teardown released focus and Chrome resumed.

`chrome-focus-test.mp4` records Chrome playing a generated video, switching to the native fixture to begin reading, returning to Chrome while reading remains active, then stopping the reading and observing Chrome resume. The matching screenshots show `paused=true` and then `paused=false` with advancing playback time. A read-only status overlay reports the real HTMLVideoElement properties.

The emulator host audio sink was disabled, so this is native playback/focus and video-state evidence, not a listening test. This WebView exposes no `speechSynthesis` API; the unsupported browser path was checked for cleanup, but browser-engine speech was not exercised. Real synthesis services, physical-device speaker output, manufacturer variations, Android 24/25's legacy focus branch, and iOS remain unverified.
