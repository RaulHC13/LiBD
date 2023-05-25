package com.proyecto.libd.model

data class Libro(
    var titulo: String = "",
    var numPaginas: Int ?= null,
    var autor: String ?= null,
    var valoracion: Float = 0.0f,
    var numValoraciones: Int = 0,
    var fecha: Long = System.currentTimeMillis()
): java.io.Serializable