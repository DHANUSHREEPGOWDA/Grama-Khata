package com.gramakhata.ui.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.gramakhata.databinding.DialogAddCustomerBinding
import com.gramakhata.viewmodel.GramaKhataViewModel

class AddCustomerDialog : DialogFragment() {

    private var _binding: DialogAddCustomerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GramaKhataViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddCustomerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Name required"
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                binding.etPhone.error = "Phone required"
                return@setOnClickListener
            }

            viewModel.addCustomer(name, phone)
            Toast.makeText(requireContext(), "$name added!", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        binding.btnCancel.setOnClickListener { dismiss() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
