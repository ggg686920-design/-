# Current Handoff

Updated: 2026-09-14. Recovery milestone: `1.0.1-recovered` / versionCode `2`.

## Owner stopped verification

- On 2026-09-14 the owner explicitly requested stopping further analysis/testing to conserve credit and publishing the current work. Do not resume device tests automatically.
- All source fixes and test infrastructure were published incrementally to `main`; latest source/test commit before this handoff is `f547ea6`.
- The current debug APK was built successfully before the stop request and supplied separately to the owner. It is debug-signed, not release-signed, and cannot be assumed to update the original installation.
- Local debug build, 13 JVM tests, lint and test-APK compilation passed before stopping. No further checks were run after the stop request. Active GitHub checks were sent cancellation requests.
- **Stage-one runtime acceptance is NOT complete.** The first API 35 run passed the reopen/recreate/rotation smoke but failed four visibility checks; API 23 activation failed. Later runs did not establish a passing matrix before the owner stopped verification. Do not label this app fully device-validated or leak-free.
- Remaining review items: joined-emoji deletion currently deletes one code point on API 24+; raw-key-only `TYPE_NULL` editors need compatibility validation; no heap/battery/TalkBack or physical-device verification is claimed. Email/URI use the existing Latin layout with `@` on the symbols page, not a new dedicated field layout.
- No network permission, typed-text storage or future processing feature was added. The Kotlin password/incognito policy remains unchanged and must gate every future processing entry point.

## Ready to continue

- This repository now contains a buildable Android Studio/Gradle project. **Do not ask for source or repeat APK recovery. Start in `app/src/`.**
- The owner authorized recovering the same app from APK and filling missing build/source infrastructure. Identity remains `app.nasma.keyboard`; minSdk 23, target/compile SDK 35.
- Recovered and cleaned: `NasmaIme.java`, `MainActivity.java`, `KeyLayout.java`, icon/theme/method resources. Added: Gradle project/Wrapper, extracted UI strings, Kotlin `EditorPolicy`, privacy backup rules, tests and CI.
- Original APK remains unchanged under `artifacts/original/`. Recovered source is not claimed byte-identical to the creator's lost source. Details: `docs/RECOVERY.md`.

## Verified locally

- First emulator run `34813419597`: API 35 passed the repeated reopen/recreate/rotation test but four tests missed IME visibility; API 23 failed IME activation before its five tests. This is not a passing runtime baseline. Harden test window-focus/activation synchronization and retain screenshots/diagnostics on failure before attributing failures to production code.
- Added isolated Android instrumentation smoke tests and an API 23/35 emulator workflow. Test APK compilation and build/JVM/lint checks pass; device results are pending, not claimed from compilation.
- Stage-one recheck (2026-09-14): debug build, 13 JVM tests and lint pass on ARM64 with the documented host flags. Standard invocation hits the documented NFS locking limitation.
- Existing GitHub run `34809010121` failed only on `OldTargetApi`. That advisory is now informational (not hidden); all other lint warnings remain errors. SDK levels are unchanged. New CI/runtime results are pending.
- JDK 17, Gradle 8.11.1, AGP 8.9.2, Kotlin 2.1.20, SDK/build-tools 35.
- `:app:assembleDebug`, `:app:assembleRelease`, `:app:testDebugUnitTest`, and `:app:lintDebug` pass. Release output is unsigned by design. Lint: no warnings/errors; 13 JVM tests pass.
- All 24 layout combinations and seven key weights match the decompiled original. The original and rebuilt debug APK signatures were checked with SDK `apksigner`.
- Host is ARM64 with a network filesystem; local build needed an external project-cache directory and a QEMU AAPT2 wrapper. These host-only flags are documented in `docs/BUILDING.md`, not embedded in project configuration. CI uses ordinary x86-64 tooling.
- **Not run:** Android installation, live IME typing, gesture timing, rotation, TalkBack, performance/battery tests, and upgrade testing. Build success is not runtime validation.

## Current behavior and limitations

- Correct the Previous editor-action label (previously displayed newline despite dispatching Previous); the runtime matrix now also checks custom action labels/IDs and newline precedence.
- Track the initiating delete pointer and cancel repetition when it lifts, even if another finger stays on the same key. Added a two-pointer native MotionEvent regression; system-bound hold/release remains in the smoke matrix.
- Close a repeat-delete cleanup gap in `onUnbindInput`; a native-view regression checks the Handler queue and held-button reference. This is lifecycle hardening, not a measured claim of zero memory leaks.
- Removed recovered tap haptics and disabled framework long-press haptics on all IME keys, as required for this stage. Added native-view regression coverage; physical vibration is not measurable on the emulator.
- Arabic/English, symbols, extra Arabic characters/diacritics, Shift/Caps, editor-action Enter, repeat-delete, language preference and system light/dark keyboard colors are reconstructed.
- No suggestions, autocorrect, learned dictionary, emoji/GIF browser, clipboard manager, glide typing, custom themes, cloud AI or analytics. No INTERNET permission. Only the language preference is persisted.
- Number/phone/date fields currently open the existing symbol page, not a dedicated optimized number pad. Arabic subtype metadata retains the original single combined Arabic/ASCII-capable subtype.
- `EditorPolicy.permitsPersonalization` is tested but reserved for future feature entry points. There are currently no suggestion, learning or network sinks to guard.
- The app keeps the original compact key sizes; accessibility and mixed-text deletion need Android-device review. Do not claim Gboard parity.

## Signing is not a development blocker

- The original private signing key is still unavailable and cannot be recovered from APK. Do not invent it, commit a substitute, or silently change applicationId.
- Debug builds use the local/CI Android debug key; certificates vary across environments. They do not update the original installed APK in place. Use a test device/profile with no conflicting installation; do not uninstall the owner's copy automatically.
- Release builds remain unsigned until the owner configures signing securely. Source development, unit tests, lint and builds do not require that original key.

## Next small task

1. Wait for the owner's APK installation feedback or explicit approval to resume validation.
2. If approved, inspect the saved runtime reports and finish the existing matrix before adding features. Preserve the current Java/Kotlin split and privacy policy.
