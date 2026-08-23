package com.tailormaster.pro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onExportPdf: () -> Unit,
    onSignOut: () -> Unit
) {
    var showSignOutConfirm by remember { mutableStateOf(false) }

    Column(Modifier.padding(16.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(20.dp))

        SettingRow(icon = Icons.Default.Backup, title = "Backup Database", subtitle = "Save a copy of your data", onClick = onBackup)
        SettingRow(icon = Icons.Default.Restore, title = "Restore Database", subtitle = "Load data from a backup file", onClick = onRestore)
        SettingRow(icon = Icons.Default.PictureAsPdf, title = "Export PDF", subtitle = "Export customer/order report", onClick = onExportPdf)

        Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Row(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DarkMode, contentDescription = null)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text("Dark Mode", fontWeight = FontWeight.SemiBold)
                        Text("Toggle app theme", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Switch(checked = darkMode, onCheckedChange = onDarkModeChange)
            }
        }

        SettingRow(
            icon = Icons.Default.Logout,
            title = "Sign Out",
            subtitle = "Your data stays safely backed up in the cloud",
            onClick = { showSignOutConfirm = true }
        )

        Spacer(Modifier.height(30.dp))
        Text("App Version 1.0.0", style = MaterialTheme.typography.bodyMedium)
    }

    if (showSignOutConfirm) {
        AlertDialog(
            onDismissRequest = { showSignOutConfirm = false },
            title = { Text("Sign Out?") },
            text = { Text("You can sign back in anytime with the same account to get all your data back.") },
            confirmButton = {
                TextButton(onClick = { showSignOutConfirm = false; onSignOut() }) { Text("Sign Out") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
    ) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
