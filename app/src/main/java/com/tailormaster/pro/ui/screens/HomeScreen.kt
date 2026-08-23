package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.ui.components.DashboardStatCard
import com.tailormaster.pro.ui.theme.*
import com.tailormaster.pro.viewmodel.CustomerViewModel
import com.tailormaster.pro.viewmodel.OrderViewModel

@Composable
fun HomeScreen(
    customerVm: CustomerViewModel,
    orderVm: OrderViewModel,
    onAddCustomer: () -> Unit
) {
    val customerCount by customerVm.customerCount.collectAsState()
    val pending by orderVm.pendingCount.collectAsState()
    val ready by orderVm.readyCount.collectAsState()
    val delivered by orderVm.deliveredCount.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddCustomer,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Add Customer") }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .padding(20.dp)
        ) {
            Text("Tailor Master Pro", style = MaterialTheme.typography.headlineMedium)
            Text("Welcome back", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                DashboardStatCard(
                    title = "Total Customers",
                    value = customerCount.toString(),
                    icon = { Icon(Icons.Default.Groups, contentDescription = null, tint = GreenPrimary) },
                    containerColor = GreenLight,
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = "Pending Orders",
                    value = pending.toString(),
                    icon = { Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = StatusPending) },
                    containerColor = StatusPending.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                DashboardStatCard(
                    title = "Ready Orders",
                    value = ready.toString(),
                    icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = StatusReady) },
                    containerColor = StatusReady.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
                DashboardStatCard(
                    title = "Delivered Orders",
                    value = delivered.toString(),
                    icon = { Icon(Icons.Default.LocalShipping, contentDescription = null, tint = StatusDelivered) },
                    containerColor = StatusDelivered.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
