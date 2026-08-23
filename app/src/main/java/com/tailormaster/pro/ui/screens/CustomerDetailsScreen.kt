package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.data.Customer
import com.tailormaster.pro.ui.components.CustomerAvatar
import com.tailormaster.pro.ui.components.SectionLabel
import com.tailormaster.pro.ui.components.StatusChip
import com.tailormaster.pro.viewmodel.CustomerViewModel
import com.tailormaster.pro.viewmodel.OrderViewModel

@Composable
fun CustomerDetailsScreen(
    customerId: String,
    customerVm: CustomerViewModel,
    orderVm: OrderViewModel,
    onEdit: (String) -> Unit,
    onDeleted: () -> Unit,
    onBack: () -> Unit
) {
    var customer by remember { mutableStateOf<Customer?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val orders by orderVm.ordersForCustomer(customerId).collectAsState(initial = emptyList())

    LaunchedEffect(customerId) {
        customer = customerVm.getById(customerId)
    }

    val c = customer ?: return

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            CustomerAvatar(size = 72)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(c.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(c.customerCode, style = MaterialTheme.typography.bodyMedium)
                Text(c.phone, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Address", fontWeight = FontWeight.SemiBold)
        Text(c.address.ifBlank { "—" }, style = MaterialTheme.typography.bodyMedium)

        SectionLabel("Measurements")
        val measurements = listOf(
            "Shoulder" to c.shoulder, "Chest" to c.chest, "Waist" to c.waist, "Hip" to c.hip,
            "Neck" to c.neck, "Sleeve" to c.sleeve, "Shirt Length" to c.shirtLength,
            "Shalwar Length" to c.shalwarLength, "Bottom Width" to c.bottomWidth,
            "Cuff" to c.cuff, "Pocket" to c.pocket, "Collar" to c.collar
        )
        measurements.chunked(2).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (label, value) ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(label, style = MaterialTheme.typography.bodyMedium)
                            Text(value.ifBlank { "-" }, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        if (c.notes.isNotBlank()) {
            SectionLabel("Notes")
            Text(c.notes, style = MaterialTheme.typography.bodyMedium)
        }

        SectionLabel("Previous Orders")
        if (orders.isEmpty()) {
            Text("No orders yet", style = MaterialTheme.typography.bodyMedium)
        } else {
            orders.forEach { o ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(o.dressType, fontWeight = FontWeight.SemiBold)
                            Text("Qty: ${o.quantity}  •  Rs ${o.totalAmount.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        StatusChip(o.status)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { onEdit(c.id) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Edit")
            }
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Delete")
            }
        }
        Spacer(Modifier.height(30.dp))
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Customer?") },
            text = { Text("This will permanently delete ${c.name} and cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    customerVm.delete(c) { onDeleted() }
                    showDeleteConfirm = false
                }) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}
