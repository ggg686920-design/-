# Test Matrix

**Runtime acceptance is incomplete; testing was stopped at the owner's request on 2026-09-14.** The first real emulator run (`34813419597`, commit `756113c`) passed the API 35 reopen/recreate/rotation smoke but failed four IME-visibility checks; API 23 activation failed. Follow-up test synchronization and diagnostics were published, but no passing full matrix was established before stopping. See `HANDOFF.md`; do not automatically resume tests.

Before stopping, the current code passed local debug build, 13 JVM tests (8 editor-policy and 5 layout tests), Android lint and compilation of the separate instrumentation APK. This is not equivalent to passing those instrumented tests. Record future runtime results with date, device/API, build commit, exact steps, and pass/fail.

## Source checks: executable now

```sh
bash ./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleRelease
```

Use `gradlew.bat` on Windows. SDK setup and special-host flags are in `BUILDING.md`. See `RECOVERY.md` for the independent 24-state layout comparison and signing checks. `tools/run-ime-smoke.sh` runs the existing instrumentation suite and changes the selected IME: use it only on a disposable test device/profile, after owner approval to resume.

## Reference archive: executable now

```sh
python3 tools/inspect_apk.py artifacts/original/nasma-keyboard.apk --expected-sha256 b801b24a0694414830f47cfde3170a04672c361ab2ddcba2c70c1d0b5dd25895
```

Expected: valid ZIP CRC, the exact SHA-256, and readable manifest metadata. A mismatched checksum must fail. This is not signature verification, app compilation, malware scanning, or a runtime privacy test.

## Android core and lifecycle

- Enable/select IME from settings; switch to/from another keyboard; do not trap the user.
- Arabic and English words, mixed-direction sentences, punctuation, numbers, symbols, Shift/Caps Lock, and language switching.
- Plain/multiline text, email/URI, phone, signed/decimal number, null/custom input connections, and search/send/done/go/next actions.
- Selection replacement, cursor movement outside the composing word, rapid typing, composition finish/cancel, and undo correction. No duplicated or dropped text.
- Delete taps/hold/swipe; release/cancel/outside-pointer events stop repeat deletion. Correctly handle surrogate pairs, joined emoji and Arabic combining marks without splitting unintended units.
- App/field switches, input restart, process recreation, rotation, landscape, split-screen, font scaling, screen insets and system navigation. No stale editor callbacks or leaked views/services.
- Test the actual supported minimum and a current Android version. The archived app declares API 23; the requested future baseline is API 24. Verify any compatibility change explicitly.

## Sensitive-field regression suite

- `TYPE_CLASS_TEXT` with `TYPE_TEXT_VARIATION_PASSWORD`, `TYPE_TEXT_VARIATION_VISIBLE_PASSWORD`, or `TYPE_TEXT_VARIATION_WEB_PASSWORD`.
- `TYPE_CLASS_NUMBER` with `TYPE_NUMBER_VARIATION_PASSWORD`.
- `EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING`, app incognito requests, `TYPE_TEXT_FLAG_NO_SUGGESTIONS`, autocomplete fields, and unknown/null field types; use conservative fallbacks and distinguish privacy restrictions from ordinary UI hints.
- Switch normal -> sensitive -> normal while suggestion/learning/AI jobs are pending. No stale results, text, previews or clipboard contents leak into the new editor.
- Passwords never reach suggestion models, learned dictionaries, logs, telemetry, cloud requests or clipboard history. Disable character previews and sensitive-data panels appropriately.
- Use synthetic sentinel strings, then inspect test storage/logs/network to prove exclusion. Never use real passwords or publish typed text. Do not claim every custom host field can be identified perfectly; document platform limitations.

## Suggestions, storage, and customization

- Arabic and English completion, common misspellings, names and unknown words; conservative correction with undo. Disable suggestions/learning and confirm the setting works.
- Language change, cursor edits, rapid input and stale async responses. Suggestion commits replace only the intended composing segment.
- Personal dictionary add/list/delete/reset; storage migration without data loss. No sensitive training or backups of user text.
- Theme contrast, image permission loss, height/font boundaries, Arabic glyph shaping, landscape touch targets, TalkBack labels and traversal.
- Sound/haptic toggles and system restrictions. Settings persist correctly; main-thread work does not block keystrokes.

## Optional features before claiming completion

- Clipboard: opt-in, permitted foreground/default-IME access, sensitive-clip flag, bounded retention, pin/unpin, deletion and sensitive/incognito suppression across supported API levels.
- Emoji: Unicode sequences, flags, skin tones, search and recents privacy; sensible deletion and fallback display.
- GIF: provider licensing/keys, MIME negotiation, `commitContent` support, temporary URI grants, unsupported-host fallback, and no unsolicited network calls.
- Cloud AI: explicit outgoing-text preview/consent, sensitive-field exclusion, no default background transmission, cancellation, timeout/rate-limit/error handling, stale editor detection, and review before insertion. No API secrets in build outputs.
- Glide/gestures: bilingual accuracy against test words, false-trigger rate, accessibility alternative, deletion/cursor safety.

## Release and performance

- Verify APK signatures and test upgrade from the actual prior signer/version with user settings retained. Never uninstall automatically to bypass an upgrade failure.
- Record startup latency, typing responsiveness, memory and idle battery behavior under repeatable conditions. Choose measured budgets after baseline profiling, not invented benchmark numbers.
- Review manifest permissions, backups, exported components, logs, offline/network behavior, privacy wording and current distribution requirements.
