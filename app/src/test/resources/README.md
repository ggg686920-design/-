# Original Layout Fixture

`original-layouts.txt` is generated from the original APK's decompiled `KeyLayout`, not from the reconstructed production source. It records `Arrays.deepToString(rows)` for Arabic=false/true, mode=0/1/2, shift=false/true and page=false/true in that order, each followed by LF, then the seven original key weights for space/gap/enter/shift/symbol/back/a.

SHA-256: `f16c823372340f493c6a58ed32d31a01a46d885e902491c07ee46d2183b56a7f`.

The golden test makes this one-time recovery comparison repeatable without decompilation. Update it only for deliberate, reviewed layout changes and describe the behavioral difference; never regenerate it just to silence a regression.
