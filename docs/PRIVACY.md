# Privacy Design Checklist - Not a Published Policy

This file specifies intended development safeguards. The recovered source currently stores only language preference, has no text-processing/network sinks, and explicitly excludes app data from cloud/device transfer. These are source-level observations, not a full security/runtime audit or a Play-ready privacy policy.

## Local by default

- Minimize stored data. Personal vocabulary and clipboard features, when implemented, must be user-controlled, deletable, bounded where appropriate, and private to the app.
- Never persist passwords, sensitive-field contents, raw keystroke histories, crash-log text contents, or hidden analytics.
- Respect no-personalized-learning/incognito requests and conservative handling of unknown editor contexts.
- Do not include personal text in Android backup exports, debug logs, screenshots, issue reports or example datasets.
- No network permission is declared in the archived or reconstructed manifest. There are no networking, advertising, clipboard, learning or suggestion features in the recovered baseline. Re-review each future feature and validate behavior on devices.

## Cloud and GIF require separate review

- Document the provider, endpoints, exact data, purpose, retention, costs, API-key handling and opt-out controls.
- A cloud AI request requires a manual user action plus informed approval of the selected outgoing text. It must never be a hidden per-keystroke feature.
- Sensitive fields are excluded even if AI is enabled globally. Do not automatically read/send the whole host document or clipboard.
- GIF search may disclose search terms and IP information; clearly disclose this before use and assess licensing/attribution.

## Before publishing

The owner must approve a factual policy with operator identity/contact details, data categories, local vs external processing, permissions, retention/deletion, children-related obligations where applicable, and an effective date. Publish it at a stable public URL and match the Play Data safety disclosures to actual behavior. Do not invent a support email or assert regulatory compliance.
