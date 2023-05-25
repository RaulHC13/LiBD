package com.proyecto.libd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libd.databinding.ActivityOpcionesBinding

class OpcionesActivity : AppCompatActivity() {
    lateinit var binding: ActivityOpcionesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpcionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.btnVolverOpciones.setOnClickListener {
            finish()
        }
    }
}