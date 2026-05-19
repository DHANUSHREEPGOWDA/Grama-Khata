package com.gramakhata.ui.customers

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.gramakhata.databinding.FragmentCustomerDetailBinding
import com.gramakhata.ui.transactions.TransactionAdapter
import com.gramakhata.viewmodel.GramaKhataViewModel
import kotlinx.coroutines.launch

class CustomerDetailFragment : Fragment() {

    private var _binding: FragmentCustomerDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GramaKhataViewModel by activityViewModels()
    private var customerId: Int = -1
    private var customerName: String = ""
    private var customerPhone: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerId = arguments?.getInt("customerId") ?: -1

        setupRecyclerView()
        loadCustomer()
        observeTransactions()
        observeBalance()
        setupButtons()
        observeAi()
    }

    private fun loadCustomer() {
        lifecycleScope.launch {
            val customer = viewModel.allCustomers.value?.find { it.id == customerId }
            customer?.let {
                customerName = it.name
                customerPhone = it.phone
                binding.tvCustomerName.text = it.name
                binding.tvCustomerPhone.text = it.phone

                val initials = it.name.split(" ")
                    .mapNotNull { w -> w.firstOrNull()?.toString() }
                    .take(2).joinToString("").uppercase()
                binding.tvInitials.text = initials
            }
        }
        // Also observe for live updates
        viewModel.allCustomers.observe(viewLifecycleOwner) { customers ->
            customers.find { it.id == customerId }?.let {
                customerName = it.name
                customerPhone = it.phone
                binding.tvCustomerName.text = it.name
                binding.tvCustomerPhone.text = it.phone
            }
        }
    }

    private fun setupRecyclerView() {
        val adapter = TransactionAdapter { transaction ->
            viewModel.deleteTransaction(transaction)
        }
        binding.rvTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTransactions.adapter = adapter

        viewModel.getTransactionsForCustomer(customerId).observe(viewLifecycleOwner) { txns ->
            adapter.submitList(txns)
            binding.tvNoTransactions.visibility = if (txns.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeTransactions() {
        // Already handled in setupRecyclerView
    }

    private fun observeBalance() {
        viewModel.getNetBalance(customerId).observe(viewLifecycleOwner) { balance ->
            val balanceText = "₹${"%.2f".format(kotlin.math.abs(balance))}"
            binding.tvNetBalance.text = balanceText

            when {
                balance > 0 -> {
                    binding.tvBalanceLabel.text = "Amount Due (Customer owes you)"
                    binding.tvNetBalance.setTextColor(
                        resources.getColor(android.R.color.holo_red_dark, null)
                    )
                }
                balance < 0 -> {
                    binding.tvBalanceLabel.text = "You owe customer"
                    binding.tvNetBalance.setTextColor(
                        resources.getColor(android.R.color.holo_green_dark, null)
                    )
                }
                else -> {
                    binding.tvBalanceLabel.text = "All settled"
                    binding.tvNetBalance.setTextColor(
                        resources.getColor(android.R.color.darker_gray, null)
                    )
                }
            }
        }
    }

    private fun setupButtons() {
        // Add Credit (Customer paid)
        binding.btnCredit.setOnClickListener {
            AddTransactionDialog.newInstance(customerId, isCredit = true)
                .show(parentFragmentManager, "AddCredit")
        }

        // Add Debit (Customer took goods)
        binding.btnDebit.setOnClickListener {
            AddTransactionDialog.newInstance(customerId, isCredit = false)
                .show(parentFragmentManager, "AddDebit")
        }

        // WhatsApp Alert using AI
        binding.btnWhatsapp.setOnClickListener {
            val balance = viewModel.getNetBalance(customerId).value ?: 0.0
            if (balance > 0) {
                viewModel.generateWhatsAppMessage(customerName, "My Shop", balance)
            } else {
                sendWhatsApp("Namaskara $customerName, your account is all settled. Thank you!")
            }
        }
    }

    private fun observeAi() {
        viewModel.whatsAppMessage.observe(viewLifecycleOwner) { message ->
            if (message.isNotBlank()) {
                sendWhatsApp(message)
            }
        }
    }

    private fun sendWhatsApp(message: String) {
        val phone = customerPhone.replace(Regex("[^0-9]"), "")
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=91$phone&text=${Uri.encode(message)}")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
