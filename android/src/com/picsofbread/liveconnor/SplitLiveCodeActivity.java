package com.picsofbread.liveconnor;

import android.annotation.SuppressLint;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.github.ahmadaghazadeh.editor.processor.TextNotFoundException;
import com.github.ahmadaghazadeh.editor.widget.CodeEditor;
import com.picsofbread.liveconnor.state.States;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplitLiveCodeActivity extends AndroidApplication implements ILogCallbackProcessor {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private LiveConnor liveConnorInstance;

    private Deque<LogLine> log = new ArrayDeque<>();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = null;
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (AUTO_HIDE) {
                        delayedHide(AUTO_HIDE_DELAY_MILLIS);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    view.performClick();
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_split_live_code);

        mVisible = true;
        //mControlsView = findViewById(R.id.gameViewFrameLayout);
        mContentView = findViewById(R.id.parentLayout);

        // initialize the gdx view
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        liveConnorInstance = new LiveConnor();
        States.STATE_LIVE_CODE_MAIN.logCallback = this;

        View view = initializeForView(liveConnorInstance, config);

        // add it to the empty framelayout we have for it
        ViewGroup group = findViewById(R.id.gameViewFrameLayout);
        group.addView(view);

        CodeEditor editor = findViewById(R.id.codeMultiLineText);
        editor.setText("", 0);

        //FIXME: maybe find a way to disable the onscreen keyboard?

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(view1 -> toggle());

        // NOTE: could use a binding for this, but for now we just dump the whole edittext's string into the codehaschanged func in LiveConnor
        //       -- means we can do processing on it after
        editor.setOnTextChange(str -> States.STATE_LIVE_CODE_MAIN.codeHasChanged(str));

        Button testButton = findViewById(R.id.testItButton);
        testButton.setOnClickListener(v -> States.STATE_LIVE_CODE_MAIN.executeCode());

        EditText errorLog = findViewById(R.id.errorLog);

        Button refreshLogButton = findViewById(R.id.refreshLogButton); // FIXME: make this not a button.
        refreshLogButton.setOnClickListener(v -> errorLog.setText(logToString()));

        ImageButton undoButton = findViewById(R.id.undoButton);
        undoButton.setOnClickListener(v -> {
            try {
                editor.undo();
            } catch (TextNotFoundException e) {
                // just ignore if there's no text
            }
        });

        ImageButton redoButton = findViewById(R.id.redoButton);
        redoButton.setOnClickListener(v -> {
            try {
                editor.redo();
            } catch (TextNotFoundException e) {
                // just ignore if there's no text
            }
        });
    }

    public String logToString() {
        StringBuilder builder = new StringBuilder();

        for (LogLine l : log) {
            builder.append("(");
            builder.append(l.count);
            builder.append(") ");
            builder.append(l.message);
            builder.append("\n");
        }

        return builder.toString();
    }

    @Override
    public void logThis(String toLog) {
        if (log.size() > 128) {
            log.removeFirst();
        }

        for (LogLine l : log) {
            if (l.message == toLog) {
                l.count++;
                return;
            }
        }

        log.addLast(new LogLine(1, toLog));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = null; //getsupportactionbar
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        // Show the system bar
        if (Build.VERSION.SDK_INT >= 30) {
            mContentView.getWindowInsetsController().show(
                    WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
        } else {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        }
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}