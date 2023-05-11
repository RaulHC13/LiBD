package com.proyecto.libd.adapters

import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.libd.R
import com.example.libd.databinding.MensajeChatLayoutBinding
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.model.MensajeChat
import java.text.SimpleDateFormat
import java.util.*

class MensajeViewHolder(v: View): RecyclerView.ViewHolder(v) {
    val binding = MensajeChatLayoutBinding.bind(v)
    val storage = FirebaseStorage.getInstance("gs://libd-96d39.appspot.com/")

    fun render(mensaje: MensajeChat, email: String) {
        binding.tvMensajeCorreo.text = mensaje.email
        binding.tvMensajeTexto.text = mensaje.texto
        binding.tvMensajeFecha.text = convertLongToDate(mensaje.fecha)

        if(mensaje.email == email){
            binding.cLayoutMensaje.setBackgroundColor(ContextCompat.getColor(binding.cLayoutMensaje.context, R.color.colorChat1))
        } else {
            binding.cLayoutMensaje.setBackgroundColor(ContextCompat.getColor(binding.cLayoutMensaje.context, R.color.colorChat2))
        }
        ponerImagen(mensaje.email.toString())
    }

    private fun ponerImagen(email: String) {
        val ref = storage.reference
        val file = ref.child("perfiles/$email/perfil.jpg")

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/perfil.jpg")
            defaultImg.downloadUrl.addOnSuccessListener {
                rellenarImagen(it)
            }
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        val requestOptions = RequestOptions().transform(CircleCrop())
        Glide.with(binding.ivMensajePerfil.context)
            .load(uri)
            .centerCrop()
            .apply(requestOptions)
            .into(binding.ivMensajePerfil)
    }

    private fun convertLongToDate(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("HH:mm-dd/MM/yyyy")
        return format.format(date)
    }
}