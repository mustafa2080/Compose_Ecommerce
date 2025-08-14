package com.company.npw.domain.repository

import com.company.npw.core.util.Resource
import com.company.npw.domain.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun createOrder(order: Order): Flow<Resource<String>>
    fun getOrderById(orderId: String): Flow<Resource<Order>>
    fun getUserOrders(userId: String): Flow<Resource<List<Order>>>
    fun updateOrderStatus(orderId: String, status: String): Flow<Resource<String>>
    fun cancelOrder(orderId: String): Flow<Resource<String>>
    fun trackOrder(orderId: String): Flow<Resource<Order>>
}
