package com.gramakhata.ui.customers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.gramakhata.R
import com.gramakhata.databinding.FragmentCustomerListBinding
import com.gramakhata.viewmodel.GramaKhataViewModel

class CustomerListFragment : Fragment() {

    private var _binding: FragmentCustomerListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GramaKhataViewModel by activityViewModels()
    private lateinit var adapter: CustomerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCustomerListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFab()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = CustomerAdapter { customer ->
            val bundle = Bundle().apply { putInt("customerId", customer.id) }
            findNavController().navigate(R.id.action_customerList_to_customerDetail, bundle)
        }
        binding.rvCustomers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCustomers.adapter = adapter
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupFab() {
        binding.fabAddCustomer.setOnClickListener {
            AddCustomerDialog().show(parentFragmentManager, "AddCustomer")
        }
    }

    private fun observeData() {
        viewModel.searchResults.observe(viewLifecycleOwner) { customers ->
            adapter.submitList(customers)
            binding.tvEmptyState.visibility = if (customers.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
