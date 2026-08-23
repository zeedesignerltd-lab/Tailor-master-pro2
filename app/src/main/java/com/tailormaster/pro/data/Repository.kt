package com.tailormaster.pro.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * All reads/writes go through Firestore under users/{uid}/customers and users/{uid}/orders.
 * Firestore has offline persistence enabled by default on Android: writes made offline are
 * queued locally and automatically pushed to the cloud once connectivity returns, and reads
 * are served from the local cache immediately then updated when fresh data arrives.
 */
class Repository(private val uid: String) {
    private val db = FirebaseFirestore.getInstance()
    private val customersRef = db.collection("users").document(uid).collection("customers")
    private val ordersRef = db.collection("users").document(uid).collection("orders")

    // ---- Customers ----

    fun getAllCustomers(): Flow<List<Customer>> = callbackFlow {
        val listener = customersRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Customer::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun searchCustomers(query: String): Flow<List<Customer>> = callbackFlow {
        val listener = customersRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Customer::class.java)?.copy(id = doc.id)
                }?.filter {
                    it.name.contains(query, ignoreCase = true) ||
                        it.customerCode.contains(query, ignoreCase = true)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun getCustomerById(id: String): Customer? {
        val doc = customersRef.document(id).get().await()
        return doc.toObject(Customer::class.java)?.copy(id = doc.id)
    }

    fun getCustomerCount(): Flow<Int> = callbackFlow {
        val listener = customersRef.addSnapshotListener { snapshot, _ ->
            trySend(snapshot?.size() ?: 0)
        }
        awaitClose { listener.remove() }
    }

    suspend fun insertCustomer(customer: Customer): String {
        val doc = customersRef.document()
        doc.set(customer.copy(id = doc.id)).await()
        return doc.id
    }

    suspend fun updateCustomer(customer: Customer) {
        customersRef.document(customer.id).set(customer).await()
    }

    suspend fun deleteCustomer(customer: Customer) {
        customersRef.document(customer.id).delete().await()
    }

    suspend fun generateCustomerCode(name: String): String {
        val cleanName = name.trim().replaceFirstChar { it.uppercase() }.split(" ").firstOrNull() ?: name
        val existing = customersRef.whereEqualTo("name", name).get().await().size()
        val number = (existing + 1).toString().padStart(3, '0')
        return "$cleanName-$number"
    }

    // ---- Orders ----

    fun getAllOrders(): Flow<List<Order>> = callbackFlow {
        val listener = ordersRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun getOrdersByCustomer(customerId: String): Flow<List<Order>> = callbackFlow {
        val listener = ordersRef.whereEqualTo("customerId", customerId)
            .addSnapshotListener { snapshot, _ ->
                val list = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Order::class.java)?.copy(id = doc.id)
                }?.sortedByDescending { it.createdAt } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun countOrdersByStatus(status: OrderStatus): Flow<Int> = callbackFlow {
        val listener = ordersRef.whereEqualTo("status", status.name)
            .addSnapshotListener { snapshot, _ ->
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { listener.remove() }
    }

    suspend fun insertOrder(order: Order): String {
        val doc = ordersRef.document()
        doc.set(order.copy(id = doc.id)).await()
        return doc.id
    }

    suspend fun updateOrder(order: Order) {
        ordersRef.document(order.id).set(order).await()
    }

    suspend fun deleteOrder(order: Order) {
        ordersRef.document(order.id).delete().await()
    }
}
