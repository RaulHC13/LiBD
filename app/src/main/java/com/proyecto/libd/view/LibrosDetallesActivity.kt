package com.proyecto.libd.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
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
import kotlin.math.round

class LibrosDetallesActivity : AppCompatActivity() {

    lateinit var binding: LibrosDetallesLayoutBinding
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs
    lateinit var storage: FirebaseStorage

    private var titulo = ""
    private var emailFormateado = ""
    private var drawableResourceFav = 0
    private var drawableResourceEspera = 0
    private var drawableResourceLeidos = 0
    private var numValoraciones = 0
    private var oldValoracion = 0.0f
    private var newValoracion = 0.0f
    private var estaValorado = false

    lateinit var ivFavoritos: ImageView
    lateinit var ivEspera: ImageView
    lateinit var ivLeidos: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibrosDetallesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
        storage = FirebaseStorage.getInstance(getString(R.string.storageURL))
        prefs = Prefs(this)

        getLibro()
        setVariables()
        getEstado()
        setListeners()
    }

    private fun setListeners() {
        binding.ivDetallesVolver.setOnClickListener {
            finish()
        }

        binding.detallesRatingBarShow.onRatingBarChangeListener = object: RatingBar.OnRatingBarChangeListener {

            /**
             * Comprueba si el usuario ha valorado el libro antes, si es asi, no se suma 1 a numValoraciones
             * y se valora, si no es asi, se suma 1 y ademas se actualiza numValoraciones en la BD.
             * Se calcula la nueva valoracion y se llama a valorar para actualizarla en la BD.
             * La valoracion se redondea a un decimal.
             */
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, fromUser: Boolean) {
                if (estaValorado) {
                    newValoracion = oldValoracion + (rating - oldValoracion) / numValoraciones
                    newValoracion = round(newValoracion * 10)/10

                    valorar(newValoracion)
                } else if (!estaValorado) {
                    numValoraciones++
                    newValoracion = oldValoracion + (rating - oldValoracion) / numValoraciones
                    newValoracion = round(newValoracion * 10)/10

                    updateNumValoraciones(numValoraciones)
                    valorar(newValoracion)
                    addValorados()
                }
            }
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
                ivEspera.setImageResource(R.drawable.ic_baseline_bookmark_24)
                drawableResourceEspera = R.drawable.ic_baseline_bookmark_24

            //Esta en la lista de espera
            } else if (drawableResourceEspera == R.drawable.ic_baseline_bookmark_24) {
                quitarListaEspera()
                ivEspera.setImageResource(R.drawable.baseline_bookmark_border_24)
                drawableResourceEspera = R.drawable.baseline_bookmark_border_24
            }
        }

        binding.ivDetallesLeidos.setOnClickListener {

            //No esta en la lista de leyendo
            if (drawableResourceLeidos == R.drawable.ic_baseline_menu_book_24) {
                addLeidos()
                ivLeidos.setImageResource(R.drawable.ic_baseline_menu_book_blue_24)
                drawableResourceLeidos = R.drawable.ic_baseline_menu_book_blue_24

            //Esta en la lista de leyendo
            } else if (drawableResourceLeidos == R.drawable.ic_baseline_menu_book_blue_24) {
                quitarLeidos()
                ivLeidos.setImageResource(R.drawable.ic_baseline_menu_book_24)
                drawableResourceLeidos = R.drawable.ic_baseline_menu_book_24
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
            Toast.makeText(this, "Se ha eliminado de favoritos", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Se ha eliminado de la lista de espera", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al eliminar de la lista de espera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addLeidos() {

        db.getReference("usuarios/$emailFormateado/listaLectura").child(titulo).setValue(titulo).addOnSuccessListener {
            Toast.makeText(this, "Se ha añadido a leidos", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a leidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun quitarLeidos() {

        db.getReference("usuarios/$emailFormateado/listaLectura").child(titulo).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Se ha eliminado de leidos", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al eliminar de leidos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addValorados() {

        db.getReference("usuarios/$emailFormateado/valorados").child(titulo).setValue(titulo).addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al añadir a valorados", Toast.LENGTH_LONG).show()
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
        numValoraciones = libro.numValoraciones
        oldValoracion = libro.valoracion

        binding.tvDetallesTitulo.text = titulo
        binding.tvDetallesPaginas.text = getString(R.string.numPaginas, libro.numPaginas)
        binding.tvDetallesAutor.text = getString(R.string.nombreAutor, libro.autor)
        binding.detallesRatingBarShow.rating = libro.valoracion!!

        ponerImagen(titulo)
    }

    private fun ponerImagen(titulo: String) {
        val ref = storage.reference
        val file = ref.child("portadas/$titulo/portada.jpg")
        binding.progressBarDetalles.visibility = View.VISIBLE

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                rellenarImagen(uri)
                binding.progressBarDetalles.visibility = View.GONE
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/portada.jpg")
            defaultImg.downloadUrl.addOnSuccessListener {
                rellenarImagen(it)
                binding.progressBarDetalles.visibility = View.GONE
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

        checkLeidoExiste { exists ->
            runOnUiThread {
                if (exists) {
                    ivLeidos.setImageResource(R.drawable.ic_baseline_menu_book_blue_24)
                    drawableResourceLeidos = R.drawable.ic_baseline_menu_book_blue_24
                } else {
                    ivLeidos.setImageResource(R.drawable.ic_baseline_menu_book_24)
                    drawableResourceLeidos = R.drawable.ic_baseline_menu_book_24
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

        checkValoradoExiste { exists ->
            if (exists) estaValorado = true
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

    private fun checkLeidoExiste(callback: (Boolean) -> Unit) {
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

    private fun checkValoradoExiste(callback: (Boolean) -> Unit) {
        val ref = db.getReference("usuarios/$emailFormateado/valorados").child(titulo)

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val existe = snapshot.exists()
                callback(existe)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }

    /**
     * Hace un update del campo valoracion utilizando un mapa, guarda la nueva valoracion
     * y pone la bandera a true para que no se sume 1 a numValoraciones en el futuro.
     */
    private fun valorar(newValoracion: Float) {
        val ref = db.getReference("libros/$titulo")
        val update = mapOf("valoracion" to newValoracion)

        ref.updateChildren(update).addOnSuccessListener {
            if(estaValorado){
                Toast.makeText(this, "Se ha actualizado la valoracion", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Se ha valorado el libro", Toast.LENGTH_SHORT).show()
            }
            estaValorado = true
        }.addOnFailureListener {
            Toast.makeText(this, "Ha ocurrido un error al valorar el libro", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNumValoraciones(numValoraciones: Int) {
        val ref = db.getReference("libros/$titulo")
        val update = mapOf("numValoraciones" to numValoraciones)
        ref.updateChildren(update).addOnFailureListener {
            Toast.makeText(this, "ERROR EN UPDATE NUMVALORACIONES", Toast.LENGTH_LONG).show()
        }
    }

    private fun setVariables() {
        emailFormateado = prefs.getEmailFormateado().toString()
        drawableResourceFav = R.drawable.baseline_favorite_border_24
        drawableResourceEspera = R.drawable.baseline_bookmark_border_24
        drawableResourceLeidos = R.drawable.ic_baseline_menu_book_24
        ivFavoritos = binding.ivDetallesFavoritos
        ivEspera= binding.ivDetallesListaEspera
        ivLeidos= binding.ivDetallesLeidos
    }

    private fun rellenarImagen(uri: Uri?) {
        Glide.with(binding.ivDetallesPortada.context)
            .load(uri)
            .centerCrop()
            .into(binding.ivDetallesPortada)
    }
}