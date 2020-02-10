package com.example.notify.storage

import android.content.Context
import android.content.SharedPreferences

class AccessStorage(
    val context: Context
) {
    fun getLogin(): String? {
        return getSharedPreferences().getString("login", null)
    }

    fun getPassword(): String? {
        return getSharedPreferences().getString("pass", null)
    }

    fun getSession(): String? {
        return getSharedPreferences().getString("session", null)
    }

    fun setLogin(login: String) {
        getSharedPreferences().edit().putString("login", login).apply()
    }

    fun setPassword(pass: String) {
        getSharedPreferences().edit().putString("pass", pass).apply()
    }

    fun setSession(session: String) {
        getSharedPreferences().edit().putString("session", session).apply()
    }

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("AccessStorage", 0)
    }
}