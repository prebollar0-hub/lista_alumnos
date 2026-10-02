package com.example.listaalumnos.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.listaalumnos.R
import com.example.listaalumnos.databinding.ItemPersonaBinding
import com.example.listaalumnos.model.Alumno

/**
 * Adaptador del RecyclerView para el listado de Alumnos.
 * Utiliza ViewBinding (ItemPersonaBinding) y Glide para cargar imágenes dinámicas.
 *
 * @property listaAlumnos Lista de elementos a mostrar.
 * @property onEditClick Callback lambda ejecutado al presionar el botón Editar.
 * @property onDeleteClick Callback lambda ejecutado al presionar el botón Eliminar.
 */
class AlumnoAdapter(
    private var listaAlumnos: List<Alumno> = emptyList(),
    private val onEditClick: (Alumno) -> Unit,
    private val onDeleteClick: (Alumno) -> Unit
) : RecyclerView.Adapter<AlumnoAdapter.AlumnoViewHolder>() {

    /**
     * ViewHolder que encapsula el binding del ítem individual (item_persona.xml).
     */
    inner class AlumnoViewHolder(private val binding: ItemPersonaBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(alumno: Alumno) {
            binding.tvNombre.text = alumno.nombre
            binding.tvCuenta.text = binding.root.context.getString(R.string.account_label_format, alumno.cuenta)
            binding.tvCorreo.text = alumno.correo

            // Carga de foto de perfil usando Glide con placeholder e imagen en caso de error
            Glide.with(binding.root.context)
                .load(alumno.imagen)
                .placeholder(R.drawable.ic_person_placeholder)
                .error(R.drawable.ic_person_placeholder)
                .centerCrop()
                .into(binding.imgAlumno)

            // Listeners para las acciones de edición y eliminación
            binding.btnEdit.setOnClickListener { onEditClick(alumno) }
            binding.btnDelete.setOnClickListener { onDeleteClick(alumno) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlumnoViewHolder {
        val binding = ItemPersonaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AlumnoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlumnoViewHolder, position: Int) {
        holder.bind(listaAlumnos[position])
    }

    override fun getItemCount(): Int = listaAlumnos.size

    /**
     * Método para actualizar el conjunto de datos y refrescar la vista.
     */
    fun updateLista(nuevaLista: List<Alumno>) {
        this.listaAlumnos = nuevaLista
        notifyDataSetChanged()
    }
}
