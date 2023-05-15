package com.proyecto.libd.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libd.R
import com.proyecto.libd.model.Libro

class LibroAdapter(var lista: ArrayList<Libro>,
                   ): RecyclerView.Adapter<LibroViewHolder>() {
//    var onItemSelected: (Libro) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibroViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.libro_layout, parent, false)
        return LibroViewHolder(v)
    }

    override fun onBindViewHolder(holder: LibroViewHolder, position: Int) {
        holder.render(lista[position]) //onItemSelected
    }

    override fun getItemCount() = lista.size
}

