package com.example.listaalumnos.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.example.listaalumnos.R
import com.example.listaalumnos.data.DBHelperAlumno
import com.example.listaalumnos.databinding.ActivityFormAlumnoBinding
import com.example.listaalumnos.model.Alumno

/**
 * Pantalla de Formulario reutilizable para Agregar un nuevo alumno o Editar uno existente.
 * Realiza validaciones estrictas en cada campo (campos requeridos, formato de correo y cuenta única en BD).
 */
class FormAlumnoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFormAlumnoBinding
    private lateinit var dbHelper: DBHelperAlumno

    private var studentId: Int = -1
    private var isEditMode: Boolean = false

    companion object {
        private const val EXTRA_ID = "EXTRA_ID"
        private const val EXTRA_NOMBRE = "EXTRA_NOMBRE"
        private const val EXTRA_CUENTA = "EXTRA_CUENTA"
        private const val EXTRA_CORREO = "EXTRA_CORREO"
        private const val EXTRA_IMAGEN = "EXTRA_IMAGEN"

        /**
         * Método helper para iniciar la actividad en modo Agregar.
         */
        fun newIntentForAdd(context: Context): Intent {
            return Intent(context, FormAlumnoActivity::class.java)
        }

        /**
         * Método helper para iniciar la actividad en modo Editar con datos precargados.
         */
        fun newIntentForEdit(context: Context, alumno: Alumno): Intent {
            return Intent(context, FormAlumnoActivity::class.java).apply {
                putExtra(EXTRA_ID, alumno.id)
                putExtra(EXTRA_NOMBRE, alumno.nombre)
                putExtra(EXTRA_CUENTA, alumno.cuenta)
                putExtra(EXTRA_CORREO, alumno.correo)
                putExtra(EXTRA_IMAGEN, alumno.imagen)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFormAlumnoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelperAlumno(this)

        // Verificar si recibimos parámetros de edición
        if (intent.hasExtra(EXTRA_ID)) {
            studentId = intent.getIntExtra(EXTRA_ID, -1)
            isEditMode = studentId != -1
        }

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        if (isEditMode) {
            binding.toolbarForm.title = getString(R.string.title_edit_student)
            binding.btnSave.text = getString(R.string.btn_save)

            // Precargar datos del alumno en los campos
            val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: ""
            val cuenta = intent.getStringExtra(EXTRA_CUENTA) ?: ""
            val correo = intent.getStringExtra(EXTRA_CORREO) ?: ""
            val imagen = intent.getStringExtra(EXTRA_IMAGEN) ?: ""

            binding.etNombre.setText(nombre)
            binding.etCuenta.setText(cuenta)
            binding.etCorreo.setText(correo)
            binding.etImagen.setText(imagen)

            cargarVistaPrevia(imagen)
        } else {
            binding.toolbarForm.title = getString(R.string.title_add_student)
        }
    }

    private fun setupListeners() {
        // Navegación hacia atrás
        binding.toolbarForm.setNavigationOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }

        // Actualizar vista previa de la foto en tiempo real conforme el usuario escribe la URL
        binding.etImagen.doAfterTextChanged { text ->
            cargarVistaPrevia(text.toString().trim())
        }

        // Botón Guardar
        binding.btnSave.setOnClickListener {
            guardarAlumno()
        }
    }

    /**
     * Carga la foto en el ImageView de vista previa mediante Glide.
     */
    private fun cargarVistaPrevia(url: String) {
        if (url.isNotEmpty()) {
            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .centerCrop()
                .into(binding.imgPreview)
        } else {
            binding.imgPreview.setImageResource(R.drawable.ic_person_placeholder)
        }
    }

    /**
     * Valida todos los campos e inserta o actualiza en la base de datos SQLite.
     */
    private fun guardarAlumno() {
        // Limpiar errores previos
        binding.tilNombre.error = null
        binding.tilCuenta.error = null
        binding.tilCorreo.error = null
        binding.tilImagen.error = null

        val nombre = binding.etNombre.text.toString().trim()
        val cuenta = binding.etCuenta.text.toString().trim()
        val correo = binding.etCorreo.text.toString().trim()
        val imagen = binding.etImagen.text.toString().trim()

        var isValid = true

        // Validación: Nombre obligatorio
        if (nombre.isEmpty()) {
            binding.tilNombre.error = getString(R.string.error_field_required)
            isValid = false
        }

        // Validación: Cuenta numérica, no vacía y de al menos 6 dígitos
        if (cuenta.isEmpty()) {
            binding.tilCuenta.error = getString(R.string.error_field_required)
            isValid = false
        } else if (!cuenta.matches(Regex("^[0-9]{6,10}$"))) {
            binding.tilCuenta.error = getString(R.string.error_invalid_account)
            isValid = false
        } else if (dbHelper.existeCuenta(cuenta, if (isEditMode) studentId else -1)) {
            // Validación: No duplicar el número de cuenta en la BD
            binding.tilCuenta.error = getString(R.string.error_duplicate_account)
            isValid = false
        }

        // Validación: Correo obligatorio y formato válido
        if (correo.isEmpty()) {
            binding.tilCorreo.error = getString(R.string.error_field_required)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            binding.tilCorreo.error = getString(R.string.error_invalid_email)
            isValid = false
        }

        // Validación: URL de Imagen obligatoria
        if (imagen.isEmpty()) {
            binding.tilImagen.error = getString(R.string.error_field_required)
            isValid = false
        }

        if (!isValid) return

        val alumno = Alumno(
            id = if (isEditMode) studentId else 0,
            nombre = nombre,
            cuenta = cuenta,
            correo = correo,
            imagen = imagen
        )

        val exito: Boolean = if (isEditMode) {
            val filasAfectadas = dbHelper.actualizarAlumno(alumno)
            filasAfectadas > 0
        } else {
            val idInsertado = dbHelper.insertarAlumno(alumno)
            idInsertado != -1L
        }

        if (exito) {
            val mensaje = if (isEditMode) getString(R.string.msg_student_updated) else getString(R.string.msg_student_saved)
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, getString(R.string.msg_error_saving), Toast.LENGTH_SHORT).show()
        }
    }
}
