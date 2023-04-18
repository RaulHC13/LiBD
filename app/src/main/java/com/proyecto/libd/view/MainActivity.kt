package com.proyecto.libd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.libd.R
import com.example.libd.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.LibroAdapter
import com.proyecto.libd.fragments.*
import com.proyecto.libd.model.Libro

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var prefs: Prefs
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var navView: NavigationView
    lateinit var email: String
    lateinit var drawerLayout: DrawerLayout

    lateinit var adapter: LibroAdapter
    private var listaLibros10 = arrayListOf<Libro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)
        email = prefs.getEmail().toString()

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        cambiarFragment(HomeFragment(), "Home")

        setPerfil()
        setListeners()
//        setRecycler()
    }

    private fun setPerfil() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val aux_email = headerView.findViewById<TextView>(R.id.tvUserEmail)
        val aux_nombre = headerView.findViewById<TextView>(R.id.tvUsername)

        var nombreGenerado = email.substring(0, email.indexOf("@"))
        aux_email.text = email
        aux_nombre.text = nombreGenerado
        //Hay que meter el nombre de usuario y la imagen de perfil, despues de crear el sistema
        //de perfiles con realtime storage.
    }

//    private fun setRecycler() {
//        val layoutManager = LinearLayoutManager(this)
//        binding.recycler.layoutManager = layoutManager
//
//        adapter = LibroAdapter(listaLibros10)
//        binding.recycler.adapter = adapter
//    }

    private fun setListeners() {

//        binding.btnAdd.setOnClickListener {
//            startActivity(Intent(this, CrearActivity::class.java))
//        }

        navView.setNavigationItemSelectedListener {

            it.isChecked = true

            when(it.itemId) {
                R.id.nav_home -> cambiarFragment(HomeFragment(), it.title.toString())
                R.id.nav_chat -> cambiarFragment(ChatFragment(), it.title.toString())
                R.id.nav_list -> cambiarFragment(ListaEsperaFragment(), it.title.toString())
                R.id.nav_fav -> cambiarFragment(FavoritosFragment(), it.title.toString())
                R.id.nav_rate -> cambiarFragment(ValoracionesFragment(), it.title.toString())
                R.id.nav_search -> cambiarFragment(BuscarFragment(), it.title.toString())
                R.id.nav_perfil -> Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                R.id.nav_config -> Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()

                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    prefs.borrarEmail()
                    finish()
                }
                R.id.nav_exit -> finishAffinity()

            }
            true
        }
    }

    private fun cambiarFragment(fragment: Fragment, titulo: String) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragment.retainInstance
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        setTitle(titulo)
    }

    override fun onBackPressed() {
        val homeFragment = supportFragmentManager.findFragmentByTag("homeFragment")
        if (homeFragment == null || !homeFragment.isVisible) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.frameLayout, HomeFragment())
                .commit()
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}