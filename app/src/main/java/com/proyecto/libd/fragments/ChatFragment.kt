package com.proyecto.libd.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.libd.R
import com.example.libd.databinding.FragmentBuscarBinding
import com.example.libd.databinding.FragmentChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.proyecto.libd.Prefs
import com.proyecto.libd.adapters.MensajeAdapter
import com.proyecto.libd.model.MensajeChat

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    lateinit var prefs: Prefs
    lateinit var adapter: MensajeAdapter
    lateinit var db: FirebaseDatabase

    private var lista = ArrayList<MensajeChat>()
    var email = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = Prefs(requireContext())
        email = prefs.getEmail().toString()
        db = FirebaseDatabase.getInstance(getString(R.string.databaseURL))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setRecycle()
        setListener()
        traerMensaje()
    }

    /**
     * inputManager controla el teclado virtual, con hideSoftInputFromWindow se puede configurar
     * para que cuando el teclado aparezca, no desplaze componentes, en este caso, la propia barra
     * de texto.
     */
    private fun setListener() {
        binding.ivEnviar.setOnClickListener {
            enviarMensaje()
            val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(binding.ivEnviar.windowToken, 0)
        }
    }

    private fun setRecycle() {
        var layoutManager = LinearLayoutManager(requireContext())
        binding.recChat.layoutManager = layoutManager

        adapter = MensajeAdapter(lista, email)
        binding.recChat.adapter = adapter
    }

    /**
     * Para enviar el texto primero toma el texto y luego toma una referencia de la base de datos
     * y añade el valor. Si es exitoso, se llama a traerMensaje y se pone la barra de texto en blanco.
     */
    private fun enviarMensaje() {
        val texto = binding.tiChat.text.toString().trim()
        if (texto.isEmpty()) return

        val m = MensajeChat(texto, email)
        db.getReference("chat").child(m.fecha.toString()).setValue(m).addOnSuccessListener {
            binding.tiChat.setText("")
            traerMensaje()
        }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Ha ocurrido un error al enviar", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Toma una referencia de la BD y añade un addValueEventListener, sobrescribiendo el metodo onDataChange
     * se recorre los hijos de snapshot y si no son nulos, se añaden a lista ordenados por fecha mas reciente,
     * notifica a adapter y hace scroll al nuevo mensaje.
     */
    private fun traerMensaje() {
        db.getReference("chat").addValueEventListener(object: ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                if (snapshot.exists()) {
                    for (item in snapshot.children) {
                        val mensaje = item.getValue(MensajeChat::class.java)
                        if (mensaje != null) {
                            lista.add(mensaje)
                        }
                    }
                    lista.sortBy { mensaje -> mensaje.fecha }
                    adapter.lista = lista
                    adapter.notifyDataSetChanged()
                    binding.recChat.scrollToPosition(lista.size -1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun View.ocultarTeclado() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }
}