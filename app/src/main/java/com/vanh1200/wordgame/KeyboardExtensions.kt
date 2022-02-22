package com.vanh1200.wordgame

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText


/**
 * Show the soft input from view
 */
fun View.showSoftInput() {
    showSoftInput(0)
}

/**
 * Show the soft input from view
 *
 * @param flags Provides additional operating flags.  Currently may be 0 or have the [InputMethodManager.SHOW_IMPLICIT] bit set.
 */
fun View.showSoftInput(flags: Int) {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { manager ->
        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()
        manager.showSoftInput(
            this, flags,
            object : ResultReceiver(Handler()) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    if (resultCode == InputMethodManager.RESULT_UNCHANGED_HIDDEN ||
                        resultCode == InputMethodManager.RESULT_HIDDEN
                    ) {
                        context.toggleSoftInput()
                    }
                }
            }
        )
        manager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }
}

/**
 * Hide the soft input from view
 */
fun View.hideSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { manager ->
        manager.hideSoftInputFromWindow(windowToken, 0)
    }
}

/**
 * Hide the soft input from window
 */
fun Window.hideSoftInput() {
    currentFocus?.let {
        val focusView = decorView.findViewWithTag<View?>("keyboardTagView")
        val view = if (focusView == null) {
            val editText = EditText(context)
            editText.tag = "keyboardTagView"
            (decorView as ViewGroup).addView(editText, 0, 0)
            editText
        } else {
            focusView
        }
        view.hideSoftInput()
    }
}

/**
 * Hide the soft input from activity
 */
fun Activity.hideSoftInput() {
    window.hideSoftInput()
}

/**
 * Toggle the soft input display or not.
 */
fun Context.toggleSoftInput() {
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.let { manager ->
        manager.toggleSoftInput(0, 0)
    }
}

private var decorViewDelta = 0



