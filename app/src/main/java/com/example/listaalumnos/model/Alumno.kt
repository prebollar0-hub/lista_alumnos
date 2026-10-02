package com.example.listaalumnos.model

/**
 * Modelo de datos que representa a un Alumno.
 *
 * @property id Identificador único autoincrementable en la base de datos SQLite.
 *           Justificación: Se incluye el id de tipo Int para facilitar las operaciones CRUD
 *           (especialmente la actualización y eliminación precisa de un registro), evitando
 *           depender únicamente del número de cuenta u otros campos modificables.
 * @property nombre Nombre completo del alumno.
 * @property cuenta Número de cuenta único del alumno (cadena numérica).
 * @property correo Correo electrónico del alumno.
 * @property imagen URL pública con la foto de perfil del alumno.
 */
data class Alumno(
    val id: Int = 0,
    val nombre: String,
    val cuenta: String,
    val correo: String,
    val imagen: String
)
