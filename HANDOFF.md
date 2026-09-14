# Current Handoff

Updated: 2026-09-14

## Actual state

- Owner's goal: continue developing the same Nasma native Arabic/English keyboard with different AI tools, without losing source or restarting each time.
- Available: the original user-supplied `nasma-keyboard.apk`, preserved at `artifacts/original/nasma-keyboard.apk`, plus its static inspection report.
- Missing: original Kotlin/Java source, Android Studio/Gradle project, dependency lock/version information, private signing key, and a verified build recipe.
- No application feature changes were made. No new APK was built. No Android install, emulator, typing, accessibility, or performance test has been performed.
- A separate replacement app approach was stopped at the owner's request. Do not resume it without explicit approval.
- This repository was created empty by the owner. This handoff package establishes its initial project record, not an app implementation.

## Evidence and checks

- `artifacts/original/inspection.json` records the APK checksum, size, manifest declarations, and Nasma DEX class descriptors.
- Archive CRC and binary-XML inspection pass for the preserved file. These checks do not verify a valid APK signing certificate or runtime behavior.
- `tools/inspect_apk.py` uses only Python's standard library. The archive-check workflow must not be presented as Android build/test CI.
- Requested features are documented in `docs/SPEC.md`, not marked implemented. No cloud/API service is configured.

## Next action

1. Obtain the original Android Studio source ZIP from the tool/session that built Nasma, excluding secrets. Preserve this APK separately as the baseline.
2. If the source cannot be obtained, agree with the owner on a one-time recovery or reconstruction, and explain the signing/update implications before changing the app.
3. Once source exists: inventory its real architecture, compare package/version/signing details, document its actual build command, build a baseline, and run Android smoke tests before implementing features.
4. Implement the first incomplete core feature with regression coverage; update this file after verification.

## Continuity

- Confirm `applicationId` from the manifest/build configuration, not from Kotlin/Java package names alone.
- Keep signing secrets outside Git. A new debug/release key does not replace the original signer for updates.
- Never delete an installed user's app or data as an automatic troubleshooting step.
- Read `AGENTS.md` for privacy rules, small-scope work, and the mandatory AI handoff.
