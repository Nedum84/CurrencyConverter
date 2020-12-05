package com.currencyconverter.app.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.currencyconverter.app.databinding.ItemCurrencyBinding
import com.currencyconverter.app.model.Currency


class AdapterCurrency(private val clickListener: SymbolClickListener) : RecyclerView.Adapter<AdapterCurrency.ViewHolder>() {

    var list: List<Currency> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = list[position]

        holder.bind(curItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCurrencyBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Currency) {
            binding.symbol = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

    }

}

//CLICK LISTENER
class SymbolClickListener(val clickListener: (Currency) -> Unit) {
    fun onClick(currency: Currency) = clickListener(currency)
}


