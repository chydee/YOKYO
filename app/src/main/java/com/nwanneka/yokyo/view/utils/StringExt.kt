package com.nwanneka.yokyo.view.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.containsLowerCaseChar(): Boolean {
    val p: Pattern = Pattern.compile("^(?=.*[a-z]).+$")
    val m: Matcher = p.matcher(this)
    return m.matches()
}

fun String.containsUpperCaseChar(): Boolean {
    val p: Pattern = Pattern.compile("^(?=.*?[A-Z]).+$")
    // Find match between given string
    // & regular expression
    val m: Matcher = p.matcher(this)
    return m.matches()
}

fun String.containsADigitChar(): Boolean {
    val p: Pattern = Pattern.compile("^(?=.*?[0-9]).+$")
    // Find match between given string
    // & regular expression
    val m: Matcher = p.matcher(this)
    return m.matches()
}

fun String.containsASpecialChar(): Boolean {
    // Compile the ReGex
    val p: Pattern = Pattern.compile("^(?=.*?[-+_!@#$%^&*., ?]).+$")
    // Find match between given string
    // & regular expression
    val m: Matcher = p.matcher(this)
    return m.matches()
}

fun String.minimumEightInLength(): Boolean {
    val m: Matcher = Pattern.compile(".{8,}\$").matcher(this)
    return m.matches()
}


fun String.isStrongPassword(): Boolean {
    val m: Matcher = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$").matcher(this)
    return m.matches()
}

fun String.isUrl(): Boolean = this.isNotEmpty() && Pattern.compile(
    "^(?!mailto:)(?:(?:http|https|ftp)://)?(?:\\S+(?::\\S*)?@)?(?:(?:(?:[1-9]\\d?|1\\d\\d|2[01]\\d|22[0-3])(?:\\.(?:1?\\d{1,2}|2[0-4]\\d|25[0-5])){2}(?:\\.(?:[0-9]\\d?|1\\d\\d|2[0-4]\\d|25[0-4]))|(?:(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]+-?)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))|localhost)(?::\\d{2,5})?(?:(/|\\?|#)[^\\s]*)?\$"
).matcher(this).matches()

fun String.isUserName(): Boolean = this.isNotEmpty() && Pattern.compile(
    "^[a-zA-Z0-9_]*$"
).matcher(this).matches()

fun String.isName(): Boolean = this.isNotEmpty() && Pattern.compile(
    "^[a-zA-Z][a-zA-Z ]*$"
).matcher(this).matches()
