# Recovery Provenance and Verification

## Input and authorization

On 2026-09-14 the owner authorized filling the missing project infrastructure and recovering the same Nasma app for another AI to continue. The input was their original APK, SHA-256 `b801b24a0694414830f47cfde3170a04672c361ab2ddcba2c70c1d0b5dd25895`. It remains unchanged in `artifacts/original/`.

JADX CLI 1.5.1 completed APK/resource decoding without errors:

```sh
jadx --show-bad-code --no-debug-info -d recovered nasma-keyboard.apk
```

The temporary decompiler output is not the canonical source. It can be regenerated from the preserved APK. A successful decompiler run alone did not make the result compilable: `NasmaIme.buildKeys` had invalid inferred boolean/integer expressions and synthetic method names.

## Recovered versus authored

- Reconstructed from decompiled logic: three Java classes (`NasmaIme`, `MainActivity`, `KeyLayout`), all keyboard rows/weights, original green/light/dark design, setup flow, Enter/Shift/delete behavior, language preference and resource definitions.
- Cleaned manually: descriptive variables, constants, valid Java types, normal listeners/lambdas, resource-based setup text, and compiler-generated classes omitted so Gradle regenerates them.
- Authored infrastructure: complete Gradle/Android Studio project, official pinned Wrapper, Kotlin support, JUnit tests, Android CI, this source map and handoff documentation.
- Focused authored behavior: Kotlin field-policy extraction plus a conservative future-personalization gate; repeat-state reset on cancel/editor finish; explicit inset request; Android 12+ data-transfer exclusions; truthful development-version notice.
- Preserved: package/namespace `app.nasma.keyboard`, `MainActivity`/`NasmaIme` class names, `settings` preferences and `arabic` key, original icon/theme, minSdk 23 and targetSdk 35.
- Changed build metadata: compileSdk now 35 rather than the original manifest's compile metadata 29; version 2 / `1.0.1-recovered`. Private signing identity is NOT preserved.
- No Room/DataStore migration, suggestions, AI, GIF, clipboard, model or dictionary was added. A fake implementation would undermine this handoff.

## Checks performed

- Standard build tasks succeeded with the documented ARM64/NFS host adaptations: debug APK, unsigned release APK, 13 JVM tests and Android lint with zero warnings/errors.
- Compiled the original decompiled `KeyLayout` independently, compared every combination of two languages, three modes, two Shift states and two symbol pages, plus seven key weights. All 24 states matched the reconstructed class.
- Deterministic layout-comparison stdout SHA-256: `f16c823372340f493c6a58ed32d31a01a46d885e902491c07ee46d2183b56a7f`. This verifies layout data/weights only, not all app behavior.
- That original snapshot is preserved in `app/src/test/resources/original-layouts.txt` and checked by the regular JVM golden test, so future AIs need not repeat decompilation.
- Verified Gradle Wrapper JAR and distribution checksums against official Gradle checksum endpoints.
- SDK `apksigner` verified archived APK v1/v2/v3 and rebuilt debug APK v1/v2 signatures. Signer identities differ; this is expected and disclosed.
- [Machine-readable local build results](verification.json) record APK hashes, sizes, test counts, and public signer fingerprints. Different build hosts/signers can produce different APK bytes.

## Not claimed

This is not the original author's literal source, a byte-for-byte reproduction of the APK, or a cryptographic recovery of the private key. No Android emulator/device was available for live typing, orientation, accessibility, performance or upgrade testing. Decompiled control flow and human cleanup can differ from runtime behavior; those checks must precede a production release.
