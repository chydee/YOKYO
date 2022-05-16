package com.nwanneka.yokyo.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Logg(
    val id: Int,
    val location: String,
    val long: Long,
    val lat: Long,
    val date: String,
    val time: String
) : Parcelable
