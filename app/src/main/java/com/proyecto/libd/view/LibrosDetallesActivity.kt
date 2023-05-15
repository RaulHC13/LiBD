package com.proyecto.libd.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.libd.databinding.LibrosDetallesLayoutBinding

class LibrosDetallesActivity : AppCompatActivity() {
    lateinit var binding: LibrosDetallesLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LibrosDetallesLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}