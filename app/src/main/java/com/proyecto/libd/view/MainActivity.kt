package com.proyecto.libd.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.libd.R
import com.example.libd.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.proyecto.libd.Prefs
import com.proyecto.libd.fragments.*
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {
    /**
     * Hay que borrar la imagen de portada de storage cuando se borra un libro
     */
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: Prefs
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var email: String
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var storage: FirebaseStorage

    private var selectedItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)
        email = prefs.getEmail().toString()
        storage = FirebaseStorage.getInstance("gs://libd-96d39.appspot.com/")

        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.nav_view)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        cambiarFragment(HomeFragment(), "Home")

        setPerfil()
        setListeners()
    }

    /**
     * Carga la imagen de perfil y el email en los campos del nav view.
     */
    private fun setPerfil() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)

        val auxEmail = headerView.findViewById<TextView>(R.id.tvNavEmail)
        val auxNombre = headerView.findViewById<TextView>(R.id.tvNavUsername)
        val auxImagenperfil = headerView.findViewById<CircleImageView>(R.id.ivNavPerfil)

        imagenOnStart(auxImagenperfil)
        auxEmail.text = email
        auxNombre.text = prefs.getUsername()
    }

    /**
     * Pone listeners a cada objeto del menu lateral, de este modo se navegan los fragments.
     * Permite cerrar la sesion borrando el email de prefs y salir de la aplicación.
     */
    private fun setListeners() {

        navView.setNavigationItemSelectedListener {
            it.isChecked = true

            when(it.itemId) {
                R.id.nav_home -> cambiarFragment(HomeFragment(), it.title.toString())
                R.id.nav_chat -> cambiarFragment(ChatFragment(), it.title.toString())
                R.id.nav_leyendo -> cambiarFragment(LeyendoFragment(), it.title.toString())
                R.id.nav_list -> cambiarFragment(ListaEsperaFragment(), it.title.toString())
                R.id.nav_fav -> cambiarFragment(FavoritosFragment(), it.title.toString())
                R.id.nav_rate -> cambiarFragment(ValoracionesFragment(), it.title.toString())
                R.id.nav_search -> cambiarFragment(BuscarFragment(), it.title.toString())
                R.id.nav_perfil ->  {
                    startActivity(Intent(this, PerfilActivity::class.java))

                }
                R.id.nav_config -> {
                    Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
                }

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

    /**
     * @param fragment y un titulo, mueve el fragment a frameLayout, no añade nada a backStack
     * de este modo al presionar atrás en cualquier punto nunca podrá verse el frameLayout vacío.
     */
    private fun cambiarFragment(fragment: Fragment, titulo: String) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragment.retainInstance
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        drawerLayout.closeDrawers()
        title = titulo
    }

    /**
     * Sobrescritura que comprueba si el homeFragment es visible y, si no lo es, lo mueve a
     * frameLayout, de este modo frameLayout nunca puede quedar vacío.
     */
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

    /**
     * Toma como parametro un contenedor de imagen.
     * Baja una imagen de firebase storage a partir del email de usuario y se carga en el contenedor.
     * Se comprueba si existe la imagen en la base de datos y, si no existe (el usuario no
     * ha subido una), se carga una imagen por defecto.
     */
    private fun imagenOnStart(aux_imagenPerfil: CircleImageView) {
        val requestOptions = RequestOptions().transform(CircleCrop())
        val ref = storage.reference
        val file = ref.child("perfiles/$email/perfil.jpg")

        file.metadata.addOnSuccessListener {
            file.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(aux_imagenPerfil)
            }
        }.addOnFailureListener {
            val defaultImg = ref.child("default/perfil.jpg")
            defaultImg.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .apply(requestOptions)
                    .into(aux_imagenPerfil)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setPerfil()
    }
}