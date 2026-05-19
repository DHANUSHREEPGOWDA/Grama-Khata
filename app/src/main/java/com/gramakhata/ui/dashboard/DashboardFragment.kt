package com.gramakhata.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.gramakhata.databinding.FragmentDashboardBinding
import com.gramakhata.viewmodel.GramaKhataViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GramaKhataViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDueList()
        observeDashboard()
        setupAiReport()
    }

    private fun setupDueList() {
        val adapter = DueListAdapter(viewModel.allCustomers)
        binding.rvDueList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDueList.adapter = adapter

        viewModel.customersDueSorted.observe(viewLifecycleOwner) { dueList ->
            adapter.submitList(dueList)
            binding.tvNoDues.visibility = if (dueList.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun observeDashboard() {
        viewModel.totalOutstanding.observe(viewLifecycleOwner) { total ->
            binding.tvTotalOutstanding.text = "₹${"%.2f".format(total)}"
        }

        viewModel.allCustomers.observe(viewLifecycleOwner) { customers ->
            binding.tvTotalCustomers.text = "${customers.size} Customers"
        }
    }

    private fun setupAiReport() {
        binding.btnGenerateReport.setOnClickListener {
            val shopName = binding.etShopName.text.toString().ifEmpty { "My Shop" }
            viewModel.generateDailyReport(shopName)
            binding.tvAiReport.text = "Generating report..."
            binding.progressAi.visibility = View.VISIBLE
        }

        viewModel.aiReport.observe(viewLifecycleOwner) { report ->
            binding.tvAiReport.text = report
            binding.progressAi.visibility = View.GONE
            binding.cardAiReport.visibility = View.VISIBLE
        }

        viewModel.aiLoading.observe(viewLifecycleOwner) { loading ->
            binding.btnGenerateReport.isEnabled = !loading
            binding.progressAi.visibility = if (loading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
