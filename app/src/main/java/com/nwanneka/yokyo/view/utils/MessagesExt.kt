package com.nwanneka.yokyo.view.utils

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun AppCompatActivity.showToastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}


fun AppCompatActivity.showToastMessageLong(message: String?) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToastMessage(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


fun Fragment.showToastMessageLong(message: String?) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}
