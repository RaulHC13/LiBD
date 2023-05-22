package com.proyecto.libd.adapters

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libd.R
import com.example.libd.databinding.LibroLayoutBinding
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.model.Libro

class LibroViewHolder(v: View):RecyclerView.ViewHolder(v) {

    private val binding = LibroLayoutBinding.bind(v)
    var storage = FirebaseStorage.getInstance(binding.tvTitulo.resources.getString(R.string.storageURL))

    fun render(libro: Libro , onItemSelected: (Libro) -> Unit) {
        binding.tvTitulo.text = libro.titulo
        binding.ratingBarShow.rating = libro.valoracion!!

        binding.cardViewLibro.setOnClickListener {
            onItemSelected(libro)
        }
        ponerImagen(libro.titulo)
    }

    private fun ponerImagen(titulo: String) {
        val ref = storage.reference
        val file = ref.child("portadas/$titulo/portada.jpg")

        binding.progressBar.visibility = View.VISIBLE

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
                binding.progressBar.visibility = View.GONE
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/portada.jpg")
            defaultImg.downloadUrl.addOnSuccessListener {
                rellenarImagen(it)
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivPortada)
    }
}