package app.nasma.keyboard

import android.text.InputType.*
import android.view.inputmethod.EditorInfo.*
import org.junit.Assert.*
import org.junit.Test

class EditorPolicyTest {
    @Test fun allTextPasswordVariationsAreSensitiveAndNeverPersonalized() {
        for (variation in listOf(TYPE_TEXT_VARIATION_PASSWORD, TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, TYPE_TEXT_VARIATION_WEB_PASSWORD)) {
            val type = TYPE_CLASS_TEXT or variation or TYPE_TEXT_FLAG_CAP_SENTENCES
            assertTrue(EditorPolicy.isSensitive(type))
            assertFalse(EditorPolicy.permitsPersonalization(type, IME_ACTION_DONE))
            assertTrue(EditorPolicy.prefersLatin(type, 0))
        }
    }
    @Test fun numericPasswordIsSensitiveRegardlessOfNumberFlags() {
        val type = TYPE_CLASS_NUMBER or TYPE_NUMBER_VARIATION_PASSWORD or TYPE_NUMBER_FLAG_SIGNED
        assertTrue(EditorPolicy.isSensitive(type))
        assertFalse(EditorPolicy.permitsPersonalization(type, 0))
        assertTrue(EditorPolicy.usesSymbols(type))
    }
    @Test fun incognitoAndNoSuggestionsDisablePersonalization() {
        assertFalse(EditorPolicy.permitsPersonalization(TYPE_CLASS_TEXT, IME_FLAG_NO_PERSONALIZED_LEARNING))
        assertFalse(EditorPolicy.permitsPersonalization(TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_NO_SUGGESTIONS, 0))
        assertFalse(EditorPolicy.permitsPersonalization(TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_AUTO_COMPLETE, 0))
    }
    @Test fun unknownAndNonTextFieldsDefaultToNoPersonalization() {
        for (type in listOf(0, 15, TYPE_CLASS_NUMBER, TYPE_CLASS_PHONE, TYPE_CLASS_DATETIME, TYPE_CLASS_TEXT or 0xF00)) {
            assertFalse(EditorPolicy.permitsPersonalization(type, 0))
        }
    }
    @Test fun emailAndUriPreferLatinAndAvoidLearning() {
        for (variation in listOf(TYPE_TEXT_VARIATION_EMAIL_ADDRESS, TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS, TYPE_TEXT_VARIATION_URI)) {
            val type = TYPE_CLASS_TEXT or variation
            assertTrue(EditorPolicy.prefersLatin(type, 0))
            assertFalse(EditorPolicy.permitsPersonalization(type, 0))
        }
    }
    @Test fun ordinaryTextIsEligibleButNotAutomaticallyProcessed() {
        assertTrue(EditorPolicy.permitsPersonalization(TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE, 0))
        assertFalse(EditorPolicy.isSensitive(TYPE_CLASS_TEXT))
        assertFalse(EditorPolicy.prefersLatin(TYPE_CLASS_TEXT, 0))
        assertFalse(EditorPolicy.usesSymbols(TYPE_CLASS_TEXT))
    }
    @Test fun forceAsciiHintIsHonored() {
        assertTrue(EditorPolicy.prefersLatin(TYPE_CLASS_TEXT, IME_FLAG_FORCE_ASCII or IME_ACTION_SEARCH))
    }
    @Test fun numberPhoneAndDateUseExistingSymbolsLayout() {
        for (type in listOf(TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL, TYPE_CLASS_PHONE, TYPE_CLASS_DATETIME)) {
            assertTrue(EditorPolicy.usesSymbols(type))
        }
    }
}
