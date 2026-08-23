package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.data.Order
import com.tailormaster.pro.data.OrderStatus
import com.tailormaster.pro.ui.components.StatusChip
import com.tailormaster.pro.viewmodel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    vm: OrderViewModel,
    onAddOrder: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val allOrders by vm.allOrders.collectAsState()
    var filter by remember { mutableStateOf<OrderStatus?>(null) }

    val filtered = if (filter == null) allOrders else allOrders.filter { it.status == filter }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddOrder) {
                Icon(Icons.Default.Add, contentDescription = "Add Order")
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("Orders", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChipItem("All", filter == null) { filter = null }
                FilterChipItem("Pending", filter == OrderStatus.PENDING) { filter = OrderStatus.PENDING }
                FilterChipItem("Ready", filter == OrderStatus.READY) { filter = OrderStatus.READY }
                FilterChipItem("Delivered", filter == OrderStatus.DELIVERED) { filter = OrderStatus.DELIVERED }
            }

            Spacer(Modifier.height(14.dp))

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Text("No orders", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(filtered) { o -> OrderRow(o) { onOrderClick(o.id) } }
                }
            }
        }
    }
}

@Composable
private fun FilterChipItem(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun OrderRow(order: Order, onClick: () -> Unit) {
    val dateStr = remember(order.deliveryDate) {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(order.deliveryDate))
    }
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.customerName, fontWeight = FontWeight.SemiBold)
                StatusChip(order.status)
            }
            Spacer(Modifier.height(4.dp))
            Text("${order.dressType} • ${order.color} • Qty ${order.quantity}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text("Delivery: $dateStr", style = MaterialTheme.typography.bodyMedium)
            Text("Remaining: Rs ${order.remainingPayment.toInt()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
