package com.ugurtansal.yemek_tarifleri.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ugurtansal.yemek_tarifleri.ListeFragmentDirections
import com.ugurtansal.yemek_tarifleri.databinding.RecyclerRowBinding
import com.ugurtansal.yemek_tarifleri.model.Tarif

class TarifAdapter(val tarifListesi: List<Tarif>): RecyclerView.Adapter<TarifAdapter.TarifHolder>() {

    inner class TarifHolder(val binding: RecyclerRowBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TarifHolder {
        val recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TarifHolder(recyclerRowBinding)
    }

    override fun onBindViewHolder(
        holder: TarifHolder,
        position: Int
    ) {
        holder.binding.recyclerViewText.text=tarifListesi[position].isim
        holder.itemView.setOnClickListener {
            val action= ListeFragmentDirections.actionListeFragmentToTarifFragment(bilgi = "eski",id=tarifListesi[position].id)
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return tarifListesi.size
    }


}