package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.R
import com.example.libd.databinding.FragmentLeidosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class LeidosFragment : Fragment() {

    private lateinit var binding: FragmentLeidosBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase
    lateinit var prefs: Prefs

    private var listaNombresLeidos = ArrayList<String>()
    private var listaLeidos = ArrayList<Libro>()
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
        binding = FragmentLeidosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = Prefs(requireContext())
        emailFormateado = prefs.getEmailFormateado().toString()
        binding.tvLeidosFragment.visibility = if (listaLeidos.isEmpty()) View.VISIBLE else View.INVISIBLE

        getLeidosNombres()
        getLeidos()
        setRecycler()
    }

    /**
     * Recoge en una lista el nombre de todos los libros en lista de leidos.
     */
    private fun getLeidosNombres() {
        db.getReference("usuarios/$emailFormateado/listaLectura").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaNombresLeidos.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val nombre = item.getValue(String::class.java)
                        if (nombre != null) {
                            listaNombresLeidos.add(nombre)
                        }
                    }
                }
                getLeidos()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    /**
     * Se hace una query buscando todos los libros de la db y, si el titulo de un libro
     * existe en la listaNombresLeidos, se añade a listaLeidos.
     */
    private fun getLeidos() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaLeidos.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (listaNombresLeidos.contains(libro?.titulo)) {
                            if (libro != null) {
                                listaLeidos.add(libro)
                            }
                        }
                    }
                    adapter.lista = listaLeidos
                    adapter.notifyDataSetChanged()
                }
                if (listaLeidos.isEmpty()) {
                    binding.tvLeidosFragment.visibility = View.VISIBLE
                } else {
                    binding.tvLeidosFragment.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerLeidos.layoutManager = layoutManager

        adapter = LibroAdapter(listaLeidos) { onItemSelected(it) }
        binding.recyclerLeidos.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}