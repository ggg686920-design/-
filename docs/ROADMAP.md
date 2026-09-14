# Roadmap

Recovery and build infrastructure are implemented; runtime baseline validation remains pending. Work in small, reviewable changes to control cost; do not start all phases at once.

| Stage | Deliverable | Exit evidence |
| --- | --- | --- |
| 0: recovered baseline | Source, Gradle/Wrapper, pinned dependencies, 13 tests and builds are ready; complete Android smoke tests | Build/JVM/lint verified; installation, typing and signing-compatible upgrade remain pending |
| 1: reliable core | Arabic/English, symbols, field types/actions, Shift/Caps, safe delete/composition, RTL/LTR, sensitive-field policy | Core and sensitive-field matrix passes on Android; no loss of existing behavior |
| 2: local suggestions | Completion, conservative correction/undo, local personal dictionary and deletion | Arabic/English unit/device tests; no learning in sensitive/incognito fields; measured latency |
| 3: customization | Themes, size, fonts, sounds, haptics, accessible previews | Settings survive restart; readable layouts in portrait/landscape; TalkBack smoke test |
| 4: emoji | Categories, search, recent items | Unicode insertion/deletion and supported-font tests; privacy rules verified |
| 5: optional local tools | Clipboard/pinning, long-press alternatives, delete/cursor gestures | Clipboard restrictions and sensitive-context tests; gesture false-positive tests |
| 6: separately approved advanced work | GIF/rich content, glide decoding, cloud AI | Provider/model feasibility, licensing, consent and privacy review; host compatibility and cancellation tests |
| 7: release polish | Performance, privacy publication, packaging, safe upgrades | Signed APK/AAB, tested upgrade path, current Play requirements review, accurate release notes |

## First useful AI task

Use the recovered source already in `app/src/`, run the documented build/tests, and perform Android smoke tests. Then implement a focused core fix (for example, a field-specific number/phone pad). Do not ask the owner for missing source or redo the completed recovery. Original-key signing is a release/upgrade limitation, not a source-development blocker.

## Completion rule

A task is complete only when the source is preserved, relevant checks are recorded honestly, the next AI can reproduce the work, and signing/install limitations are visible. A screenshot, a mock UI, a green archive check, or an APK without source does not meet this rule.
