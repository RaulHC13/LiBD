package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.R
import com.example.libd.databinding.FragmentListaEsperaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class ListaEsperaFragment : Fragment() {

    private lateinit var binding: FragmentListaEsperaBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs

    private var listaNombresEspera = ArrayList<String>()
    private var listaEspera = ArrayList<Libro>()
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
        binding = FragmentListaEsperaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        emailFormateado = prefs.getEmailFormateado().toString()
        binding.tvListaEsperaFragment.visibility = if (listaEspera.isEmpty()) View.VISIBLE else View.INVISIBLE

        getEsperaNombres()
        getListaEspera()
        setRecycler()
    }

    /**
     * Recoge en una lista el nombre de todos los libros en lista de espera.
     */
    private fun getEsperaNombres() {
        db.getReference("usuarios/$emailFormateado/listaEspera").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaNombresEspera.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val nombre = item.getValue(String::class.java)
                        if (nombre != null) {
                            listaNombresEspera.add(nombre)
                        }
                    }
                }
                getListaEspera()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Se hace una query buscando todos los libros de la db y, si el titulo de un libro
     * existe en la listaNombresEspera, se añade a listaEspera.
     */
    private fun getListaEspera() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaEspera.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (listaNombresEspera.contains(libro?.titulo)) {
                            if (libro != null) {
                                listaEspera.add(libro)
                            }
                        }
                    }
                    adapter.lista = listaEspera
                    adapter.notifyDataSetChanged()
                }

                if (listaEspera.isEmpty()) {
                    binding.tvListaEsperaFragment.visibility = View.VISIBLE
                } else {
                    binding.tvListaEsperaFragment.visibility = View.INVISIBLE
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerEspera.layoutManager = layoutManager

        adapter = LibroAdapter(listaEspera) { onItemSelected(it) }
        binding.recyclerEspera.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}