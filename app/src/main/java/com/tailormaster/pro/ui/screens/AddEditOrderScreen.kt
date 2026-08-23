package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.data.Customer
import com.tailormaster.pro.data.Order
import com.tailormaster.pro.data.OrderStatus
import com.tailormaster.pro.viewmodel.CustomerViewModel
import com.tailormaster.pro.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOrderScreen(
    customerVm: CustomerViewModel,
    orderVm: OrderViewModel,
    preselectedCustomerId: String?,
    onSaved: () -> Unit
) {
    var customerQuery by remember { mutableStateOf("") }
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    val customers by customerVm.customers.collectAsState()

    var dressType by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var advance by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var deliveryDate by remember { mutableStateOf(System.currentTimeMillis() + 7 * 24 * 3600 * 1000L) }
    var status by remember { mutableStateOf(OrderStatus.PENDING) }
    var expandedStatus by remember { mutableStateOf(false) }
    var expandedCustomerList by remember { mutableStateOf(false) }

    LaunchedEffect(preselectedCustomerId) {
        preselectedCustomerId?.let {
            val c = customerVm.getById(it)
            selectedCustomer = c
            customerQuery = c?.name ?: ""
        }
    }

    val remaining = (total.toDoubleOrNull() ?: 0.0) - (advance.toDoubleOrNull() ?: 0.0)
    val dateStr = remember(deliveryDate) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(deliveryDate))
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Add Order", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        // Customer picker
        ExposedDropdownMenuBox(
            expanded = expandedCustomerList,
            onExpandedChange = { expandedCustomerList = it }
        ) {
            OutlinedTextField(
                value = customerQuery,
                onValueChange = {
                    customerQuery = it
                    customerVm.onQueryChange(it)
                    expandedCustomerList = true
                },
                label = { Text("Search Customer") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(
                expanded = expandedCustomerList && customers.isNotEmpty(),
                onDismissRequest = { expandedCustomerList = false }
            ) {
                customers.take(8).forEach { c ->
                    DropdownMenuItem(
                        text = { Text("${c.name} (${c.customerCode})") },
                        onClick = {
                            selectedCustomer = c
                            customerQuery = c.name
                            expandedCustomerList = false
                        }
                    )
                }
            }
        }

        Field("Dress Type", dressType) { dressType = it }
        Field("Color", color) { color = it }
        Field("Quantity", quantity) { quantity = it.filter { ch -> ch.isDigit() } }
        Field("Total Amount (Rs)", total) { total = it.filter { ch -> ch.isDigit() || ch == '.' } }
        Field("Advance Payment (Rs)", advance) { advance = it.filter { ch -> ch.isDigit() || ch == '.' } }

        Text("Remaining Payment: Rs ${remaining.toInt()}", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(10.dp))
        Text("Delivery Date: $dateStr", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(12.dp))
        ExposedDropdownMenuBox(expanded = expandedStatus, onExpandedChange = { expandedStatus = it }) {
            OutlinedTextField(
                value = status.name.lowercase().replaceFirstChar { it.uppercase() },
                onValueChange = {},
                readOnly = true,
                label = { Text("Status") },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(14.dp)
            )
            ExposedDropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) {
                OrderStatus.values().forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = { status = s; expandedStatus = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                val c = selectedCustomer ?: return@Button
                orderVm.save(
                    Order(
                        customerId = c.id,
                        customerName = c.name,
                        dressType = dressType,
                        color = color,
                        quantity = quantity.toIntOrNull() ?: 1,
                        advancePayment = advance.toDoubleOrNull() ?: 0.0,
                        totalAmount = total.toDoubleOrNull() ?: 0.0,
                        deliveryDate = deliveryDate,
                        status = status
                    )
                ) { onSaved() }
            },
            enabled = selectedCustomer != null && dressType.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Save Order")
        }
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun Field(label: String, value: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    )
}
