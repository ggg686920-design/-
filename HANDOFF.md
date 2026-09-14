# Current Handoff

Updated: 2026-09-14. Recovery milestone: `1.0.1-recovered` / versionCode `2`.

## Ready to continue

- This repository now contains a buildable Android Studio/Gradle project. **Do not ask for source or repeat APK recovery. Start in `app/src/`.**
- The owner authorized recovering the same app from APK and filling missing build/source infrastructure. Identity remains `app.nasma.keyboard`; minSdk 23, target/compile SDK 35.
- Recovered and cleaned: `NasmaIme.java`, `MainActivity.java`, `KeyLayout.java`, icon/theme/method resources. Added: Gradle project/Wrapper, extracted UI strings, Kotlin `EditorPolicy`, privacy backup rules, tests and CI.
- Original APK remains unchanged under `artifacts/original/`. Recovered source is not claimed byte-identical to the creator's lost source. Details: `docs/RECOVERY.md`.

## Verified locally

- JDK 17, Gradle 8.11.1, AGP 8.9.2, Kotlin 2.1.20, SDK/build-tools 35.
- `:app:assembleDebug`, `:app:assembleRelease`, `:app:testDebugUnitTest`, and `:app:lintDebug` pass. Release output is unsigned by design. Lint: no warnings/errors; 13 JVM tests pass.
- All 24 layout combinations and seven key weights match the decompiled original. The original and rebuilt debug APK signatures were checked with SDK `apksigner`.
- Host is ARM64 with a network filesystem; local build needed an external project-cache directory and a QEMU AAPT2 wrapper. These host-only flags are documented in `docs/BUILDING.md`, not embedded in project configuration. CI uses ordinary x86-64 tooling.
- **Not run:** Android installation, live IME typing, gesture timing, rotation, TalkBack, performance/battery tests, and upgrade testing. Build success is not runtime validation.

## Current behavior and limitations

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

1. Read `AGENTS.md`, `docs/ARCHITECTURE.md` and `docs/BUILDING.md`; run the standard build/tests unchanged.
2. Perform the Android core/sensitive-field smoke matrix in `docs/TESTING.md`, record device/API and real outcomes, and fix the first reproducible issue.
3. Prioritize a dedicated number/phone layout or local suggestions after runtime baseline validation; follow `docs/ROADMAP.md`, one focused change at a time.
4. Update this file and `CHANGELOG.md`, preserve source, and provide actual test results with every handoff.
