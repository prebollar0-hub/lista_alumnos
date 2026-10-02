package com.example.listaalumnos.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.listaalumnos.model.Alumno

/**
 * Gestor de la base de datos SQLite para la entidad Alumno.
 * Hereda de SQLiteOpenHelper para la creación y actualización de tablas.
 */
class DBHelperAlumno(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "alumnos.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_ALUMNOS = "Alumnos"
        const val COLUMN_ID = "id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CUENTA = "cuenta"
        const val COLUMN_CORREO = "correo"
        const val COLUMN_IMAGEN = "imagen"

        // Sentencia SQL para crear la tabla de Alumnos con restricción UNIQUE en el número de cuenta
        private const val CREATE_TABLE_ALUMNOS = """
            CREATE TABLE $TABLE_ALUMNOS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NOMBRE TEXT NOT NULL,
                $COLUMN_CUENTA TEXT NOT NULL UNIQUE,
                $COLUMN_CORREO TEXT NOT NULL,
                $COLUMN_IMAGEN TEXT NOT NULL
            )
        """
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_ALUMNOS)
        poblarDatosIniciales(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ALUMNOS")
        onCreate(db)
    }

    /**
     * Inserta alumnos de prueba únicamente durante la creación inicial de la base de datos.
     */
    private fun poblarDatosIniciales(db: SQLiteDatabase?) {
        val alumnosSemilla = listOf(
            Alumno(
                nombre = "Ana María García",
                cuenta = "319012345",
                correo = "ana.garcia@alumno.unam.mx",
                imagen = "https://i.pravatar.cc/150?img=5"
            ),
            Alumno(
                nombre = "Carlos Eduardo López",
                cuenta = "319023456",
                correo = "carlos.lopez@alumno.unam.mx",
                imagen = "https://i.pravatar.cc/150?img=12"
            ),
            Alumno(
                nombre = "Sofía Hernández Ruiz",
                cuenta = "319034567",
                correo = "sofia.hernandez@alumno.unam.mx",
                imagen = "https://i.pravatar.cc/150?img=9"
            ),
            Alumno(
                nombre = "Miguel Ángel Martínez",
                cuenta = "319045678",
                correo = "miguel.martinez@alumno.unam.mx",
                imagen = "https://i.pravatar.cc/150?img=15"
            )
        )

        for (alumno in alumnosSemilla) {
            val values = ContentValues().apply {
                put(COLUMN_NOMBRE, alumno.nombre)
                put(COLUMN_CUENTA, alumno.cuenta)
                put(COLUMN_CORREO, alumno.correo)
                put(COLUMN_IMAGEN, alumno.imagen)
            }
            db?.insert(TABLE_ALUMNOS, null, values)
        }
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     * @return El ID asignado al nuevo registro o -1 en caso de error.
     */
    fun insertarAlumno(alumno: Alumno): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        return db.insert(TABLE_ALUMNOS, null, values)
    }

    /**
     * Recupera todos los alumnos ordenados alfabéticamente por nombre.
     * Cierra el Cursor de forma segura mediante use {}.
     */
    fun obtenerAlumnos(): List<Alumno> {
        val lista = mutableListOf<Alumno>()
        val db = readableDatabase
        val query = "SELECT $COLUMN_ID, $COLUMN_NOMBRE, $COLUMN_CUENTA, $COLUMN_CORREO, $COLUMN_IMAGEN FROM $TABLE_ALUMNOS ORDER BY $COLUMN_NOMBRE ASC"

        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                val idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID)
                val nombreIndex = cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)
                val cuentaIndex = cursor.getColumnIndexOrThrow(COLUMN_CUENTA)
                val correoIndex = cursor.getColumnIndexOrThrow(COLUMN_CORREO)
                val imagenIndex = cursor.getColumnIndexOrThrow(COLUMN_IMAGEN)

                do {
                    val id = cursor.getInt(idIndex)
                    val nombre = cursor.getString(nombreIndex)
                    val cuenta = cursor.getString(cuentaIndex)
                    val correo = cursor.getString(correoIndex)
                    val imagen = cursor.getString(imagenIndex)

                    lista.add(Alumno(id, nombre, cuenta, correo, imagen))
                } while (cursor.moveToNext())
            }
        }
        return lista
    }

    /**
     * Actualiza la información de un alumno existente usando consultas parametrizadas.
     * @return El número de filas afectadas.
     */
    fun actualizarAlumno(alumno: Alumno): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, alumno.nombre)
            put(COLUMN_CUENTA, alumno.cuenta)
            put(COLUMN_CORREO, alumno.correo)
            put(COLUMN_IMAGEN, alumno.imagen)
        }
        // Consulta parametrizada para prevenir inyecciones SQL
        return db.update(TABLE_ALUMNOS, values, "$COLUMN_ID = ?", arrayOf(alumno.id.toString()))
    }

    /**
     * Elimina un alumno por su ID utilizando consultas parametrizadas.
     * @return El número de filas eliminadas.
     */
    fun eliminarAlumno(id: Int): Int {
        val db = writableDatabase
        return db.delete(TABLE_ALUMNOS, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    /**
     * Verifica si un número de cuenta ya está registrado.
     * Permite ignorar un ID específico (útil al editar para no detectar la misma cuenta del propio alumno).
     */
    fun existeCuenta(cuenta: String, idIgnorar: Int = -1): Boolean {
        val db = readableDatabase
        val query: String
        val selectionArgs: Array<String>

        if (idIgnorar != -1) {
            query = "SELECT 1 FROM $TABLE_ALUMNOS WHERE $COLUMN_CUENTA = ? AND $COLUMN_ID != ?"
            selectionArgs = arrayOf(cuenta, idIgnorar.toString())
        } else {
            query = "SELECT 1 FROM $TABLE_ALUMNOS WHERE $COLUMN_CUENTA = ?"
            selectionArgs = arrayOf(cuenta)
        }

        db.rawQuery(query, selectionArgs).use { cursor ->
            return cursor.count > 0
        }
    }
}
