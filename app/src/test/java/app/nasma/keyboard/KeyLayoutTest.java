package app.nasma.keyboard;

import java.util.Arrays;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class KeyLayoutTest {
    private Set<String> keys(boolean arabic, int mode, boolean shift, boolean page) {
        Set<String> result = new HashSet<>();
        for (String[] row : KeyLayout.rows(arabic, mode, shift, page)) result.addAll(Arrays.asList(row));
        return result;
    }
    @Test public void allStatesMatchOriginalGoldenAndRetainCoreKeys() throws Exception {
        StringBuilder snapshot = new StringBuilder();
        for (boolean arabic : new boolean[]{false, true})
            for (int mode = 0; mode < 3; mode++)
                for (boolean shift : new boolean[]{false, true})
                    for (boolean page : new boolean[]{false, true}) {
                        String[][] rows = KeyLayout.rows(arabic, mode, shift, page);
                        snapshot.append(Arrays.deepToString(rows)).append('\n');
                        assertEquals(5, rows.length);
                        for (String[] row : rows) for (String key : row) assertFalse(key.isEmpty());
                        assertTrue(keys(arabic, mode, shift, page).containsAll(Arrays.asList(KeyLayout.SPACE, KeyLayout.ENTER, KeyLayout.BACK, KeyLayout.LANG)));
                    }
        for (String key : new String[]{"{space}", "{gap}", "{enter}", "{shift}", "{symbol}", "{back}", "a"})
            snapshot.append(key).append(':').append(KeyLayout.weight(key)).append('\n');
        try (InputStream original = getClass().getResourceAsStream("/original-layouts.txt")) {
            assertNotNull("Preserved original layout fixture is missing", original);
            assertEquals(new String(original.readAllBytes(), StandardCharsets.UTF_8), snapshot.toString());
        }
    }
    @Test public void arabicAndExtraPagesContainOriginalCharacters() {
        assertTrue(keys(true, 0, false, false).containsAll(Arrays.asList("ض", "ش", "ي", "ا", "ة", "،")));
        assertTrue(keys(true, 2, false, false).containsAll(Arrays.asList("أ", "إ", "آ", "َ", "ّ", "ْ", "پ")));
    }
    @Test public void englishHasFullAlphabetAndLocaleIndependentShift() {
        Locale before = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr"));
            for (char letter = 'a'; letter <= 'z'; letter++) {
                assertTrue(keys(false, 0, false, false).contains(String.valueOf(letter)));
                assertTrue(keys(false, 0, true, false).contains(String.valueOf(letter).toUpperCase(Locale.ROOT)));
            }
        } finally { Locale.setDefault(before); }
    }
    @Test public void symbolsIncludeLiteralBracesAndArabicPunctuation() {
        assertTrue(keys(true, 1, false, true).containsAll(Arrays.asList("{", "}", "١", "٠", "€")));
        assertTrue(keys(true, 1, false, false).containsAll(Arrays.asList("@", "؟", "؛", "\\")));
    }
    @Test public void recoveredKeyWeightsRemainStable() {
        assertEquals(3.4f, KeyLayout.weight(KeyLayout.SPACE), 0f);
        assertEquals(0.5f, KeyLayout.weight(KeyLayout.GAP), 0f);
        assertEquals(1.4f, KeyLayout.weight(KeyLayout.ENTER), 0f);
        assertEquals(1.3f, KeyLayout.weight(KeyLayout.BACK), 0f);
        assertEquals(1f, KeyLayout.weight("a"), 0f);
    }
}
