package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.R
import com.example.libd.databinding.FragmentFavoritosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class FavoritosFragment : Fragment() {

    private lateinit var binding: FragmentFavoritosBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs

    private var listaNombresFavoritos = ArrayList<String>()
    private var listaFavoritos = ArrayList<Libro>()
    private var emailFormateado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        emailFormateado = prefs.getEmailFormateado().toString()
        binding.tvFavoritosFragment.visibility = if (listaFavoritos.isEmpty()) View.VISIBLE else View.INVISIBLE

        getFavoritosNombres()
        getFavoritos()
        setRecycler()
    }

    /**
     * Recoger en una lista el nombre de todos los libros favoritos por usuario.
     */
    private fun getFavoritosNombres() {
        db.getReference("usuarios/$emailFormateado/favoritos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaNombresFavoritos.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val nombre = item.getValue(String::class.java)
                        if (nombre != null) {
                            listaNombresFavoritos.add(nombre)
                        }
                    }
                }
                getFavoritos()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Se hace una query buscando todos los libros de la db y, si el titulo de un libro
     * existe en la listaNombresFavoritos, se añade a listaFavoritos.
     */
    private fun getFavoritos() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaFavoritos.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (listaNombresFavoritos.contains(libro?.titulo)) {
                            if (libro != null) {
                                listaFavoritos.add(libro)
                            }
                        }
                    }
                    adapter.lista = listaFavoritos
                    adapter.notifyDataSetChanged()
                }

                if (listaFavoritos.isEmpty()) {
                    binding.tvFavoritosFragment.visibility = View.VISIBLE
                } else {
                    binding.tvFavoritosFragment.visibility = View.INVISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerFavoritos.layoutManager = layoutManager

        adapter = LibroAdapter(listaFavoritos) { onItemSelected(it) }
        binding.recyclerFavoritos.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}