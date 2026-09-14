# Nasma Development Specification

Source: the owner's Arabic requirements provided on 2026-09-14, summarized here for implementation. This is a backlog, not a feature-completion report. The owner subsequently emphasized low AI cost and continuity of the existing app.

## Platform and architecture

- Android native; prefer Kotlin for new development and `InputMethodService` for the IME. No web keyboard substitute.
- Requested minimum for future development: API 24 / Android 7. The archived APK actually declares minSdk 23 and targetSdk 35. Discuss the compatibility impact before dropping Android 6 support; do not silently change SDK levels.
- Keep the IME bound with `android.permission.BIND_INPUT_METHOD`, an `android.view.InputMethod` intent filter, and `android.view.im` metadata pointing to its method XML.
- Separate settings Activity with enable/default-IME guidance, dictionary management, customization, and privacy information.
- Proposed modules/packages: `ime`, `keyboard/layouts`, `keyboard/views`, `keyboard/gestures`, `suggestions`, `ai`, `themes`, `emoji`, `settings`, `data`, `utils`. Adopt only as justified by recovered code; do not create empty architecture scaffolding for appearances.
- Prefer Room for personal vocabulary and learned words, DataStore for preferences, and bundled licensed Arabic/English word lists in assets. If migrating existing storage, preserve user data and test migrations.

## 1. Core typing: highest priority

- Arabic/English toggle by key and a documented gesture, separate digits/symbol pages (`123`, `#+=`), Arabic `، ؛ ؟` and English punctuation.
- Space, Enter/editor actions, repeat-delete on hold, English Shift and Caps Lock.
- Honor email/URI/phone/numeric/decimal/signed field types and search/send/done/go/next actions. Do not call editor actions when newline behavior is required.
- Correct RTL/LTR presentation and mixed Arabic/English text without inserting arbitrary bidi control characters or forcing the host app's direction.
- Reliable composition, selection/cursor changes, app switches, rotation, and landscape layout. TalkBack labels and usable touch targets.

## 2. Local suggestions and correction

- At least three suggestion slots where enough candidates exist; do not invent candidates to fill them.
- Separate Arabic/English completion and common-typo correction, with user controls and undo for an unwanted correction.
- Learn repeated user words/names locally only in eligible fields; allow viewing/deleting learned words and disabling/resetting learning.
- Next-word prediction from a documented small local language model or n-gram approach. Do not present a short fixed dictionary as a high-quality AI model.
- Keep model/dictionary license, provenance, size, memory, and load latency documented.

## 3. AI: local first, cloud optional

- Everyday suggestions should work offline. Select a verified Arabic/English-capable approach before adding dependencies; generic ML Kit is not automatically a bilingual keyboard prediction/autocorrect engine.
- Cloud rewriting, correction, summarization, tone, and translation are later opt-in capabilities, not automatic per-keystroke transmission.
- Require an explicit user action, preview of the exact outgoing text, informed consent, cancellation, and review before replacing text.
- Never send passwords or sensitive fields. Never embed provider secrets in the APK. Document provider, retention, cost, network behavior, and error handling before enabling any cloud feature.
- Default offline/no networking for newly implemented behavior unless separately agreed. No hidden analytics or typing telemetry.

## 4. Themes and customization

- Light/dark and several colors, optional user-selected background image with appropriate photo-picker handling.
- Key/text colors, corner style, height, key label size, and Arabic/English font options using appropriately licensed fonts.
- Preserve contrast and accessibility; handle revoked image access and corrupt/missing assets without crashing.

## 5. Emoji, GIF, stickers

- Categorized emoji, search, and recent items stored locally in eligible contexts.
- GIF integration later with a verified provider/API contract, key provisioning, privacy disclosure, attribution/licensing, and explicit network use. Do not assume Tenor/Giphy access is free or available indefinitely.
- Use Android rich-content insertion/MIME negotiation when supported; provide an honest fallback for apps that cannot accept images. Stickers are optional later work.

## 6. Gestures and feedback

- Long-press alternatives, including accented English letters and Arabic diacritics.
- Swipe left from delete removes a word; a space-key gesture enters cursor movement. Define thresholds so ordinary typing is not mistaken for a gesture.
- Glide typing is an advanced separately evaluated feature requiring path decoding and a language model, not a cosmetic swipe trail.
- Optional key sounds/volume, haptics/intensity where supported, and key preview. Respect system accessibility, sound, and haptic settings. Never preview sensitive characters over password fields.

## 7. Clipboard and settings

- Optional clipboard history, pinned entries, per-entry deletion, clear-all, and a retention policy. Capture only within Android's permitted IME/focus behavior, not unrestricted background monitoring.
- Off by default for collection; exclude sensitive clipboard flags and password/incognito contexts. Never send clipboard data to AI implicitly. Test Android-version-specific clipboard access restrictions.
- Settings: enable/select IME, languages, suggestions/learning, themes, audio/haptics, clipboard, optional AI, dictionary management, and privacy.

## Non-negotiable privacy and quality

- Exclude password variants and sensitive contexts from suggestions, learning, logging, clipboard capture, and AI. Honor `IME_FLAG_NO_PERSONALIZED_LEARNING` and appropriate field flags. See `TESTING.md` for exact scenarios.
- Minimize permissions and storage. Publish an accurate privacy policy before a Play release; `PRIVACY.md` is a design checklist, not a published compliance claim.
- Prioritize typing latency, startup, battery, memory, APK size, orientation, accessibility, and physical-keyboard compatibility where feasible.
- Functional parity with Gboard/SwiftKey is an aspiration requiring substantial development and device testing, not an acceptance claim for a single task.
