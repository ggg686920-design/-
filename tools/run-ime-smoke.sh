#!/usr/bin/env bash
# Changes the selected keyboard. Use only on a disposable emulator/test profile.
set -euo pipefail
ime='app.nasma.keyboard/.NasmaIme'
mkdir -p app/build/runtime-diagnostics
collect() {
    adb pull /sdcard/Android/data/app.nasma.keyboard/files/ app/build/runtime-screenshots/ || true
    adb logcat -d -v threadtime AndroidRuntime:E '*:S' > app/build/runtime-diagnostics/crashes.txt || true
    adb shell dumpsys input_method > app/build/runtime-diagnostics/input-method.txt || true
    adb shell dumpsys meminfo app.nasma.keyboard > app/build/runtime-diagnostics/memory.txt || true
}
trap collect EXIT
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -W -n app.nasma.keyboard/.MainActivity
# API 23 may not have processed the package-added broadcast at install return.
for attempt in {1..20}; do
    methods=$(adb shell ime list -a -s)
    case "$methods" in *"$ime"*) break ;; esac
    sleep 1
done
case "$methods" in *"$ime"*) ;; *) printf 'Nasma was not registered as an IME\n' >&2; exit 1 ;; esac
adb shell ime enable "$ime"
adb shell ime set "$ime"
selected=$(adb shell settings get secure default_input_method | tr -d '\r')
test "$selected" = "$ime"
adb shell settings put secure show_ime_with_hard_keyboard 1
bash ./gradlew --no-daemon :app:connectedDebugAndroidTest
