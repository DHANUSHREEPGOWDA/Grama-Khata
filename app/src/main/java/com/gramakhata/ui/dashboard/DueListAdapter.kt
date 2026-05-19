package com.gramakhata.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.data.dao.CustomerBalance
import com.gramakhata.data.entity.Customer
import com.gramakhata.databinding.ItemDueBinding

class DueListAdapter(
    private val customersLiveData: LiveData<List<Customer>>
) : ListAdapter<CustomerBalance, DueListAdapter.DueViewHolder>(DiffCallback()) {

    inner class DueViewHolder(private val binding: ItemDueBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomerBalance) {
            val customer = customersLiveData.value?.find { it.id == item.customerId }
            binding.tvName.text = customer?.name ?: "Unknown"
            binding.tvPhone.text = customer?.phone ?: ""
            binding.tvDueAmount.text = "₹${"%.2f".format(item.netBalance)}"

            val initials = (customer?.name ?: "?")
                .split(" ").mapNotNull { it.firstOrNull()?.toString() }
                .take(2).joinToString("").uppercase()
            binding.tvInitials.text = initials
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DueViewHolder {
        val binding = ItemDueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DueViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<CustomerBalance>() {
        override fun areItemsTheSame(a: CustomerBalance, b: CustomerBalance) = a.customerId == b.customerId
        override fun areContentsTheSame(a: CustomerBalance, b: CustomerBalance) = a == b
    }
}
