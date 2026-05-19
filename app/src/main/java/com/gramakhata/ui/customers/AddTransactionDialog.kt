package com.gramakhata.ui.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.gramakhata.databinding.DialogAddTransactionBinding
import com.gramakhata.viewmodel.GramaKhataViewModel

class AddTransactionDialog : DialogFragment() {

    private var _binding: DialogAddTransactionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GramaKhataViewModel by activityViewModels()

    companion object {
        fun newInstance(customerId: Int, isCredit: Boolean): AddTransactionDialog {
            return AddTransactionDialog().apply {
                arguments = Bundle().apply {
                    putInt("customerId", customerId)
                    putBoolean("isCredit", isCredit)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val customerId = arguments?.getInt("customerId") ?: return
        val isCredit = arguments?.getBoolean("isCredit") ?: true

        binding.tvDialogTitle.text = if (isCredit) "Customer Paid (+)" else "Customer Took Goods (-)"
        binding.btnSave.text = if (isCredit) "Add Payment" else "Add Due"
        binding.btnSave.setBackgroundColor(
            if (isCredit) resources.getColor(android.R.color.holo_green_dark, null)
            else resources.getColor(android.R.color.holo_red_dark, null)
        )

        binding.btnSave.setOnClickListener {
            val amountStr = binding.etAmount.text.toString()
            val note = binding.etNote.text.toString()

            if (amountStr.isEmpty()) {
                binding.etAmount.error = "Amount required"
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.etAmount.error = "Enter valid amount"
                return@setOnClickListener
            }

            if (isCredit) viewModel.addCredit(customerId, amount, note)
            else viewModel.addDebit(customerId, amount, note)

            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
