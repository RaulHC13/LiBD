package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.databinding.FragmentFavoritosBinding
import com.example.libd.databinding.FragmentLeyendoBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class LeyendoFragment : Fragment() {

    private lateinit var binding: FragmentLeyendoBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs

    private var listaNombresLeyendo = ArrayList<String>()
    private var listaLeyendo = ArrayList<Libro>()
    private var emailFormateado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeyendoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        emailFormateado = prefs.getEmailFormateado().toString()
        binding.tvLeyendoFragment.visibility = if (listaLeyendo.isEmpty()) View.VISIBLE else View.INVISIBLE

        getLeyendoNombres()
        getLeyendo()
        setRecycler()
    }

    /**
     * Recoger en una lista el nombre de todos los libros en lista de lectura.
     */
    private fun getLeyendoNombres() {
        db.getReference("usuarios/$emailFormateado/listaLectura").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaNombresLeyendo.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val nombre = item.getValue(String::class.java)
                        if (nombre != null) {
                            listaNombresLeyendo.add(nombre)
                        }
                    }
                }
                getLeyendo()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Se hace una query buscando todos los libros de la db y, si el titulo de un libro
     * existe en la listaNombresLeyendo, se añade a listaLeyendo.
     */
    private fun getLeyendo() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaLeyendo.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (listaNombresLeyendo.contains(libro?.titulo)) {
                            if (libro != null) {
                                listaLeyendo.add(libro)
                            }
                        }
                    }
                    adapter.lista = listaLeyendo
                    adapter.notifyDataSetChanged()
                }
                if (listaLeyendo.isEmpty()) {
                    binding.tvLeyendoFragment.visibility = View.VISIBLE
                } else {
                    binding.tvLeyendoFragment.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerValoraciones.layoutManager = layoutManager

        adapter = LibroAdapter(listaLeyendo) { onItemSelected(it) }
        binding.recyclerValoraciones.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}