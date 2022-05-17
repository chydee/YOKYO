package com.nwanneka.yokyo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Logg(
    val uid: String? = null,
    val location: String? = null,
    val long: Long? = null,
    val lat: Long? = null,
    val date: String? = null,
    val time: String? = null
) : Parcelable
