package com.gramakhata.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gramakhata.data.entity.Transaction
import com.gramakhata.data.entity.TransactionType
import com.gramakhata.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(
    private val onDeleteClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback()) {

    inner class TransactionViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            val isCredit = transaction.type == TransactionType.CREDIT

            binding.tvAmount.text = "₹${"%.2f".format(transaction.amount)}"
            binding.tvType.text = if (isCredit) "PAID" else "DUE"
            binding.tvNote.text = transaction.note.ifEmpty { "No note" }

            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            binding.tvDate.text = sdf.format(Date(transaction.timestamp))

            // Color coding
            val color = if (isCredit)
                binding.root.context.getColor(android.R.color.holo_green_dark)
            else
                binding.root.context.getColor(android.R.color.holo_red_dark)

            binding.tvAmount.setTextColor(color)
            binding.tvType.setTextColor(color)

            val prefix = if (isCredit) "+ " else "- "
            binding.tvAmount.text = "$prefix₹${"%.2f".format(transaction.amount)}"

            binding.btnDelete.setOnClickListener { onDeleteClick(transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) = oldItem == newItem
    }
}
