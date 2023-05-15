package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.libd.databinding.ActivityCrearBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.model.Libro

class CrearActivity : AppCompatActivity() {

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
        if (uri != null) {
            binding.ivCrearPortada.setImageURI(uri)
            rellenarImagen(uri)
            guardarImagen(uri)
        }
    }

    lateinit var binding: ActivityCrearBinding
    lateinit var db: FirebaseDatabase
    lateinit var storage: FirebaseStorage

    private var titulo = ""
    private var numPaginas = 0
    private var autor = ""
    private var valoracion = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
        storage = FirebaseStorage.getInstance("gs://libd-96d39.appspot.com/")

        setListeners()
    }

    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }

        binding.btnCrear.setOnClickListener {
            crearLibro()
        }
        binding.ivCrearPortada.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun crearLibro() {

        if (!comprobarDatos()) return
        val libro = Libro(titulo = titulo, numPaginas = numPaginas, autor = autor, valoracion = valoracion)

        val ref = db.getReference("libros")
        ref.child(libro.titulo).setValue(libro).addOnSuccessListener {
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun comprobarDatos(): Boolean {

        titulo = binding.etNombre.text.toString().trim()
        if (titulo.length < 2 || titulo.length > 22) {
            binding.etNombre.requestFocus()
            binding.etNombre.error = "Titulo no válido"
            return false
        }

        var aux = binding.etPaginas.text.toString()
        if (aux.length <= 1) {
            binding.etPaginas.requestFocus()
            binding.etPaginas.error = "El libro debe tener al menos 10 paginas"
            return false
        }

        autor = binding.etAutor.text.toString().trim()
        if (autor.isNullOrBlank()) autor = "Desconocido"


        valoracion = binding.ratingBar.rating
        numPaginas = binding.etPaginas.text.toString().toInt()
        return true
    }

    private fun guardarImagen(uri: Uri) {
        titulo = binding.etNombre.text.toString().trim()
        val ref = storage.reference
        val imagen = ref.child("portadas/$titulo/portada.jpg")
        val upload = imagen.putFile(uri).addOnFailureListener {
            println(it.message)
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivCrearPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivCrearPortada)
    }

//    private fun <T: java.io.Serializable?> getSerializable(intent: Intent?, key: String, clase: Class<T>): T {
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            intent?.getSerializableExtra(key, clase)!!
//        } else {
//            intent?.getSerializableExtra(key) as T
//        }
//    }
}