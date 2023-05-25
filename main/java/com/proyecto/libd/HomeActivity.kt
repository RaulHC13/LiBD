package com.proyecto.libd

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.libd.R
import com.example.libd.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = Prefs(this)

        setListeners()
    }

    private fun setListeners() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, CrearActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.miCerrar -> {
                FirebaseAuth.getInstance().signOut()
                prefs.borrarTodo()
                finish()
                true
            }
            R.id.miSalir -> {
                finishAffinity()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}