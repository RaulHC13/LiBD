package com.proyecto.libd.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.libd.R
import com.example.libd.databinding.FragmentBuscarBinding
import com.example.libd.databinding.FragmentHomeBinding
import com.example.libd.databinding.FragmentListaEsperaBinding

class ListaEsperaFragment : Fragment() {

    private lateinit var binding: FragmentListaEsperaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaEsperaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
    }

    private fun setListeners() {

    }
}