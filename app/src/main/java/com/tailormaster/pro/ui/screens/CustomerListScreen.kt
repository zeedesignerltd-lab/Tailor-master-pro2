package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tailormaster.pro.ui.components.CustomerAvatar
import com.tailormaster.pro.viewmodel.CustomerViewModel

@Composable
fun CustomerListScreen(
    vm: CustomerViewModel,
    onCustomerClick: (String) -> Unit
) {
    val query by vm.query.collectAsState()
    val customers by vm.customers.collectAsState()

    Column(Modifier.padding(16.dp)) {
        Text("Customers", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = query,
            onValueChange = vm::onQueryChange,
            placeholder = { Text("Search by name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShapeDefault(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))

        if (customers.isEmpty()) {
            Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                Text("No customers found", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(customers) { c ->
                    Card(
                        onClick = { onCustomerClick(c.id) },
                        shape = RoundedCornerShapeDefault(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CustomerAvatar()
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(c.name, fontWeight = FontWeight.SemiBold)
                                Text(c.customerCode, style = MaterialTheme.typography.bodyMedium)
                                Text(c.phone, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoundedCornerShapeDefault() = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
