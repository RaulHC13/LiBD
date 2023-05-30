package com.proyecto.libd.model

data class Libro(
    var titulo: String = "",
    var numPaginas: Int ?= null,
    var autor: String ?= null,
    var valoracion: Float = 0.0f,
    var numValoraciones: Int = 0,
    val valoracionUsuario: HashMap<String, Float> = HashMap(),
    var fecha: Long = System.currentTimeMillis()
): java.io.Serializable