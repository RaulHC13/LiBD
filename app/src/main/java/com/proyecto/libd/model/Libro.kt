package com.proyecto.libd.model

data class Libro(
    var titulo: String ?= null,
    var numPaginas: Int ?= null,
    var autor: String ?= null,
    var valoracion: Float,
    var portadaURL: String
): java.io.Serializable