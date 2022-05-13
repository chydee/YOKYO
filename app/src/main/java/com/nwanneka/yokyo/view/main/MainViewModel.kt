package com.nwanneka.yokyo.view.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.view.utils.minimumEightInLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val viewModelJob = Job()
    private val coroutineScope: CoroutineScope = CoroutineScope(viewModelJob + Dispatchers.IO)

    private val _error: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: LiveData<String>
        get() = _error

    var auth: FirebaseAuth? = null
    var database: DatabaseReference? = null


    init {
        auth = Firebase.auth
        database = Firebase.database.reference

        getCurrentUser()
    }

    private val _userLiveData = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?>
        get() = _userLiveData

    private fun getCurrentUser() {
        coroutineScope.launch {
            if (auth != null) {
                _userLiveData.postValue(auth!!.currentUser)
            }
        }
    }

    private val _emailUpdate = MutableLiveData<Boolean>()
    val isEmailUpdated: LiveData<Boolean>
        get() = _emailUpdate

    fun updateEmail(email: String) {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.currentUser?.updateEmail(email)
                    ?.addOnCompleteListener { task ->
                        _emailUpdate.postValue(task.isSuccessful)
                    }
                    ?.addOnFailureListener { exception: Exception ->
                        _error.postValue(exception.message)
                    }
            }
        }
    }

    private val _passwordChange = MutableLiveData<Boolean>()
    val isChangePassword: LiveData<Boolean>
        get() = _passwordChange

    fun updatePassword(newPass: String) {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.currentUser?.updatePassword(newPass)
                    ?.addOnCompleteListener { task ->
                        _passwordChange.postValue(task.isSuccessful)
                    }
                    ?.addOnFailureListener { exception: Exception ->
                        _error.postValue(exception.message)
                    }
            }
        }
    }


    private val _deleteAccount = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean>
        get() = _deleteAccount


    fun deleteMyAccount() {
        coroutineScope.launch {
            if (auth != null) {
                auth!!.currentUser?.delete()
                    ?.addOnCompleteListener { task ->
                        _deleteAccount.postValue(task.isSuccessful)
                    }
                    ?.addOnFailureListener { exception: Exception ->
                        _error.postValue(exception.message)
                    }
            }
        }
    }

    private val _highlights = MutableLiveData<DataSnapshot>()
    val highlightSnapshot: LiveData<DataSnapshot>
        get() = _highlights

    fun fetchHighlights() {
        coroutineScope.launch {
            database?.child("highlights")?.get()
                ?.addOnSuccessListener { snapshot ->
                    if (snapshot.exists() && snapshot.hasChildren()) {
                        _highlights.postValue(snapshot)
                        snapshot.children.forEach {
                            Log.d("Highlights", (it.child("id").getValue<Int>() as Int).toString())
                        }
                    }
                }
        }
    }

    fun verifyPasswordText(context: Context, password: String): String {
        return when {
            password.isEmpty() -> context.getString(R.string.field_empty)
            !password.minimumEightInLength() -> context.getString(R.string.password_8_characters_long)
            else -> ""
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}