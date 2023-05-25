package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.libd.R
import com.example.libd.databinding.ActivityCrearBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.Prefs
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
    lateinit var prefs: Prefs

    private var titulo = ""
    private var numPaginas = 0
    private var autor = ""
    private var valoracion = 0.0f
    private var numValoraciones = 0
    private var emailFormateado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
        storage = FirebaseStorage.getInstance(binding.etAutor.resources.getString(R.string.storageURL))
        prefs = Prefs(this)

        emailFormateado = prefs.getEmailFormateado().toString()
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

    /**
     * Crea el libro a partir de la informacion recogida de los campos obligatorios y no obligatorios
     * en caso de existir. Los sube a la base de datos.
     */
    private fun crearLibro() {

        if (!comprobarDatos()) return
        val libro = Libro(titulo = titulo, numPaginas = numPaginas, autor = autor, valoracion = valoracion, numValoraciones = numValoraciones)

        if (numValoraciones != 0) addValorados()

        val ref = db.getReference("libros")
        ref.child(libro.titulo).setValue(libro).addOnSuccessListener {
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Comprueba que los datos han sido cumplimentados apropiadamente.
     */
    private fun comprobarDatos(): Boolean {

        titulo = binding.etNombre.text.toString().trim()
        if (titulo.length < 2 || titulo.length > 32) {
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

        if (!autor.isNullOrBlank() && (autor.length < 2 || autor.length > 32)){
            binding.etAutor.requestFocus()
            binding.etAutor.error = "Autor no valido"
            return false
        }

        if (autor.isNullOrBlank()) autor = "Desconocido"

        valoracion = binding.ratingBar.rating
        if (valoracion != 0.0f) numValoraciones++

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

    private fun addValorados() {

        db.getReference("usuarios/$emailFormateado/valorados").child(titulo).setValue(titulo).addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a valorados", Toast.LENGTH_LONG).show()
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivCrearPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivCrearPortada)
    }
}