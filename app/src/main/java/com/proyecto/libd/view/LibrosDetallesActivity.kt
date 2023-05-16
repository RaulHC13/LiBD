package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.libd.R
import com.example.libd.databinding.LibrosDetallesLayoutBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.model.Libro

class LibrosDetallesActivity : AppCompatActivity() {

    lateinit var binding: LibrosDetallesLayoutBinding
    lateinit var db: FirebaseDatabase
    val storage = FirebaseStorage.getInstance("gs://libd-96d39.appspot.com/")

    private var titulo = ""
    var drawableResourceFav = 0
    var drawableResourceEspera = 0
    var drawableResourceBook = 0

    lateinit var libroCarga: Libro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibrosDetallesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
        drawableResourceFav = R.drawable.baseline_favorite_border_24
        drawableResourceEspera = R.drawable.baseline_bookmark_border_24
        drawableResourceBook = R.drawable.ic_baseline_menu_book_24


        getLibro()
        setListeners()
    }

    private fun setListeners() {
        binding.ivDetallesVolver.setOnClickListener {
            finish()
        }

        binding.ivDetallesFavoritos.setOnClickListener {
            var ivFavoritos = binding.ivDetallesFavoritos
            if (drawableResourceFav == R.drawable.baseline_favorite_border_24) {
                ivFavoritos.setImageResource(R.drawable.ic_baseline_favorite_24)
                drawableResourceFav = R.drawable.ic_baseline_favorite_24

            } else if (drawableResourceFav == R.drawable.ic_baseline_favorite_24) {
                ivFavoritos.setImageResource(R.drawable.baseline_favorite_border_24)
                drawableResourceFav = R.drawable.baseline_favorite_border_24
            }
        }

        binding.ivDetallesListaEspera.setOnClickListener {
            var ivEspera= binding.ivDetallesListaEspera
            if (drawableResourceEspera == R.drawable.baseline_bookmark_border_24) {
                ivEspera.setImageResource(R.drawable.ic_baseline_bookmark_24)
                drawableResourceEspera = R.drawable.ic_baseline_bookmark_24

            } else if (drawableResourceEspera == R.drawable.ic_baseline_bookmark_24) {
                ivEspera.setImageResource(R.drawable.baseline_bookmark_border_24)
                drawableResourceEspera = R.drawable.baseline_bookmark_border_24
            }
        }

        binding.ivDetallesLeyendo.setOnClickListener {
            var ivLeyendo= binding.ivDetallesLeyendo
            if (drawableResourceBook == R.drawable.ic_baseline_menu_book_24) {
                ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_blue_24)
                drawableResourceBook = R.drawable.ic_baseline_menu_book_blue_24

            } else if (drawableResourceBook == R.drawable.ic_baseline_menu_book_blue_24) {
                ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_24)
                drawableResourceBook = R.drawable.ic_baseline_menu_book_24
            }
        }
    }

    private fun getLibro() {
        val datos = intent.extras
        val libro = datos?.getSerializable("LIBRO") as Libro

        titulo = libro.titulo
        binding.tvDetallesTitulo.text = titulo
        binding.tvDetallesPaginas.text = getString(R.string.numPaginas, libro.numPaginas)
        binding.tvDetallesAutor.text = libro.autor
        binding.detallesRatingBarShow.rating = libro.valoracion!!

        ponerImagen(titulo)
    }

    private fun ponerImagen(titulo: String) {
        val ref = storage.reference
        val file = ref.child("portadas/$titulo/portada.jpg")

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/portada.jpg")
            defaultImg.downloadUrl.addOnSuccessListener {
                rellenarImagen(it)
            }
        }
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivDetallesPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivDetallesPortada)
    }
}