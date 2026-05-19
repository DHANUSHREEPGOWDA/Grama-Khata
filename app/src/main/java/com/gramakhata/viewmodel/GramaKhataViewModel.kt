package com.gramakhata.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.gramakhata.data.database.GramaKhataDatabase
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.Transaction
import com.gramakhata.data.entity.TransactionType
import com.gramakhata.data.repository.AiRepository
import com.gramakhata.data.repository.GramaKhataRepository
import kotlinx.coroutines.launch

class GramaKhataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = GramaKhataDatabase.getDatabase(application)
    private val repository = GramaKhataRepository(db.customerDao(), db.transactionDao())
    private val aiRepository = AiRepository()

    // ── Customers ──────────────────────────────────────────────
    val allCustomers: LiveData<List<Customer>> = repository.allCustomers
    val totalOutstanding: LiveData<Double> = repository.getTotalOutstanding()
    val customersDueSorted = repository.getCustomersDueSorted()

    private val _searchQuery = MutableLiveData<String>("")
    val searchResults: LiveData<List<Customer>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) repository.allCustomers
        else repository.searchCustomers(query)
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCustomer(name: String, phone: String, photoUri: String? = null) {
        viewModelScope.launch {
            repository.insertCustomer(Customer(name = name, phone = phone, photoUri = photoUri))
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch { repository.deleteCustomer(customer) }
    }

    // ── Transactions ───────────────────────────────────────────
    fun getTransactionsForCustomer(customerId: Int) =
        repository.getTransactionsForCustomer(customerId)

    fun getNetBalance(customerId: Int) =
        repository.getNetBalanceForCustomer(customerId)

    fun addCredit(customerId: Int, amount: Double, note: String = "") {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(customerId = customerId, amount = amount, type = TransactionType.CREDIT, note = note)
            )
        }
    }

    fun addDebit(customerId: Int, amount: Double, note: String = "") {
        viewModelScope.launch {
            repository.insertTransaction(
                Transaction(customerId = customerId, amount = amount, type = TransactionType.DEBIT, note = note)
            )
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch { repository.deleteTransaction(transaction) }
    }

    // ── AI Features ────────────────────────────────────────────
    private val _aiReport = MutableLiveData<String>()
    val aiReport: LiveData<String> = _aiReport

    private val _aiLoading = MutableLiveData<Boolean>(false)
    val aiLoading: LiveData<Boolean> = _aiLoading

    private val _whatsAppMessage = MutableLiveData<String>()
    val whatsAppMessage: LiveData<String> = _whatsAppMessage

    fun generateDailyReport(shopName: String) {
        viewModelScope.launch {
            _aiLoading.value = true
            val customers = repository.allCustomers.value ?: emptyList()
            val summaries = customers.map { customer ->
                val txns = repository.getTransactionsForCustomerOnce(customer.id)
                val balance = txns.sumOf { if (it.type == TransactionType.DEBIT) it.amount else -it.amount }
                "${customer.name}: ₹${"%.2f".format(balance)}"
            }.filter { it.contains("₹") }

            val total = totalOutstanding.value ?: 0.0
            val result = aiRepository.generateDailyCollectionReport(shopName, summaries, total)
            _aiReport.value = result.getOrElse { "Failed to generate report. Check your API key." }
            _aiLoading.value = false
        }
    }

    fun generateWhatsAppMessage(customerName: String, shopName: String, amountDue: Double) {
        viewModelScope.launch {
            _aiLoading.value = true
            val result = aiRepository.generateWhatsAppMessage(customerName, shopName, amountDue)
            _whatsAppMessage.value = result.getOrElse {
                "Namaskara $customerName, your due at $shopName is ₹${"%.2f".format(amountDue)}."
            }
            _aiLoading.value = false
        }
    }
}
