package com.proyecto.libd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libd.databinding.ActivityCrearBinding

class CrearActivity : AppCompatActivity() {
    lateinit var binding: ActivityCrearBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrearBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners() {
        binding.btnVolver.setOnClickListener {
            finish()
        }
    }
}