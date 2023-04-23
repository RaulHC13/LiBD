package com.proyecto.libd.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libd.R
import com.proyecto.libd.model.MensajeChat

class MensajeAdapter(var lista: ArrayList<MensajeChat>,
                     var email: String): RecyclerView.Adapter<MensajeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.mensaje_chat_layout, parent, false)
        return MensajeViewHolder(v)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        holder.render(lista[position], email)
    }

    override fun getItemCount() = lista.size
}