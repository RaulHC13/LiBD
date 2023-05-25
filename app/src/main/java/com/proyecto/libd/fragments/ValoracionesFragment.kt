package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.R
import com.example.libd.databinding.FragmentValoracionesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro
import com.proyecto.libd.view.LibrosDetallesActivity

class ValoracionesFragment : Fragment() {

    /**
     * Contiene un top 10 valoraciones, este top 10 solo contiene libros que han sido valorados,
     * si no se han valorado no podran formar parte del top 10. Se cargan libros en recycler ordenando
     * por valoracion y, despues, por fecha desc.
     */
    private lateinit var binding: FragmentValoracionesBinding
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase

    private var librosLista = ArrayList<Libro>()
    private var librosTop10 = ArrayList<Libro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentValoracionesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvValoracionesFragment.visibility = if (librosLista.isEmpty()) View.VISIBLE else View.INVISIBLE

        setRecycler()
        traerTopLibros()
    }


    /**
     * Trae libros de la base de datos, y si tienen valoracion, los mete a librosLista.
     * La lista se ordena por valoraciones y se crea librosTop10 tomando los 10 primeros
     * de librosLista.
     */
    private fun traerTopLibros() {

        db.getReference("libros").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                librosLista.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (libro != null && libro.valoracion != 0.0f) {
                            librosLista.add(libro)
                        }
                    }
                    librosLista.sortByDescending { libro -> libro.valoracion }
                    librosTop10 = ArrayList(librosLista.take(10))
                    adapter.lista = librosTop10
                    adapter.notifyDataSetChanged()
                }
                if (librosLista.isEmpty()) {
                    binding.tvValoracionesFragment.visibility = View.VISIBLE
                } else {
                    binding.tvValoracionesFragment.visibility = View.INVISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.initialPrefetchItemCount = 10
        binding.recyclerValoraciones.layoutManager = layoutManager

        adapter = LibroAdapter(librosTop10) { onItemSelected(it) }
        binding.recyclerValoraciones.adapter = adapter
    }

    private fun onItemSelected(libro: Libro) {
        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
        i.putExtra("LIBRO", libro)
        startActivity(i)
    }
}