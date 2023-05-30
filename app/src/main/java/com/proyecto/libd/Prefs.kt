package com.proyecto.libd

import android.content.Context

class Prefs(c: Context) {
    val storage = c.getSharedPreferences("CHAT", 0)

    fun setEmail(email: String) {
        storage.edit().putString("EMAIL", email).apply()
    }

    fun getEmail(): String? {
        return storage.getString("EMAIL", null)
    }

    fun setUsername(username: String) {
        storage.edit().putString("USERNAME", username).apply()
    }

    //El valor por defecto es un username autogenerado, el texto antes del @ del email.
    fun getUsername(): String {
        return storage.getString("USERNAME", getUsernameGenerado())!!
    }

    fun setDefaultUsername() {
        setUsername(getUsernameGenerado())
    }

    //Genera una substring con el texto de antes del @
    fun getUsernameGenerado(): String {
        var email = getEmail()
        return email!!.substring(0, email.indexOf("@")).toString()
    }

    //Cambia los "." en el email a "-" para que firebase no de problemas de formato.
    fun getEmailFormateado(): String? {
        var emailFormateado = getEmail()?.replace(".", "-")
        return emailFormateado
    }

    fun borrarPrefs() {
        storage.edit().clear().apply()
    }
}