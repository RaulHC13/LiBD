package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.databinding.FragmentBuscarBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class BuscarFragment : Fragment() {


    /**
     * Tiene que aparecer todos los libros al abrir el fragment, de momento no salen al abrirlo
     * pero el resto funciona OK.
     */

    private lateinit var binding: FragmentBuscarBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase

    private var librosInicial = ArrayList<Libro>()
    private var librosBuscar = ArrayList<Libro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuscarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBuscarFragment.visibility = if (librosInicial.isEmpty()) View.VISIBLE else View.INVISIBLE

        setRecycler()
        setListeners()
        traerLibrosInicial()
    }


    private fun setListeners() {

        binding.svBuscar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                buscarLibros(newText)
                return true
            }
        })
    }

    private fun traerLibrosInicial() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                librosInicial.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (libro != null) {
                            librosInicial.add(libro)
                        }
                    }
                    librosInicial.sortByDescending { libro -> libro.titulo }
                    librosInicial.sortByDescending { libro -> libro.autor }
                    adapter.notifyDataSetChanged()
                    buscarLibros("")
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    /**
     * Recorre la listaInicial buscando libros que coincidan con la query. Si la query esta vacia,
     * pone la lista de libros inicial en recyclerBuscar
     */
    private fun buscarLibros(query: String) {
        librosBuscar.clear()

        if (query.isNotEmpty()) {
            val buscarQuery = query.lowercase()
            for (libro in librosInicial) {
                val titulo = libro.titulo.lowercase()
                if (titulo.contains(buscarQuery)) {
                    librosBuscar.add(libro)
                }
            }

        } else {
            librosBuscar.addAll(librosInicial)
        }
        adapter.notifyDataSetChanged()

        if (librosBuscar.isEmpty()) {
            binding.tvBuscarFragment.visibility = View.VISIBLE
        } else {
            binding.tvBuscarFragment.visibility = View.INVISIBLE
        }
    }

    private fun setRecycler() {

        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerBuscar.layoutManager = layoutManager

        adapter = LibroAdapter(librosBuscar) { onItemSelected(it) }
        binding.recyclerBuscar.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}