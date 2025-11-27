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
    if (rut.isBlank()) return "El RUT es obligatorio"
    if (rut.length < 7 || rut.length > 12) return "El RUT ingresado es invalido"

    val clean = rut.replace(".", "").replace(" ", "")

    val regex = Regex("^[0-9]{7,8}-[0-9Kk]$")

    if (!clean.matches(regex)) return "El RUT ingresado es inválido"

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

//VALIDACION PRECIO

fun formatearPesos(valor: String): String {
    if (valor.isBlank()) return ""
    return "$" + valor.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

fun validarCantidad(valor: String): String? {
    if (valor.isBlank()) return "La cantidad es obligatoria"
    if (!valor.all { it.isDigit() }) return "La cantidad debe ser numérica"
    val numero = valor.toIntOrNull() ?: return "Cantidad inválida"
    if (numero <= 0) return "La cantidad debe ser mayor a 0"
    return null
}



