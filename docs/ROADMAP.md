# Roadmap

All app-development phases below are pending. Work in small, reviewable changes to control cost; do not start all phases at once.

| Stage | Deliverable | Exit evidence |
| --- | --- | --- |
| 0: recover baseline | Obtain source, original build configuration, dependencies and signing plan; compare with archived APK | Reproducible baseline build; manifest/version comparison; installation and typing smoke-test report |
| 1: reliable core | Arabic/English, symbols, field types/actions, Shift/Caps, safe delete/composition, RTL/LTR, sensitive-field policy | Core and sensitive-field matrix passes on Android; no loss of existing behavior |
| 2: local suggestions | Completion, conservative correction/undo, local personal dictionary and deletion | Arabic/English unit/device tests; no learning in sensitive/incognito fields; measured latency |
| 3: customization | Themes, size, fonts, sounds, haptics, accessible previews | Settings survive restart; readable layouts in portrait/landscape; TalkBack smoke test |
| 4: emoji | Categories, search, recent items | Unicode insertion/deletion and supported-font tests; privacy rules verified |
| 5: optional local tools | Clipboard/pinning, long-press alternatives, delete/cursor gestures | Clipboard restrictions and sensitive-context tests; gesture false-positive tests |
| 6: separately approved advanced work | GIF/rich content, glide decoding, cloud AI | Provider/model feasibility, licensing, consent and privacy review; host compatibility and cancellation tests |
| 7: release polish | Performance, privacy publication, packaging, safe upgrades | Signed APK/AAB, tested upgrade path, current Play requirements review, accurate release notes |

## First useful AI task

Import and inspect the original source, then build it unchanged. Report actual architecture and current gaps before proposing a minimal first core improvement. If the source remains missing, ask the owner to choose a recovery/reconstruction approach; do not disguise a rewrite as an update.

## Completion rule

A task is complete only when the source is preserved, relevant checks are recorded honestly, the next AI can reproduce the work, and signing/install limitations are visible. A screenshot, a mock UI, a green archive check, or an APK without source does not meet this rule.
