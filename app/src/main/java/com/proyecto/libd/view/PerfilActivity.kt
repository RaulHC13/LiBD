package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.libd.R
import com.example.libd.databinding.ActivityPerfilBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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

    lateinit var db: FirebaseDatabase
    lateinit var storage: FirebaseStorage
    lateinit var binding: ActivityPerfilBinding
    lateinit var prefs: Prefs

    private lateinit var email: String
    private lateinit var emailFormateado: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
        storage = FirebaseStorage.getInstance(binding.ivPerfil.resources.getString(R.string.storageURL))

        email = prefs.getEmail().toString()
        emailFormateado = prefs.getEmailFormateado().toString()
        binding.tvPerfilUsername.text = prefs.getUsername()

        getUsernameDB()
        imagenOnStart()
        setListeners()
    }

    private fun getUsernameDB() {
        val ref = db.getReference("usuarios/$emailFormateado")

        ref.child("username").addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.getValue(String::class.java)
                if (username != null) {
                    binding.tvPerfilUsername.text = username
                } else {
                    binding.tvPerfilUsername.text = prefs.getUsernameGenerado()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setListeners() {
        binding.ivPerfil.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnPerfil.setOnClickListener {
            cambiarUsername()
            finish()
        }
    }

    /**
     * Toma el texto y cambia el username en las sharedpreferences
     * En caso de dejar el campo vacio, se crea un username por defecto
     */
    private fun cambiarUsername() {
        var newUsername: String = binding.etPerfilUsername.text.toString()
        if (newUsername.isBlank() || newUsername.isEmpty()) newUsername = prefs.getUsernameGenerado() //Si se deja en blanco se pone uno por defecto.

        db.getReference("usuarios/$emailFormateado").child("username").setValue(newUsername).addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al cambiar el nombre de usuario", Toast.LENGTH_LONG).show()
        }
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
        binding.progressBarImagenPerfil.visibility = View.VISIBLE

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
                binding.progressBarImagenPerfil.visibility = View.GONE
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/perfil.jpg")
            defaultImg.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
                binding.progressBarImagenPerfil.visibility = View.GONE
            }
        }
    }
}