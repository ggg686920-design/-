# Changelog

## Unreleased - Stage-one verification (2026-09-14)

- Stop repeat-delete when its initiating finger lifts during a two-finger press; suppress an unintended final release deletion.
- Cancel queued repeat deletion and clear held-key state when the IME's client input binding is removed.
- Remove the recovered key-tap vibration request and disable framework long-press haptics. Keyboard keys stay silent without adding settings or features.
- Add a separate test APK and real-IME emulator workflow for API 23/35: bilingual typing, Shift/Caps, symbols/diacritics, deletion, field routing/actions, sensitive fields, reopening and rotation. Runtime results pending.
- Keep the deliberately pinned target SDK 35 age advisory informational so CI can validate the recovered baseline without an untested SDK migration. Other lint warnings still fail the build.
- Re-run debug build, 13 JVM tests and lint successfully on the documented ARM64/NFS host setup. Android runtime validation remains pending.

## 1.0.1-recovered (versionCode 2) - 2026-09-14

- Restore a buildable native project from the owner's APK while preserving package identity, core layouts and visual design.
- Clean recovered Java IME/setup/layout code; add Kotlin field policy, resource-based setup text and explicit backup/transfer restrictions.
- Add pinned Gradle/Android/Kotlin configuration, official Wrapper, 13 passing JVM tests and a build/test/lint CI workflow.
- Verify all 24 layout states against recovered original data, debug/unsigned-release builds, zero-warning lint and APK signatures.
- Signing identity is not the original; debug builds are test-only and release is unsigned. Android runtime tests remain pending; no suggestions/AI/GIF/clipboard features are claimed.

## 2026-09-14 - Project handoff baseline

- Preserve the original Nasma APK and record static metadata and its checksum.
- Add cross-AI instructions, requirements, a staged roadmap, privacy safeguards, and build/signing/test guidance.
- Add an archive integrity inspection tool and CI check; these do not build or run Android.
- No app features changed; no new app version released. Original source and signing continuity remain unresolved.
