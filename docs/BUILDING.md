# Source, Builds, and Signing

## Current status

There is no Android source or Gradle project here yet. `./gradlew assembleDebug` is NOT currently a valid command in this repository. Do not install a toolchain or guess dependency versions merely to create the appearance of a working project.

The preserved manifest declares package `app.nasma.keyboard`, version `1.0` / code `1`, minSdk `23`, and targetSdk `35`. These are static declarations, not proof of runtime compatibility or current Play eligibility. See `artifacts/original/inspection.json`.

## Recover once

Ask the original creator for:

- Kotlin/Java source, complete resources, manifest, method XML, assets, and any tests.
- Root/module Gradle files, `settings.gradle` or `.kts`, safe `gradle.properties`, version catalogs/lock files if used.
- Gradle Wrapper scripts, JAR, and properties with an official distribution URL and checksum where supported.
- Exact JDK, Gradle, Android Gradle Plugin, Kotlin, compile/min/target SDK, and dependency versions.
- Instructions and provenance/licenses for dictionaries, fonts, images, and models.

Do not commit `local.properties`, signing configuration containing secrets, private keys, or user data. Inspect received wrapper/build scripts before executing them. Do not overwrite existing documentation or the preserved APK during import.

If source is unavailable, agree explicitly on a recovery/reconstruction effort. Decompiled source is not guaranteed equivalent or compilable. Record tool versions, recovered-vs-authored files, behavior differences, and unresolved resources. Never claim to recover the original private key from the APK.

## Establish a verified build

After the real source arrives, replace this section with commands that actually succeed. Record prerequisites, the exact module/task, test commands, produced APK path, checksum, and signer fingerprint. Do not invent module names. Run a baseline build before introducing feature changes.

Then add Android CI using the same commands. Until then the existing workflow checks only reference-APK integrity. Keep dependency versions pinned, avoid unreviewed upgrades, and include the Wrapper in source exports. Avoid committing generated `build/` directories and caches.

## Keep future APKs installable as updates

- Preserve `applicationId` and use the original signing identity. Increase `versionCode` for releases.
- The original APK includes signature-related files, but its signer has not yet been cryptographically verified here. Use Android build tools' `apksigner verify --verbose --print-certs` when available to inspect public certificate information.
- Possession of a signing certificate or signed APK does not provide the private signing key.
- The owner must retain the release keystore and passwords securely outside the repository. Do not ask them to paste secrets in chat. Prefer signing locally or via a deliberately configured secrets-backed release process.
- Debug keys differ across machines/tools. A newly generated debug key usually cannot update another tool's installed debug APK. Do not silently replace it and recommend uninstalling as if nothing changed.
- If the original key is lost, explain update limitations and potential data loss before installing an independently signed rebuild. Do not change package name without approval.
- For Google Play App Signing, distinguish app signing key from upload key; use Play's supported procedures rather than assuming local sideload signing and Play signing are interchangeable.

## Release handoff checklist

- Source commit/tag, matching source ZIP, change log, and tested APK/AAB where actually built.
- Exact build commands, supported Android versions, test results and known limitations.
- SHA-256 and public signing certificate fingerprint, with a clear install/upgrade path.
- No private keys, tokens, copied user text, dictionaries learned from real users, or clipboard history in source, APK assets, screenshots, or logs.
- Do not mark a build "production ready" before device verification and privacy/release review.
