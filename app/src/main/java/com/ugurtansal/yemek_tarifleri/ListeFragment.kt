package com.ugurtansal.yemek_tarifleri

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.room.Room
import com.ugurtansal.yemek_tarifleri.databinding.FragmentListeBinding
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDAO
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDatabase


class ListeFragment : Fragment() {
    private lateinit var binding: FragmentListeBinding

    private lateinit var db: TarifDatabase;
    private lateinit var tarifDao: TarifDAO;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db= Room.databaseBuilder(requireContext(), TarifDatabase::class.java, "Tarifler").build()
        tarifDao=db.tarifDao()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentListeBinding.inflate(inflater, container, false)

        binding.fab.setOnClickListener {
            yeniEkle(it)
        }

        return binding.root
    }

    fun yeniEkle(view: View){
        val action= ListeFragmentDirections.actionListeFragmentToTarifFragment("yeni",0)
        Navigation.findNavController(view).navigate(action)
    }


}