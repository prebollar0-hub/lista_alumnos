package com.example.listaalumnos

import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listaalumnos.data.DBHelperAlumno
import com.example.listaalumnos.databinding.ActivityMainBinding
import com.example.listaalumnos.model.Alumno
import com.example.listaalumnos.ui.FormAlumnoActivity
import com.example.listaalumnos.ui.adapter.AlumnoAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Pantalla Principal de la aplicación.
 * Muestra el listado de alumnos en un RecyclerView con datos desde SQLite,
 * gestiona la vista de estado vacío y coordina el flujo de agregar, editar y eliminar.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelperAlumno
    private lateinit var alumnoAdapter: AlumnoAdapter

    // Launcher para manejar los resultados al regresar del Formulario
    private val formActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cargarAlumnos()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DBHelperAlumno(this)

        setupRecyclerView()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Refrescar la lista de alumnos al retomar la pantalla
        cargarAlumnos()
    }

    private fun setupRecyclerView() {
        alumnoAdapter = AlumnoAdapter(
            onEditClick = { alumno -> editarAlumno(alumno) },
            onDeleteClick = { alumno -> confirmarEliminacion(alumno) }
        )

        binding.rvAlumnos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alumnoAdapter
        }
    }

    private fun setupListeners() {
        // Botón flotante para agregar alumno
        binding.fabAdd.setOnClickListener {
            val intent = FormAlumnoActivity.newIntentForAdd(this)
            formActivityResultLauncher.launch(intent)
        }
    }

    /**
     * Consulta la base de datos y actualiza la interfaz.
     * Muestra la vista de estado vacío si no existen registros.
     */
    private fun cargarAlumnos() {
        val listaAlumnos = dbHelper.obtenerAlumnos()

        if (listaAlumnos.isEmpty()) {
            binding.rvAlumnos.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
        } else {
            binding.rvAlumnos.visibility = View.VISIBLE
            binding.layoutEmpty.visibility = View.GONE
            alumnoAdapter.updateLista(listaAlumnos)
        }
    }

    /**
     * Inicia FormAlumnoActivity en modo edición con los datos del alumno seleccionado.
     */
    private fun editarAlumno(alumno: Alumno) {
        val intent = FormAlumnoActivity.newIntentForEdit(this, alumno)
        formActivityResultLauncher.launch(intent)
    }

    /**
     * Muestra un diálogo de confirmación MaterialAlertDialogBuilder previo a borrar el alumno.
     */
    private fun confirmarEliminacion(alumno: Alumno) {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.delete_dialog_title))
            .setMessage(getString(R.string.delete_dialog_message, alumno.nombre))
            .setPositiveButton(getString(R.string.btn_delete)) { _, _ ->
                eliminarAlumno(alumno)
            }
            .setNegativeButton(getString(R.string.btn_cancel), null)
            .show()
    }

    /**
     * Elimina el alumno de SQLite y muestra un Snackbar de confirmación.
     */
    private fun eliminarAlumno(alumno: Alumno) {
        val filasEliminadas = dbHelper.eliminarAlumno(alumno.id)
        if (filasEliminadas > 0) {
            Snackbar.make(
                binding.mainLayout,
                getString(R.string.msg_student_deleted),
                Snackbar.LENGTH_SHORT
            ).show()
            cargarAlumnos()
        }
    }
}
