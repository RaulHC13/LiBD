package com.example.libd

import android.content.Context

class Prefs(c: Context) {
    val storage = c.getSharedPreferences("CHAT", 0)

    fun setEmail(email: String) {
        storage.edit().putString("EMAIL", email).apply()
    }

    fun getEmail(): String? {
        return storage.getString("EMAIL", null)
    }

    fun borrarTodo() {
        storage.edit().clear().apply()
    }
}