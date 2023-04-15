package com.proyecto.libd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libd.R
import com.example.libd.databinding.ActivityHomeBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.libd.CrearActivity
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.model.Libro

class HomeActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    lateinit var prefs: Prefs
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView

    lateinit var adapter: LibroAdapter
    private var listaLibros10 = arrayListOf<Libro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setListeners()
        setRecycler()
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recycler.layoutManager = layoutManager

        adapter = LibroAdapter(listaLibros10)
        binding.recycler.adapter = adapter
    }

    private fun setListeners() {

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CrearActivity::class.java))
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_home -> Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
                R.id.nav_chat -> Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show()
                R.id.nav_list -> Toast.makeText(this, "Lista de lectura", Toast.LENGTH_SHORT).show()
                R.id.nav_fav -> Toast.makeText(this, "Favoritos", Toast.LENGTH_SHORT).show()
                R.id.nav_rate -> Toast.makeText(this, "Valoraciones", Toast.LENGTH_SHORT).show()
                R.id.nav_perfil -> Toast.makeText(this, "Editar perfil", Toast.LENGTH_SHORT).show()
                R.id.nav_config -> Toast.makeText(this, "Configuracion", Toast.LENGTH_SHORT).show()

                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    prefs.borrarTodo()
                    finish()
                }
                R.id.nav_exit -> finishAffinity()

            }
            true
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}