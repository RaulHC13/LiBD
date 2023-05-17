package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.libd.R
import com.example.libd.databinding.LibrosDetallesLayoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.Prefs
import com.proyecto.libd.model.Libro

class LibrosDetallesActivity : AppCompatActivity() {

    lateinit var binding: LibrosDetallesLayoutBinding
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs
    val storage = FirebaseStorage.getInstance("gs://libd-96d39.appspot.com/")

    private var titulo = ""
    private var emailFormateado = ""

    var drawableResourceFav = 0
    var drawableResourceEspera = 0
    var drawableResourceLeyendo = 0
    lateinit var ivFavoritos: ImageView
    lateinit var ivEspera: ImageView
    lateinit var ivLeyendo: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibrosDetallesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
        prefs = Prefs(this)
        emailFormateado = prefs.getEmailFormateado().toString()

        getLibro()
        setVariables()
        getEstado()
        setListeners()
    }



    private fun setListeners() {
        binding.ivDetallesVolver.setOnClickListener {
            finish()
        }

        binding.ivDetallesFavoritos.setOnClickListener {

            /**
             * Al presionar cualquiera de los ivFavoritos, ivLeyendo, ivEspera, se comprueba
             * su estado, si estan o no presionados, si no lo estan, añaden el libro a sus respectivas
             * listas y cambian su icono, si no, lo borran de la base de datos y cambian su icono.
             */

            //No favorito
            if (drawableResourceFav == R.drawable.baseline_favorite_border_24) {
                addFavorito()
                ivFavoritos.setImageResource(R.drawable.ic_baseline_favorite_24)
                drawableResourceFav = R.drawable.ic_baseline_favorite_24

                //Es favorito
            } else if (drawableResourceFav == R.drawable.ic_baseline_favorite_24) {
                quitarFavorito()
                ivFavoritos.setImageResource(R.drawable.baseline_favorite_border_24)
                drawableResourceFav = R.drawable.baseline_favorite_border_24
            }
        }

        binding.ivDetallesListaEspera.setOnClickListener {

            //No esta en la lista de espera
            if (drawableResourceEspera == R.drawable.baseline_bookmark_border_24) {
                addListaEspera()
                Toast.makeText(this, "Guardado $titulo", Toast.LENGTH_SHORT).show()
                ivEspera.setImageResource(R.drawable.ic_baseline_bookmark_24)
                drawableResourceEspera = R.drawable.ic_baseline_bookmark_24

                //Esta en la lista de espera
            } else if (drawableResourceEspera == R.drawable.ic_baseline_bookmark_24) {
                quitarListaEspera()
                Toast.makeText(this, "Borrado $titulo", Toast.LENGTH_SHORT).show()
                ivEspera.setImageResource(R.drawable.baseline_bookmark_border_24)
                drawableResourceEspera = R.drawable.baseline_bookmark_border_24
            }
        }

        binding.ivDetallesLeyendo.setOnClickListener {

            //No esta en la lista de leyendo
            if (drawableResourceLeyendo == R.drawable.ic_baseline_menu_book_24) {
                addLeyendo()
                ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_blue_24)
                drawableResourceLeyendo = R.drawable.ic_baseline_menu_book_blue_24

                //Esta en la lista de leyendo
            } else if (drawableResourceLeyendo == R.drawable.ic_baseline_menu_book_blue_24) {
                quitarLeyendo()
                ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_24)
                drawableResourceLeyendo = R.drawable.ic_baseline_menu_book_24
            }
        }
    }

    /**
     * Añaden y eliminan libros de favoritos, lista de espera y lista de lectura, se identifican
     * por el titulo del libro y solo contienen el propio titulo.
     */
    private fun addFavorito() {

        db.getReference("usuarios/$emailFormateado/favoritos").child(titulo).setValue(titulo).addOnSuccessListener {
            Toast.makeText(this, "Se ha añadido a favoritos", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a favoritos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun quitarFavorito() {

        db.getReference("usuarios/$emailFormateado/favoritos").child(titulo).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Se ha eliminado de favoritos,", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al eliminar de favoritos,", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addListaEspera() {

        db.getReference("usuarios/$emailFormateado/listaEspera").child(titulo).setValue(titulo).addOnSuccessListener {
            Toast.makeText(this, "Se ha añadido a la lista de espera", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a la lista de espera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun quitarListaEspera() {

        db.getReference("usuarios/$emailFormateado/listaEspera").child(titulo).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Se ha eliminado de la lista de espera,", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al eliminar de la lista de espera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addLeyendo() {

        db.getReference("usuarios/$emailFormateado/listaLectura").child(titulo).setValue(titulo).addOnSuccessListener {
            Toast.makeText(this, "Se ha añadido a la lista de lectura", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a la lista de lectura", Toast.LENGTH_SHORT).show()
        }
    }

    private fun quitarLeyendo() {

        db.getReference("usuarios/$emailFormateado/listaLectura").child(titulo).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Se ha eliminado de la lista de lectura", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al eliminar de la lista de lectura,", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Toma los datos del intent y los pinta en pantalla, tambien le pasa a ponerImagen el titulo
     * para que pinte la imagen utilizando storage
     */
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


    /**
     * Llama a checkFavoritoExiste, checkLeyendoExiste y checkListaEsperaExiste, dependiendo del
     * valor devuelto, pinta los imageView con un icono u otro.
     */
    private fun getEstado() {
        ivFavoritos = binding.ivDetallesFavoritos

        checkFavoritoExiste { exists ->
            runOnUiThread {
                if (exists) {
                    ivFavoritos.setImageResource(R.drawable.ic_baseline_favorite_24)
                    drawableResourceFav = R.drawable.ic_baseline_favorite_24
                } else {
                    ivFavoritos.setImageResource(R.drawable.baseline_favorite_border_24)
                    drawableResourceFav = R.drawable.baseline_favorite_border_24
                }
            }
        }

        checkLeyendoExiste { exists ->
            runOnUiThread {
                if (exists) {
                    ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_blue_24)
                    drawableResourceLeyendo = R.drawable.ic_baseline_menu_book_blue_24
                } else {
                    ivLeyendo.setImageResource(R.drawable.ic_baseline_menu_book_24)
                    drawableResourceLeyendo = R.drawable.ic_baseline_menu_book_24
                }
            }
        }

        checkListaEsperaExiste { exists ->
            runOnUiThread {
                if (exists) {
                    ivEspera.setImageResource(R.drawable.ic_baseline_bookmark_24)
                    drawableResourceEspera = R.drawable.ic_baseline_bookmark_24
                } else {
                    ivEspera.setImageResource(R.drawable.baseline_bookmark_border_24)
                    drawableResourceEspera = R.drawable.baseline_bookmark_border_24
                }
            }
        }

    }


    /**
     * Comprueba si el titulo existe en la base de datos, en favoritos, lista leyendo o lista de espera.
     * Toma un callback como parametro y dependiendo de si el libro esta en la bd o no devuelve true o false.
     */
    private fun checkFavoritoExiste(callback: (Boolean) -> Unit) {
        val favoritosRef = db.getReference("usuarios/$emailFormateado/favoritos").child(titulo)

        favoritosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun checkLeyendoExiste(callback: (Boolean) -> Unit) {
        val favoritosRef = db.getReference("usuarios/$emailFormateado/listaLectura").child(titulo)

        favoritosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun checkListaEsperaExiste(callback: (Boolean) -> Unit) {
        val favoritosRef = db.getReference("usuarios/$emailFormateado/listaEspera").child(titulo)

        favoritosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val exists = snapshot.exists()
                callback(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    private fun setVariables() {
        drawableResourceFav = R.drawable.baseline_favorite_border_24
        drawableResourceEspera = R.drawable.baseline_bookmark_border_24
        drawableResourceLeyendo = R.drawable.ic_baseline_menu_book_24
        ivFavoritos = binding.ivDetallesFavoritos
        ivEspera= binding.ivDetallesListaEspera
        ivLeyendo= binding.ivDetallesLeyendo
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivDetallesPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivDetallesPortada)
    }
}