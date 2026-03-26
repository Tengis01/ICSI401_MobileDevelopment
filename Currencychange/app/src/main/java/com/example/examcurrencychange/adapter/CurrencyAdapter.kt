// adapter/CurrencyAdapter.kt
package com.example.examcurrencychange.adapter  // ← засах!

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.examcurrencychange.data.Currency          // ← examcurrencychange!
import com.example.examcurrencychange.databinding.ItemCurrencyBinding  // ← examcurrencychange!

class CurrencyAdapter(
    private val currencies: List<Currency>,
    private val onItemClick: (Currency) -> Unit
) : RecyclerView.Adapter<CurrencyAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCurrencyBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(currency: Currency) {
            binding.tvFlag.text = currency.flag
            binding.tvName.text = currency.nameMn
            binding.tvCode.text = currency.code
            binding.root.setOnClickListener { onItemClick(currency) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCurrencyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(currencies[position])

    override fun getItemCount() = currencies.size
}