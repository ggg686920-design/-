# Source Map

This recovery preserves the small original architecture instead of introducing empty Clean Architecture layers. Native Android Views and Java host the recovered behavior; Kotlin is configured for focused new code.

| File | Responsibility |
| --- | --- |
| `app/src/main/AndroidManifest.xml` | Launcher Activity, permission-protected IME, no network permission, backup restrictions |
| `app/src/main/java/app/nasma/keyboard/NasmaIme.java` | IME lifecycle, row rendering, key dispatch, Shift, Enter, delete repeat |
| `app/src/main/java/app/nasma/keyboard/KeyLayout.java` | Pure Arabic/English/extra/symbol layout data and key widths |
| `app/src/main/java/app/nasma/keyboard/MainActivity.java` | Enable/select keyboard, non-persisted typing sandbox, usage/privacy text |
| `app/src/main/java/app/nasma/keyboard/EditorPolicy.kt` | Pure field classification, existing layout routing and future personalization eligibility |
| `app/src/main/res/xml/method.xml` | Original combined Arabic/ASCII-capable input subtype and settings Activity |
| `app/src/main/res/xml/data_extraction_rules.xml` | Cloud/device-transfer exclusions for app data |
| `app/src/main/res/values/strings.xml` | Extracted setup-screen text, ready for localization |
| `app/src/test/java/app/nasma/keyboard/` | JVM layout and editor-policy regression tests |
| `.github/workflows/android-build.yml` | Build debug/unsigned release, JVM tests, lint, temporary test artifacts |

## State and flow

`onStartInput` resets transient mode/Shift/repeat state, reads only `settings/arabic`, then classifies the editor. `onStartInputView` builds native rows. Buttons commit text or perform editor actions through the active `InputConnection`. Finish/cancel/destroy paths stop repeat-delete callbacks. System night mode affects key colors. The current implementation does not keep a composing word or text history.

## Safe extension points

- Number-pad improvement: `KeyLayout` plus `EditorPolicy.usesSymbols`/IME routing; add field-aware tests before altering layout behavior.
- Suggestions: add a Kotlin engine behind `EditorPolicy.permitsPersonalization`, then a suggestion strip and carefully owned composing range. No processing in sensitive/unknown/incognito fields; cancel stale results on editor/selection changes. The existing demo field opts out of suggestions, so use synthetic dedicated test fields.
- Settings/themes: retain `settings/arabic` compatibility. If adopting DataStore/Room later, implement and test a real migration instead of dropping stored settings.
- Touch improvements: keep normal Android Button accessibility semantics and repeat cancellation. Validate gestures and touch-target changes on Android before replacing working key input.
- Advanced cloud/GIF features: separate opt-in and privacy review. Do not add network permission merely because a backlog mentions AI.
