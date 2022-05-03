package com.nwanneka.yokyo.view.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.text.Spannable
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.toSpannable
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout


fun TextView.setClickableText(startIndex: Int, endIndex: Int, color: Int? = null, onClick: () -> Unit) {
    val span = text.toSpannable().apply {
        setSpan(object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                color?.let { ds.color = it }
                ds.isUnderlineText = false
            }

            override fun onClick(p0: View) {
                onClick.invoke()
            }

        }, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    text = span
    if (movementMethod != LinkMovementMethod.getInstance()) {
        movementMethod = LinkMovementMethod.getInstance()
    }
}

fun TextInputLayout.getText(): String {
    return editText?.text.toString()
}

fun TextInputLayout.setText(text: String?) {
    editText?.setText(text)
}

fun Context.getColorOnPrimary(): Int {
    val tv = TypedValue()

    theme.resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, tv, true)

    return tv.data
}

fun Context.getColorOnBackground(): Int {
    val tv = TypedValue()

    theme.resolveAttribute(com.google.android.material.R.attr.colorOnBackground, tv, true)

    return tv.data
}

fun Context.getColorPrimaryVariant(): Int {
    val tv = TypedValue()

    theme.resolveAttribute(com.google.android.material.R.attr.colorPrimaryVariant, tv, true)

    return tv.data
}

fun Context.getColorPrimary(): Int {
    val tv = TypedValue()

    theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, tv, true)

    return tv.data
}

fun EditText.takeText(): String {
    return this.text.toString().trim()
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun ImageView.setImageDrawableWithAnimation(drawable: Drawable, duration: Int = 300) {
    val currentDrawable = getDrawable()
    if (currentDrawable == null) {
        setImageDrawable(drawable)
        return
    }

    val transitionDrawable = TransitionDrawable(
        arrayOf(
            currentDrawable,
            drawable
        )
    )
    setImageDrawable(transitionDrawable)
    transitionDrawable.startTransition(duration)
}

fun View.showSnackBar(message: String) {
    this.let {
        Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
    }
}

fun View.snackBarWithAction(message: String, actionName: String, action: () -> Unit) {
    this.let {
        Snackbar.make(it, message, Snackbar.LENGTH_LONG)
            .setAction(actionName) {
                action()
            }
            .show()
    }
}
