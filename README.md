# Práctica Académica: Control y Gestión de Alumnos

Aplicación Android nativa desarrollada en **Kotlin** para la gestión de alumnos con almacenamiento local en **SQLite** y presentación en un **RecyclerView** con imágenes dinámicas vía **Glide** y componentes modernos de **Material Design 3**.

---

## 📱 Descripción de la Práctica

Esta aplicación permite administrar un catálogo de alumnos registrados en una institución académica. Ofrece las operaciones básicas de **CRUD** (Crear, Leer, Actualizar y Eliminar) sobre una base de datos SQLite local, aplicando validaciones de formulario en tiempo real, manejo de imágenes dinámicas vía URL y avisos de confirmación previos a la eliminación.

---

## 🛠️ Tecnologías y Librerías Utilizadas

* **Lenguaje:** Kotlin
* **Diseño UI:** Layouts XML con Material Design 3 (sin Jetpack Compose)
* **Vinculación de Vistas:** View Binding (`ActivityMainBinding`, `ActivityFormAlumnoBinding`, `ItemPersonaBinding`)
* **Persistencia de Datos:** SQLite nativo mediante `SQLiteOpenHelper` (sin ORM Room)
* **Carga de Imágenes:** Glide 4.16.0 (con placeholder e imagen de error)
* **Componentes Material Design:** `MaterialCardView`, `ShapeableImageView`, `FloatingActionButton`, `TextInputLayout`, `MaterialAlertDialogBuilder`, `Snackbar`.
* **Manejo de Insets:** `EdgeToEdge` y compatibilidad con barras del sistema

---

## 🏗️ Arquitectura y Estructura del Proyecto

El código fuente está organizado en paquetes por responsabilidad:

```text
com.example.listaalumnos
 ├── data/
 │    └── DBHelperAlumno.kt       # Gestor SQLiteOpenHelper (CRUD y semilla de datos)
 ├── model/
 │    └── Alumno.kt               # Data class Alumno (id, nombre, cuenta, correo, imagen)
 └── ui/
      ├── adapter/
      │    └── AlumnoAdapter.kt   # RecyclerView.Adapter con ViewBinding y Glide
      ├── FormAlumnoActivity.kt   # Actividad para Agregar/Editar con validaciones
      └── MainActivity.kt         # Pantalla principal con RecyclerView y vista vacía
```

### 🔄 Flujo de Datos

```text
 [ Base de Datos SQLite ]
         │
         │ (dbHelper.obtenerAlumnos())
         ▼
 [ MainActivity / ActivityResult ]
         │
         │ (alumnoAdapter.updateLista())
         ▼
 [ AlumnoAdapter (ViewHolder + ViewBinding) ]
         │
         │ (Glide.load(imagen))
         ▼
 [ RecyclerView (item_persona.xml) ]
```

---

## ⚙️ Pasos para Ejecutar y Probar

1. **Requisitos:**
   * Android Studio Ladybug / Meerkat o superior.
   * Dispositivo físico o emulador con Android 8.0 (API 27) o superior.
   * Conexión a Internet activa (para descargar las fotos de perfil con Glide).

2. **Compilación y Ejecución:**
   * Abre el proyecto en Android Studio.
   * Sincroniza Gradle (`Sync Project with Gradle Files`).
   * Selecciona tu emulador o dispositivo y presiona **Run** (`Shift + F10`).

3. **Prueba de Funcionalidades:**
   * **Listar:** Al abrir la app por primera vez, la BD se creará automáticamente y cargará 4 alumnos de ejemplo con foto.
   * **Agregar:** Presiona el botón flotante `+ Agregar alumno`, completa todos los campos del formulario y presiona `Guardar Alumno`. Observa cómo la foto de vista previa se actualiza dinámicamente al ingresar una URL.
   * **Editar:** Presiona el ícono del lápiz en cualquier tarjeta de alumno, modifica sus datos y presiona `Guardar Alumno`.
   * **Eliminar:** Presiona el ícono del bote de basura en cualquier tarjeta, confirma en el diálogo que aparece y verifica la actualización de la lista y el mensaje Snackbar.
   * **Vista Vacía:** Si eliminas a todos los alumnos, se mostrará automáticamente una ilustración y un mensaje indicando que no hay registros.

---

## 📷 Capturas de Pantalla

| Lista de Alumnos | Formulario (Agregar/Editar) | Confirmación de Borrado |
| :---: | :---: | :---: |
| *(Agrega aquí captura de MainActivity)* | *(Agrega aquí captura de FormAlumnoActivity)* | *(Agrega aquí captura del Diálogo)* |
