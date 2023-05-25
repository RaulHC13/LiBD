package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.libd.R
import com.example.libd.databinding.ActivityPerfilBinding
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.Prefs

class PerfilActivity : AppCompatActivity() {

    /**
     * Launcher que se activa al hacer click sobre la imagen.
     * Abre la galeria y permite buscar y seleccionar una imagen.
     * Despues llama a los metodos para rellenar y guardar la imagen.
     */
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri != null) {
            binding.ivPerfil.setImageURI(uri)
            rellenarImagen(uri)
            guardarImagen(uri)
        }
    }

    lateinit var storage: FirebaseStorage
    lateinit var binding: ActivityPerfilBinding
    lateinit var prefs: Prefs
    private lateinit var email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)
        storage = FirebaseStorage.getInstance(binding.ivPerfil.resources.getString(R.string.storageURL))
        email = prefs.getEmail().toString()
        binding.tvPerfilUsername.text = prefs.getUsername()

        imagenOnStart()
        setListeners()
    }

    private fun setListeners() {
        binding.ivPerfil.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPerfil.setOnClickListener {
            cambiarUsername()
        }
    }

    /**
     * Toma el texto y cambia el username en las sharedpreferences
     * En caso de dejar el campo vacio, se crea un username por defecto
     */
    private fun cambiarUsername() {
        var newUsername: String
        newUsername = binding.etPerfilUsername.text.toString()
        if (newUsername.isBlank() || newUsername.isEmpty()) {
            prefs.setDefaultUsername() //Si se deja en blanco se pone uno por defecto.
        } else {
            prefs.setUsername(newUsername)
        }
        finish()
    }

    private fun guardarImagen(uri: Uri) {
        val ref = storage.reference
        val imagen = ref.child("perfiles/$email/perfil.jpg")
        val upload = imagen.putFile(uri).addOnFailureListener {
            println(it.message)
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        val requestOptions = RequestOptions().transform(CircleCrop())
        Glide.with(binding.ivPerfil.context)
            .load(uri)
            .centerCrop()
            .apply(requestOptions)
            .into(binding.ivPerfil)
    }

    /**
     * Metodo que se llama en onCreate. Carga una imagen en el imageView, si puede la descarga
     * de firebase y si no, carga una por defecto.
     */
    private fun imagenOnStart() {
        val ref = storage.reference
        val file = ref.child("perfiles/$email/perfil.jpg")
        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/perfil.jpg")
            defaultImg.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
            }
        }
    }
}