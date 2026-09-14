package app.nasma.keyboard

import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import java.io.File
import java.util.concurrent.atomic.AtomicInteger
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Real IME windows and InputConnections; run only on a disposable test device. */
@RunWith(AndroidJUnit4::class)
class ImeSmokeTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val device = UiDevice.getInstance(instrumentation)
    private lateinit var scenario: ActivityScenario<MainActivity>
    private lateinit var editor: EditText
    private val action = AtomicInteger(-1)

    @Before fun open() {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE).edit()
            .putBoolean("arabic", true).commit()
        scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            editor = requireNotNull(findEditor(activity.window.decorView))
            editor.setOnEditorActionListener { _, id, _ -> action.set(id); true }
        }
        field(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            EditorInfo.IME_FLAG_NO_ENTER_ACTION)
    }

    @After fun close() {
        if (::scenario.isInitialized) scenario.close()
    }

    private fun findEditor(view: View): EditText? {
        if (view is EditText) return view
        if (view is ViewGroup) for (index in 0 until view.childCount) {
            findEditor(view.getChildAt(index))?.let { return it }
        }
        return null
    }

    private fun field(type: Int, options: Int = EditorInfo.IME_ACTION_DONE) {
        scenario.onActivity { activity ->
            editor.inputType = type
            editor.imeOptions = options
            editor.setText("")
            editor.requestFocus()
            val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.restartInput(editor)
            manager.showSoftInput(editor, InputMethodManager.SHOW_IMPLICIT)
        }
        assertTrue("Nasma IME window did not appear", device.wait(Until.hasObject(
            By.pkg(context.packageName).desc(context.getString(R.string.delete_description))), 5000))
        device.waitForIdle()
    }

    private fun key(label: String) {
        val button = device.wait(Until.findObject(By.pkg(context.packageName)
            .clazz(Button::class.java).text(label)), 3000)
        assertNotNull("Missing keyboard key: $label", button)
        button.click()
        instrumentation.waitForIdleSync()
    }

    private fun text(): String {
        var value = ""
        scenario.onActivity { value = editor.text.toString() }
        return value
    }

    private fun expect(value: String) {
        val deadline = SystemClock.uptimeMillis() + 3000
        while (text() != value && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(30)
        assertEquals(value, text())
    }

    private fun seed(value: String) {
        scenario.onActivity { editor.setText(value); editor.setSelection(value.length) }
        instrumentation.waitForIdleSync()
    }

    @Test fun bilingualShiftCapsSymbolsAndDiacritics() {
        for (letter in listOf("س", "ل", "ا", "م")) key(letter)
        key("العربية")
        key("EN")
        key("⇧"); key("A"); key("b")
        key("⇧"); key("⇧"); key("C"); key("D"); key("CAPS"); key("e")
        expect("سلام AbCDe")
        key("?123"); key("@"); key("2/2"); key("١"); key("ABC")
        key("ع"); key("أَ"); key("أ"); key("◌َ"); key("أبج"); key("1")
        expect("سلام AbCDe@١أَ1")
        device.takeScreenshot(File(context.getExternalFilesDir(null), "bilingual.png"))
    }

    @Test fun deleteTapSelectionSurrogateAndHoldRelease() {
        seed("abc"); key("⌫"); expect("ab")
        seed("abcdef")
        scenario.onActivity { editor.setSelection(1, 5) }
        key("⌫"); expect("af")
        seed("a\uD83D\uDE00"); key("⌫"); expect("a")
        seed("x".repeat(80))
        val button = device.findObject(By.desc(context.getString(R.string.delete_description)))
        val bounds = button.visibleBounds
        device.swipe(bounds.centerX(), bounds.centerY(), bounds.centerX(), bounds.centerY(), 150)
        val remaining = text().length
        assertTrue("Hold must delete repeatedly", remaining in 1..78)
        SystemClock.sleep(500)
        assertEquals("Deletion continued after release", remaining, text().length)
    }

    @Test fun fieldRoutingAndEditorActions() {
        field(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
        key("a"); key("?123"); key("@"); key("ABC"); key("b"); expect("a@b")
        field(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI)
        key("a"); key("?123"); key("/"); expect("a/")
        for (type in listOf(InputType.TYPE_CLASS_NUMBER, InputType.TYPE_CLASS_PHONE,
            InputType.TYPE_CLASS_DATETIME,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)) {
            field(type); key("1"); key("2"); expect("12")
            assertTrue(device.hasObject(By.text("2/2")))
        }
        val actions = listOf(EditorInfo.IME_ACTION_SEARCH to "بحث", EditorInfo.IME_ACTION_SEND to "إرسال",
            EditorInfo.IME_ACTION_GO to "اذهب", EditorInfo.IME_ACTION_NEXT to "التالي",
            EditorInfo.IME_ACTION_DONE to "تم")
        for ((id, label) in actions) {
            action.set(-1)
            field(InputType.TYPE_CLASS_TEXT, id)
            key(label)
            assertEquals(id, action.get())
            expect("")
        }
        field(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
            EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_FLAG_NO_ENTER_ACTION)
        key("س"); key("↵"); expect("س\n")
    }

    @Test fun sensitiveFieldsKeepOnlyLanguagePreference() {
        val fields = listOf(
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD,
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD)
        for (type in fields) {
            field(type); key("1"); key("2"); expect("12")
            assertFalse(EditorPolicy.permitsPersonalization(type, 0))
        }
        field(InputType.TYPE_CLASS_TEXT, EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING)
        key("س"); expect("س")
        assertFalse(EditorPolicy.permitsPersonalization(InputType.TYPE_CLASS_TEXT,
            EditorInfo.IME_FLAG_NO_PERSONALIZED_LEARNING))
        assertEquals(mapOf("arabic" to true),
            context.getSharedPreferences("settings", Context.MODE_PRIVATE).all)
        val permissions = context.packageManager.getPackageInfo(context.packageName,
            PackageManager.GET_PERMISSIONS).requestedPermissions.orEmpty()
        assertFalse(permissions.contains("android.permission.INTERNET"))
        scenario.onActivity {
            assertFalse(editor.isSaveEnabled)
            assertFalse(editor.freezesText)
        }
    }

    @Test fun repeatedOpenCloseAndActivityRecreation() {
        repeat(12) {
            key("س"); expect("س")
            device.pressBack()
            assertTrue(device.wait(Until.gone(By.desc(context.getString(R.string.delete_description))), 3000))
            field(InputType.TYPE_CLASS_TEXT)
        }
        scenario.recreate()
        scenario.onActivity { editor = requireNotNull(findEditor(it.window.decorView)) }
        expect("")
        field(InputType.TYPE_CLASS_TEXT); key("س"); expect("س")
        device.setOrientationLeft()
        device.waitForIdle()
        scenario.onActivity { editor = requireNotNull(findEditor(it.window.decorView)) }
        field(InputType.TYPE_CLASS_TEXT); key("س"); expect("س")
        device.takeScreenshot(File(context.getExternalFilesDir(null), "landscape.png"))
        device.setOrientationNatural()
        device.unfreezeRotation()
    }
}
