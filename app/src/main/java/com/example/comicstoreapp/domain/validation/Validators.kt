package com.example.comicstoreapp.domain.validation

import android.util.Patterns


//VALIDACIONES DE REGISTRO

fun validarNombre(nombre: String): String? {
    if (nombre.isBlank()) return "El nombre es obligatorio"
    if (nombre.length < 3) return "El nombre ingresado es demasiado corto"

    val regex = Regex("^[A-Za-zÁÉÍÓÚÑáéíóúñ ]+$")
    return if (!regex.matches(nombre)) "Solo letras y espacios" else null
}


fun validarRut(rut: String): String? {
    val regex = Regex("^[0-9]{7,8}-[0-9Kk]$")
    val format = rut.replace(".","")

    if (rut.isBlank()) return "El RUT es obligatorio"
    if (!format.matches(regex)) return "El RUT ingresado es inválido"
    return null
}


fun validarCorreo(correo: String): String?{
    val regex = Patterns.EMAIL_ADDRESS.matcher(correo).matches()

    if (correo.isBlank()) return "El correo es obligatorio"
    if (!regex) return "El correo ingresado es inválido"
    return null
}


fun validarContrasena(contrasena: String): String?{
    if (contrasena.isBlank()) return "La contraseña es obligatoria"
    if (contrasena.length < 8) return "La contraseña debe tener al menos 8 caracteres"
    if (!contrasena.any { it.isUpperCase() }) return "La contraseña debe tener al menos una letra mayúscula"
    return null
}


fun confirmarContrasena(contrasena: String, confirmacion: String): String? {
    if (confirmacion.isBlank()) return "La confirmación es obligatoria"
    if (contrasena != confirmacion) return "Las contraseñas no coinciden"
    return null
}
