package com.proyecto.libd.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.libd.R
import com.example.libd.databinding.LibroLayoutBinding
import com.proyecto.libd.model.Libro

class LibroViewHolder(v: View):RecyclerView.ViewHolder(v) {

    private val binding = LibroLayoutBinding.bind(v)

    fun render(libro: Libro) {
        binding.tvTitulo.text = libro.titulo.toString()
        binding.tvPaginas.text = String.format(binding.tvPaginas.context.getString(R.string.numPaginas), libro.autor.toString())
        binding.tvAutor.text = libro.autor.toString()
        binding.ratingBarShow.rating = libro.valoracion
    }
}