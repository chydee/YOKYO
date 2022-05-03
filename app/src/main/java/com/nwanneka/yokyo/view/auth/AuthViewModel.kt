package com.nwanneka.yokyo.view.auth

import android.content.Context
import androidx.lifecycle.ViewModel
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.view.utils.minimumEightInLength
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class AuthViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    fun verifyMailText(context: Context, mail: String): String {
        return if (mail.isEmpty()) context.getString(R.string.field_empty)
        else ""
    }

    fun verifyPasswordText(context: Context, password: String): String {
        return when {
            password.isEmpty() -> context.getString(R.string.field_empty)
            !password.minimumEightInLength() -> context.getString(R.string.password_8_characters_long)
            else -> ""
        }
    }
}