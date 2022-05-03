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

@HiltViewModel
class AuthViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _error: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String>
        get() = _error

    private val _userLiveData = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _userLiveData

    private val auth: FirebaseAuth = Firebase.auth

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
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _userLiveData.postValue(auth.currentUser)
                    } else {
                        _userLiveData.postValue(null)
                        _error.postValue("Authentication Failed!")
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    _userLiveData.postValue(null)
                    _error.postValue(it.message)
                }
        }

    }

    fun signUp(email: String, password: String) {
        coroutineScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        _userLiveData.postValue(auth.currentUser)
                    } else {
                        _userLiveData.postValue(null)
                        _error.postValue("Account creation Failed!")
                    }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    _userLiveData.postValue(null)
                    _error.postValue(it.message)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
        coroutineScope.cancel()
    }
}