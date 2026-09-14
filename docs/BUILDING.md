# Build and Signing

## Standard setup

Open the repository root in a compatible Android Studio, or install JDK 17 and Android SDK packages `platforms;android-35` and `build-tools;35.0.0`. Configure `ANDROID_HOME` or an untracked `local.properties` with your SDK path. Accept the SDK licenses as required. Dependency downloads require internet the first time; the app itself has no INTERNET permission or API-key requirement.

Pinned project versions: Gradle 8.11.1, Android Gradle Plugin 8.9.2, Kotlin 2.1.20, JUnit 4.13.2, compile/target SDK 35, build-tools 35.0.0, minSdk 23, JVM toolchain 17. The module is `:app`.

```sh
bash ./gradlew --no-daemon :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
bash ./gradlew --no-daemon :app:assembleRelease
```

Windows: replace `bash ./gradlew` with `gradlew.bat`. Using `bash` avoids executable-mode differences in downloaded ZIPs and API-created Git files. These are the real module/tasks, not placeholders.

## Outputs

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`, debug-signed for testing.
- Release APK: `app/build/outputs/apk/release/app-release-unsigned.apk`, intentionally unsigned and not installable until signed.
- Unit-test report: `app/build/reports/tests/testDebugUnitTest/index.html`.
- Lint results: `app/build/reports/lint-results-debug.html` / `.txt` when a report is emitted. Lint warnings are treated as errors.
- GitHub Actions' Android build uploads APKs and reports for seven days. Download them from a successful run's Artifacts section while signed in; rebuild after expiration. They are not production releases.

## Wrapper provenance

Wrapper scripts/JAR are from Gradle's official `v8.11.1` tag. Distribution SHA-256 is pinned in `gradle/wrapper/gradle-wrapper.properties`.

- Wrapper JAR SHA-256: `2db75c40782f5e8ba1fc278a5574bab070adccb2d21ca5a6e5ed840888448046`.
- Gradle ZIP SHA-256: `f397b287023acdba1e9f6fc5ea72d22dd63669d59ed4a289a29b1a76eee151c6`.
- Keep the Wrapper JAR tracked; never blanket-ignore all JAR files. Review wrapper/build changes before executing them.

## Special hosts, not project dependencies

The recovery worker used Linux ARM64 on NFS. Standard Google Linux AAPT2 binaries are x86-64; Debian's older native AAPT2 lacked required AGP flags. We ran SDK 35 AAPT2 using QEMU's `qemu-x86_64` and installed its x86 runtime libraries. Gradle's project cache was placed on local temporary storage because NFS locking failed.

The verified local command was:

```sh
ANDROID_HOME=/tmp/bcode/android-sdk GRADLE_USER_HOME=/tmp/bcode/gradle-home \
  bash ./gradlew --no-daemon --project-cache-dir /tmp/bcode/nasma-project-cache \
  -Pandroid.aapt2FromMavenOverride=/tmp/bcode/aapt2 \
  :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:assembleRelease
```

Those paths are ephemeral host setup, NOT included tools. The local executable wrapper simply invoked `qemu-x86_64 <SDK>/build-tools/35.0.0/aapt2 "$@"`. Recreate suitable paths only if your ARM64/NFS environment needs it. Ordinary x86-64 CI/desktop environments should use the standard commands above, without these overrides. Do not downgrade AGP or commit machine paths to work around an unsupported host.

## Signing and safe updates

- Stable applicationId: `app.nasma.keyboard`. Current versionCode `2`, versionName `1.0.1-recovered`. Future releases should increase versionCode.
- Original signer certificate SHA-256: `97a4f87dd77a86869b23b39ac822ca5eb3f60f7223aae26ecb2ded30eb22cdbc`. SDK 35 `apksigner` verified the archived APK's v1/v2/v3 signatures.
- The original private key is unavailable. Certificate/public-key information is not a private signing key. Recovery does not make the new APK an in-place update to the original.
- Debug keys are for development and differ between workers/CI runs. Do not promise cross-environment debug updates or auto-delete installed app data to resolve a signing conflict.
- Keep release keys and passwords outside Git, logs and chat. The owner must configure signing separately through a secure local/CI process; release remains unsigned until then. Do not ship an embedded signing secret or invent a fake `signing.properties`.
- When signing is configured, use `apksigner verify --verbose --print-certs` to confirm the public certificate, then test the actual upgrade path. For Play App Signing distinguish the app signing key from the upload key.
- APK/AAB signing, Play policy approval, published privacy policy and on-device validation remain release tasks, not prerequisites for source development.
