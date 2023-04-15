package com.proyecto.libd.model

data class Libro(
    var titulo: String,
    var numPaginas: Int ?= null,
    var autor: String ?= null,
    var valoracion: Float ?= null,
    var portadaURL: String ?= null
): java.io.Serializable