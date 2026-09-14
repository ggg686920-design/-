package app.nasma.keyboard;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import java.util.Locale;
import static app.nasma.keyboard.KeyLayout.*;

/** Reconstructed from Nasma 1.0; no suggestions, recording, clipboard or networking. */
public final class NasmaIme extends InputMethodService {
    private boolean arabic = true;
    private boolean dark, repeated, secondPage;
    private int mode, shift;
    private int deletePointer = -1;
    private Button heldDelete;
    private LinearLayout keyboard;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable repeatDelete = new Runnable() {
        @Override public void run() {
            if (heldDelete == null || !heldDelete.isPressed()) return;
            repeated = true;
            delete();
            handler.postDelayed(this, 55);
        }
    };

    @Override public View onCreateInputView() {
        keyboard = new LinearLayout(this);
        keyboard.setOrientation(LinearLayout.VERTICAL);
        keyboard.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        keyboard.setPadding(dp(4), dp(6), dp(4), dp(8));
        if (Build.VERSION.SDK_INT >= 26) keyboard.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        buildKeys();
        return keyboard;
    }

    // Keep Android Button's normal click/accessibility behavior; touch only schedules repeat.
    @SuppressLint("ClickableViewAccessibility")
    private void buildKeys() {
        if (keyboard == null) return;
        stopRepeat();
        dark = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        keyboard.setBackgroundColor(color(dark ? "#171D1B" : "#E9EEE8"));
        keyboard.removeAllViews();
        int height = dp(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 40 : 49);
        for (String[] keys : KeyLayout.rows(arabic, mode, shift > 0, secondPage)) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER);
            keyboard.addView(row, new LinearLayout.LayoutParams(-1, height));
            for (String key : keys) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, KeyLayout.weight(key));
                params.setMargins(dp(2), dp(3), dp(2), dp(3));
                if (GAP.equals(key)) { row.addView(new View(this), params); continue; }
                Button button = new Button(this);
                button.setText(label(key));
                button.setAllCaps(false);
                button.setMinWidth(0);
                button.setMinimumWidth(0);
                button.setMinHeight(0);
                button.setMinimumHeight(0);
                button.setPadding(0, 0, 0, 0);
                button.setIncludeFontPadding(false);
                button.setGravity(Gravity.CENTER);
                button.setTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
                button.setTextSize(key.startsWith("{") ? 13 : arabic ? 21 : 19);
                button.setTextDirection(View.TEXT_DIRECTION_LOCALE);
                button.setSoundEffectsEnabled(false);
                button.setHapticFeedbackEnabled(false);
                button.setStateListAnimator(null);
                button.setSingleLine(true);
                button.setTag(key);
                styleKey(button, ENTER.equals(key) || (SHIFT.equals(key) && shift > 0));
                if (BACK.equals(key)) button.setContentDescription(getString(R.string.delete_description));
                if (SPACE.equals(key)) button.setContentDescription(getString(R.string.space_description));
                if (LANG.equals(key)) button.setContentDescription(getString(R.string.language_description));
                button.setOnClickListener(view -> {
                    if (BACK.equals(key) && repeated) { repeated = false; return; }
                    press(key);
                });
                if (SPACE.equals(key) || LANG.equals(key)) {
                    button.setOnLongClickListener(view -> {
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).showInputMethodPicker();
                        return true;
                    });
                }
                if (BACK.equals(key)) button.setOnTouchListener((view, event) -> {
                    switch (event.getActionMasked()) {
                        case MotionEvent.ACTION_DOWN:
                            stopRepeat(); repeated = false; heldDelete = button;
                            deletePointer = event.getPointerId(0);
                            handler.postDelayed(repeatDelete, 400); break;
                        case MotionEvent.ACTION_POINTER_UP:
                            if (event.getPointerId(event.getActionIndex()) == deletePointer) {
                                stopRepeat(); repeated = true;
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                            repeated = false; stopRepeat(); break;
                        case MotionEvent.ACTION_UP:
                            stopRepeat(); break;
                    }
                    return false;
                });
                row.addView(button, params);
            }
        }
    }

    private void styleKey(Button button, boolean accent) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color(accent ? "#23634E" : dark ? "#2B3530" : "#FFFFFF"));
        shape.setCornerRadius(dp(7));
        button.setBackground(new RippleDrawable(ColorStateList.valueOf(color(dark ? "#50655A" : "#CCDCCE")), shape, null));
        button.setTextColor(color(accent || dark ? "#F7FAF5" : "#1E3027"));
    }

    private String label(String key) {
        switch (key) {
            case BACK: return "⌫";
            case SPACE: return arabic ? "العربية" : "English";
            case LANG: return arabic ? "EN" : "ع";
            case SHIFT: return shift == 2 ? "CAPS" : "⇧";
            case SYMBOL: return mode == 1 ? (arabic ? "أبج" : "ABC") : "?123";
            case EXTRA: return mode == 2 ? "أبج" : "أَ";
            case PAGE: return secondPage ? "1/2" : "2/2";
            case ENTER:
                EditorInfo info = getCurrentInputEditorInfo();
                if (info == null || (info.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) return "↵";
                if (info.actionLabel != null) return info.actionLabel.toString();
                switch (info.imeOptions & EditorInfo.IME_MASK_ACTION) {
                    case EditorInfo.IME_ACTION_GO: return arabic ? "اذهب" : "Go";
                    case EditorInfo.IME_ACTION_SEARCH: return arabic ? "بحث" : "Search";
                    case EditorInfo.IME_ACTION_SEND: return arabic ? "إرسال" : "Send";
                    case EditorInfo.IME_ACTION_NEXT: return arabic ? "التالي" : "Next";
                    case EditorInfo.IME_ACTION_PREVIOUS: return arabic ? "السابق" : "Previous";
                    case EditorInfo.IME_ACTION_DONE: return arabic ? "تم" : "Done";
                    default: return "↵";
                }
            default:
                return key.length() == 1 && Character.getType(key.charAt(0)) == Character.NON_SPACING_MARK ? "◌" + key : key;
        }
    }

    private void press(String key) {
        switch (key) {
            case SYMBOL: mode = mode == 1 ? 0 : 1; secondPage = false; buildKeys(); break;
            case BACK: delete(); break;
            case LANG:
                arabic = !arabic; mode = 0; shift = 0;
                getSharedPreferences("settings", MODE_PRIVATE).edit().putBoolean("arabic", arabic).apply();
                buildKeys(); break;
            case PAGE: secondPage = !secondPage; buildKeys(); break;
            case ENTER: enter(); break;
            case EXTRA: mode = mode == 2 ? 0 : 2; buildKeys(); break;
            case SHIFT: shift = (shift + 1) % 3; updateCase(); break;
            default:
                InputConnection connection = getCurrentInputConnection();
                if (connection == null) return;
                String text = SPACE.equals(key) ? " " : key;
                if (!arabic && mode == 0) text = shift > 0 ? text.toUpperCase(Locale.ROOT) : text.toLowerCase(Locale.ROOT);
                EditorInfo info = getCurrentInputEditorInfo();
                if (info != null && info.inputType == 0 && text.length() == 1) sendKeyChar(text.charAt(0));
                else connection.commitText(text, 1);
                if (!arabic && mode == 0 && shift == 1 && !SPACE.equals(key)) { shift = 0; updateCase(); }
        }
    }

    private void updateCase() {
        if (keyboard == null) return;
        for (int i = 0; i < keyboard.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) keyboard.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                View child = row.getChildAt(j);
                if (!(child instanceof Button)) continue;
                Button button = (Button) child;
                String key = (String) button.getTag();
                if (SHIFT.equals(key)) { button.setText(label(key)); styleKey(button, shift > 0); }
                else if (!key.startsWith("{")) button.setText(shift > 0 ? key.toUpperCase(Locale.ROOT) : key.toLowerCase(Locale.ROOT));
            }
        }
    }

    private void delete() {
        InputConnection connection = getCurrentInputConnection();
        if (connection == null) return;
        EditorInfo info = getCurrentInputEditorInfo();
        if (info != null && info.inputType == 0) { sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL); return; }
        CharSequence selection = connection.getSelectedText(0);
        if (selection != null && selection.length() > 0) connection.commitText("", 1);
        else if (Build.VERSION.SDK_INT < 24 || !connection.deleteSurroundingTextInCodePoints(1, 0)) sendDownUpKeyEvents(KeyEvent.KEYCODE_DEL);
    }

    private void enter() {
        InputConnection connection = getCurrentInputConnection();
        if (connection == null) return;
        EditorInfo info = getCurrentInputEditorInfo();
        if (info != null && (info.imeOptions & EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0) {
            int action = info.actionLabel != null ? info.actionId : info.imeOptions & EditorInfo.IME_MASK_ACTION;
            if ((info.actionLabel != null || (action != EditorInfo.IME_ACTION_NONE && action != EditorInfo.IME_ACTION_UNSPECIFIED))
                    && connection.performEditorAction(action)) return;
        }
        if (info != null && info.inputType == 0) sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
        else connection.commitText("\n", 1);
    }

    private void stopRepeat() { handler.removeCallbacks(repeatDelete); heldDelete = null; deletePointer = -1; }
    private int color(String value) { return Color.parseColor(value); }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    @Override public void onStartInput(EditorInfo info, boolean restarting) {
        super.onStartInput(info, restarting);
        stopRepeat(); repeated = false;
        arabic = getSharedPreferences("settings", MODE_PRIVATE).getBoolean("arabic", true);
        mode = 0; shift = 0; secondPage = false;
        if (info != null) {
            if (EditorPolicy.usesSymbols(info.inputType)) mode = 1;
            if (EditorPolicy.prefersLatin(info.inputType, info.imeOptions)) arabic = false;
        }
    }
    @Override public void onStartInputView(EditorInfo info, boolean restarting) { super.onStartInputView(info, restarting); buildKeys(); }
    @Override public boolean onEvaluateFullscreenMode() { return false; }
    @Override public void onFinishInput() { stopRepeat(); repeated = false; super.onFinishInput(); }
    @Override public void onFinishInputView(boolean finishingInput) { stopRepeat(); repeated = false; super.onFinishInputView(finishingInput); }
    @Override public void onUnbindInput() { stopRepeat(); repeated = false; super.onUnbindInput(); }
    @Override public void onDestroy() { stopRepeat(); super.onDestroy(); }
}
