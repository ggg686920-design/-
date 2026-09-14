package app.nasma.keyboard;

import java.util.Locale;

/** Layouts recovered from the owner's 1.0 APK, with descriptive names restored. */
final class KeyLayout {
    static final String BACK = "{back}";
    static final String ENTER = "{enter}";
    static final String EXTRA = "{extra}";
    static final String GAP = "{gap}";
    static final String LANG = "{lang}";
    static final String PAGE = "{page}";
    static final String SHIFT = "{shift}";
    static final String SPACE = "{space}";
    static final String SYMBOL = "{symbol}";

    private KeyLayout() { }

    static String[][] rows(boolean arabic, int mode, boolean uppercase, boolean secondPage) {
        String bottom = "{symbol} {lang}" + (arabic ? " {extra} ، " : " , ") + "{space} . {enter}";
        if (mode == 1) {
            return split("1 2 3 4 5 6 7 8 9 0",
                    secondPage ? "[ ] { } # % ^ * + =" : "@ # $ % & * - + ( )",
                    secondPage ? "١ ٢ ٣ ٤ ٥ ٦ ٧ ٨ ٩ ٠" : "! \" ' : ; / ? _ = ~",
                    PAGE + (secondPage ? " < > | ~ ` € £ ¥ " : " ، ؛ ؟ … / \\ : ") + BACK,
                    bottom);
        }
        if (arabic && mode == 2) {
            return split("1 2 3 4 5 6 7 8 9 0", "أ إ آ ٱ ء ؤ ئ ة ى ا",
                    "َ ً ُ ٌ ِ ٍ ْ ّ ٰ ـ", "پ چ ژ گ ڤ ک ی ؛ ؟ {back}", bottom);
        }
        if (arabic) {
            return split("1 2 3 4 5 6 7 8 9 0", "ض ص ث ق ف غ ع ه خ ح ج د",
                    "ش س ي ب ل ا ت ن م ك ط", "ذ ئ ء ؤ ر ى ة و ز ظ {back}", bottom);
        }
        String top = "q w e r t y u i o p";
        String middle = "{gap} a s d f g h j k l {gap}";
        String lower = "z x c v b n m";
        if (uppercase) {
            top = top.toUpperCase(Locale.ROOT);
            middle = "{gap} A S D F G H J K L {gap}";
            lower = lower.toUpperCase(Locale.ROOT);
        }
        return split("1 2 3 4 5 6 7 8 9 0", top, middle, SHIFT + " " + lower + " " + BACK, bottom);
    }

    private static String[][] split(String... rows) {
        String[][] result = new String[rows.length][];
        for (int i = 0; i < rows.length; i++) result[i] = rows[i].split(" ");
        return result;
    }

    static float weight(String key) {
        if (SPACE.equals(key)) return 3.4f;
        if (GAP.equals(key)) return 0.5f;
        if (ENTER.equals(key) || SHIFT.equals(key) || SYMBOL.equals(key)) return 1.4f;
        return BACK.equals(key) ? 1.3f : 1f;
    }
}
