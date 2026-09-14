# Privacy Design Checklist - Not a Published Policy

This file specifies intended development safeguards. The original binary has not undergone a full security/runtime audit, and this text is not a verified statement of its behavior or a Play-ready privacy policy.

## Local by default

- Minimize stored data. Personal vocabulary and clipboard features, when implemented, must be user-controlled, deletable, bounded where appropriate, and private to the app.
- Never persist passwords, sensitive-field contents, raw keystroke histories, crash-log text contents, or hidden analytics.
- Respect no-personalized-learning/incognito requests and conservative handling of unknown editor contexts.
- Do not include personal text in Android backup exports, debug logs, screenshots, issue reports or example datasets.
- No network permission is declared in the archived manifest inspection. This observation is narrower than proof of runtime privacy; re-review the actual manifest and code after source recovery.

## Cloud and GIF require separate review

- Document the provider, endpoints, exact data, purpose, retention, costs, API-key handling and opt-out controls.
- A cloud AI request requires a manual user action plus informed approval of the selected outgoing text. It must never be a hidden per-keystroke feature.
- Sensitive fields are excluded even if AI is enabled globally. Do not automatically read/send the whole host document or clipboard.
- GIF search may disclose search terms and IP information; clearly disclose this before use and assess licensing/attribution.

## Before publishing

The owner must approve a factual policy with operator identity/contact details, data categories, local vs external processing, permissions, retention/deletion, children-related obligations where applicable, and an effective date. Publish it at a stable public URL and match the Play Data safety disclosures to actual behavior. Do not invent a support email or assert regulatory compliance.
