package com.proyecto.libd.model

data class MensajeChat(
    var texto: String ?= null,
    var email: String ?= null,
    var fecha: Long = System.currentTimeMillis()
):java.io.Serializable
