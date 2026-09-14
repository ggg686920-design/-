package app.nasma.keyboard

import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

/** Isolated native-view checks, complementary to the system-bound smoke tests. */
@RunWith(AndroidJUnit4::class)
class ImeViewTest {
    private fun buttons(view: View): List<Button> = when (view) {
        is Button -> listOf(view)
        is ViewGroup -> (0 until view.childCount).flatMap { buttons(view.getChildAt(it)) }
        else -> emptyList()
    }

    private fun withView(check: (NasmaIme, View) -> Unit) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        instrumentation.runOnMainSync {
            val ime = NasmaIme()
            // Only attach a context to render Views; do not register a second system IME.
            ContextWrapper::class.java.getDeclaredMethod("attachBaseContext", Context::class.java)
                .apply { isAccessible = true }.invoke(ime, instrumentation.targetContext)
            val view = ime.onCreateInputView()
            try { check(ime, view) } finally { ime.onFinishInput() }
        }
    }

    @Test fun everyKeyboardPageDisablesSoundsAndHaptics() = withView { _, root ->
        val transitions = listOf(KeyLayout.SYMBOL, KeyLayout.PAGE, KeyLayout.SYMBOL,
            KeyLayout.EXTRA, KeyLayout.EXTRA, KeyLayout.LANG, KeyLayout.SHIFT, KeyLayout.SYMBOL)
        for (transition in transitions) {
            val keys = buttons(root)
            assertTrue(keys.isNotEmpty())
            for (key in keys) {
                assertFalse("Key requested sound", key.isSoundEffectsEnabled)
                assertFalse("Key allowed tap/long-press haptics", key.isHapticFeedbackEnabled)
            }
            keys.single { it.tag == transition }.performClick()
        }
        assertTrue(buttons(root).all { !it.isHapticFeedbackEnabled && !it.isSoundEffectsEnabled })
    }

    @Test fun unbindCancelsPendingDeleteAndDropsHeldButton() = withView { ime, root ->
        val delete = buttons(root).single { it.tag == KeyLayout.BACK }
        val time = SystemClock.uptimeMillis()
        val event = MotionEvent.obtain(time, time, MotionEvent.ACTION_DOWN, 5f, 5f, 0)
        try { delete.dispatchTouchEvent(event) } finally { event.recycle() }
        val handler = NasmaIme::class.java.getDeclaredField("handler")
            .apply { isAccessible = true }.get(ime) as Handler
        val held = NasmaIme::class.java.getDeclaredField("heldDelete").apply { isAccessible = true }
        assertTrue(handler.hasMessages(0))
        assertSame(delete, held.get(ime))
        ime.onUnbindInput()
        assertFalse("Unbound service retained pending repeat callback", handler.hasMessages(0))
        assertNull("Unbound service retained held button", held.get(ime))
    }
}
