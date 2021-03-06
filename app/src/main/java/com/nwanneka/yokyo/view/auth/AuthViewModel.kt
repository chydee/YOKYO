package com.nwanneka.yokyo.view.auth

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.view.utils.minimumEightInLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _error: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String>
        get() = _error

    private val _userLiveData = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _userLiveData

    var auth: FirebaseAuth? = null

    init {
        auth = Firebase.auth
    }

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

    fun signIn(email: String, password: String) {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _userLiveData.postValue(auth!!.currentUser)
                        } else {
                            _userLiveData.postValue(null)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        _userLiveData.postValue(null)
                        _error.postValue(it.message ?: "Authentication Failed!")
                    }
            }
        }

    }

    fun signUp(email: String, password: String) {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _userLiveData.postValue(auth!!.currentUser)
                        } else {
                            _userLiveData.postValue(null)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        _userLiveData.postValue(null)
                        _error.postValue(it.message ?: "Authentication Failed!")
                    }
            }
        }
    }

    private val _resetPass = MutableLiveData<String?>()
    val resetLiveData: LiveData<String?>
        get() = _resetPass

    fun resetPassword(email: String) {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _resetPass.postValue("A link to reset your password has been sent to $email")
                        } else {
                            _resetPass.postValue(null)
                        }
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        _userLiveData.postValue(null)
                        _error.postValue(it.message ?: "Unknown error")
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        coroutineScope.cancel()
    }
}