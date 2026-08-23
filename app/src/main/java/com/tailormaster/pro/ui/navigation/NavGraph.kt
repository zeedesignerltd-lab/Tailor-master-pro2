package com.tailormaster.pro.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tailormaster.pro.ui.screens.*
import com.tailormaster.pro.viewmodel.CustomerViewModel
import com.tailormaster.pro.viewmodel.OrderViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Customers : Screen("customers")
    object Orders : Screen("orders")
    object Settings : Screen("settings")
    object AddCustomer : Screen("add_customer")
    object EditCustomer : Screen("edit_customer/{id}")
    object CustomerDetails : Screen("customer_details/{id}")
    object AddOrder : Screen("add_order?customerId={customerId}")
}

private data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun AppNavGraph(
    customerVm: CustomerViewModel,
    orderVm: OrderViewModel,
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    onSignOut: () -> Unit
) {
    val navController = rememberNavController()
    val bottomItems = listOf(
        BottomItem(Screen.Home.route, "Home", Icons.Default.Home),
        BottomItem(Screen.Customers.route, "Customers", Icons.Default.Groups),
        BottomItem(Screen.Orders.route, "Orders", Icons.Default.ShoppingBag),
        BottomItem(Screen.Settings.route, "Settings", Icons.Default.Settings)
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = bottomItems.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(if (showBottomBar) padding else PaddingValues(0.dp))
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(customerVm, orderVm, onAddCustomer = { navController.navigate(Screen.AddCustomer.route) })
            }
            composable(Screen.Customers.route) {
                CustomerListScreen(customerVm, onCustomerClick = { id ->
                    navController.navigate("customer_details/$id")
                })
            }
            composable(Screen.Orders.route) {
                OrdersScreen(
                    orderVm,
                    onAddOrder = { navController.navigate("add_order") },
                    onOrderClick = {}
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    darkMode = darkMode,
                    onDarkModeChange = onDarkModeChange,
                    onBackup = {}, onRestore = {}, onExportPdf = {},
                    onSignOut = onSignOut
                )
            }
            composable(Screen.AddCustomer.route) {
                AddEditCustomerScreen(customerVm, editingId = null, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(
                Screen.EditCustomer.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id")
                AddEditCustomerScreen(customerVm, editingId = id, onSaved = { navController.popBackStack() }, onBack = { navController.popBackStack() })
            }
            composable(
                Screen.CustomerDetails.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: ""
                CustomerDetailsScreen(
                    customerId = id,
                    customerVm = customerVm,
                    orderVm = orderVm,
                    onEdit = { navController.navigate("edit_customer/$it") },
                    onDeleted = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                "add_order?customerId={customerId}",
                arguments = listOf(navArgument("customerId") { type = NavType.StringType; nullable = true; defaultValue = null })
            ) { entry ->
                val cid = entry.arguments?.getString("customerId")
                AddEditOrderScreen(customerVm, orderVm, preselectedCustomerId = cid, onSaved = { navController.popBackStack() })
            }
        }
    }
}
