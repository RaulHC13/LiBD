package com.proyecto.libd.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.view.CrearActivity
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    lateinit var prefs: Prefs
    lateinit var adapter: LibroAdapter
    lateinit var db: FirebaseDatabase

    private var libros10 = ArrayList<Libro>()
    private var listaLibros10 = ArrayList<Libro>()
    var email = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = Prefs(requireContext())
        email = prefs.getEmail().toString()
        db = FirebaseDatabase.getInstance("https://libd-96d39-default-rtdb.europe-west1.firebasedatabase.app/")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycler()
        setListeners()
        traerLibros()
    }

    private fun traerLibros() {
        db.getReference("libros").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                listaLibros10.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val libro = item.getValue(Libro::class.java)
                        if (libro != null) {
                            listaLibros10.add(libro)
                        }
                    }
                    listaLibros10.sortByDescending { libro -> libro.fecha }
                    libros10 = ArrayList(listaLibros10.take(10))
                    adapter.lista = libros10
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireActivity(), CrearActivity::class.java))
        }
    }

//    private fun onItemSelected(libro: Libro) {
//        val i = Intent(requireActivity(), LibrosDetallesActivity::class.java)
//        i.putExtra("LIBRO", libro)
//    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.initialPrefetchItemCount = 10
        binding.recycler.layoutManager = layoutManager

        adapter = LibroAdapter(libros10) //{onItemSelected(it)}
        binding.recycler.adapter = adapter
    }
}