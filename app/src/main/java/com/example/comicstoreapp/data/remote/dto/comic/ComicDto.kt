package com.example.comicstoreapp.data.remote.dto.comic

data class ComicDto(

    val id: Int,
    val titulo: String,
    val descripcion: String,
    val autor: String,
    val precio: Int,
    val cantidad: Int,
    val imagenUrl: String?,
    val categoria: String

)
