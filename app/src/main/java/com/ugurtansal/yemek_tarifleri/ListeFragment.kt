package com.ugurtansal.yemek_tarifleri

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.ugurtansal.yemek_tarifleri.adapter.TarifAdapter
import com.ugurtansal.yemek_tarifleri.databinding.FragmentListeBinding
import com.ugurtansal.yemek_tarifleri.model.Tarif
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDAO
import com.ugurtansal.yemek_tarifleri.roomDb.TarifDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class ListeFragment : Fragment() {
    private lateinit var binding: FragmentListeBinding

    private lateinit var db: TarifDatabase;
    private lateinit var tarifDao: TarifDAO;

    private val mDisposable= CompositeDisposable()


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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tarifRv.layoutManager= LinearLayoutManager(requireContext())
    }

    private fun verileriAl(){
        mDisposable.add(
            tarifDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::handleResponse
                )
        )
    }

    private fun handleResponse(tarifler:List<Tarif>){

        val adapter= TarifAdapter(tarifler)
        binding.tarifRv.adapter=adapter
    }

    fun yeniEkle(view: View){
        val action= ListeFragmentDirections.actionListeFragmentToTarifFragment("yeni",0)
        Navigation.findNavController(view).navigate(action)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        mDisposable.clear()
    }

}