package app.nasma.keyboard;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/** Restores the original setup screen; display strings are now in resources. */
public final class MainActivity extends Activity {
    private static final int INK = Color.rgb(28, 48, 38);
    private static final int GREEN = Color.rgb(35, 99, 78);
    private static final int MUTED = Color.rgb(87, 105, 94);
    private Button enable, select;
    private TextView status;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(245, 247, 242));
        scroll.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        if (Build.VERSION.SDK_INT >= 35) {
            scroll.setOnApplyWindowInsetsListener((view, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout() | WindowInsets.Type.ime());
                view.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                return windowInsets;
            });
        } else scroll.setFitsSystemWindows(true);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(26), dp(30), dp(26), dp(28));
        content.setFocusableInTouchMode(true);
        scroll.addView(content, new FrameLayout.LayoutParams(-1, -2));
        TextView mark = text(getString(R.string.brand_mark), 34, Color.WHITE);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(shape(GREEN, 20));
        content.addView(mark, new LinearLayout.LayoutParams(dp(68), dp(68)));
        addText(content, R.string.app_name, 33, INK, 20, true);
        addText(content, R.string.tagline, 17, MUTED, 4, false);
        status = text("", 14, GREEN);
        LinearLayout.LayoutParams statusParams = new LinearLayout.LayoutParams(-1, -2);
        statusParams.topMargin = dp(22);
        status.setPadding(dp(14), dp(12), dp(14), dp(12));
        status.setBackground(shape(Color.rgb(225, 238, 224), 12));
        content.addView(status, statusParams);
        enable = button(R.string.enable, GREEN, Color.WHITE);
        addButton(content, enable, 18);
        enable.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)));
        select = button(R.string.select, Color.rgb(227, 234, 225), INK);
        addButton(content, select, 10);
        select.setOnClickListener(view -> {
            if (isEnabled()) manager().showInputMethodPicker();
            else Toast.makeText(this, R.string.enable_first, Toast.LENGTH_LONG).show();
        });
        addText(content, R.string.try_title, 18, INK, 26, true);
        EditText sample = new EditText(this);
        sample.setHint(R.string.try_hint);
        sample.setTextColor(INK);
        sample.setHintTextColor(MUTED);
        sample.setTextSize(18);
        sample.setGravity(Gravity.TOP | Gravity.START);
        sample.setTextDirection(View.TEXT_DIRECTION_FIRST_STRONG);
        sample.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        sample.setMinLines(2);
        sample.setPadding(dp(14), dp(14), dp(14), dp(14));
        sample.setBackground(shape(Color.WHITE, 12));
        sample.setSaveEnabled(false);
        sample.setFreezesText(false);
        if (Build.VERSION.SDK_INT >= 26) sample.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        LinearLayout.LayoutParams sampleParams = new LinearLayout.LayoutParams(-1, -2);
        sampleParams.topMargin = dp(10);
        content.addView(sample, sampleParams);
        addText(content, R.string.help_title, 18, INK, 24, true);
        addText(content, R.string.help_body, 14, MUTED, 8, false);
        addText(content, R.string.privacy_title, 18, INK, 24, true);
        addText(content, R.string.privacy_body, 14, MUTED, 8, false);
        addText(content, R.string.trust_notice, 12, MUTED, 18, false);
        TextView version = text(getString(R.string.version_label, BuildConfig.VERSION_NAME), 12, MUTED);
        LinearLayout.LayoutParams versionParams = new LinearLayout.LayoutParams(-1, -2);
        versionParams.topMargin = dp(22);
        content.addView(version, versionParams);
        setContentView(scroll);
        content.requestFocus();
        scroll.requestApplyInsets();
    }

    private InputMethodManager manager() { return (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE); }
    private boolean isEnabled() {
        for (InputMethodInfo info : manager().getEnabledInputMethodList()) {
            if (getPackageName().equals(info.getPackageName())) return true;
        }
        return false;
    }
    private void refresh() {
        if (status == null) return;
        boolean enabled = isEnabled();
        String setting = Settings.Secure.getString(getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD);
        ComponentName component = setting == null ? null : ComponentName.unflattenFromString(setting);
        boolean selected = component != null && getPackageName().equals(component.getPackageName());
        status.setText(selected ? R.string.status_ready : enabled ? R.string.status_enabled : R.string.status_start);
        enable.setText(enabled ? R.string.enabled : R.string.enable);
        select.setText(selected ? R.string.selected : R.string.select);
    }
    private void addButton(LinearLayout parent, Button button, int top) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(56));
        params.topMargin = dp(top);
        parent.addView(button, params);
    }
    private void addText(LinearLayout parent, int resource, int size, int color, int top, boolean bold) {
        TextView view = text(getString(resource), size, color);
        if (bold) view.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(top);
        parent.addView(view, params);
    }
    private TextView text(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value); view.setTextSize(size); view.setTextColor(color);
        view.setLineSpacing(dp(3), 1);
        return view;
    }
    private Button button(int resource, int color, int textColor) {
        Button button = new Button(this);
        button.setText(resource); button.setTextSize(16); button.setTextColor(textColor);
        button.setAllCaps(false); button.setBackground(shape(color, 14));
        return button;
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(color); shape.setCornerRadius(dp(radius));
        return shape;
    }
    private int dp(float value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    @Override public void onResume() { super.onResume(); refresh(); }
    @Override public void onWindowFocusChanged(boolean hasFocus) { super.onWindowFocusChanged(hasFocus); if (hasFocus) refresh(); }
}
