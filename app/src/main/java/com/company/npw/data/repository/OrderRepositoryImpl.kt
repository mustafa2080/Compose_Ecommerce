package com.company.npw.data.repository

import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.company.npw.data.remote.firebase.database.FirebaseDatabaseService
import com.company.npw.domain.model.Order
import com.company.npw.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val databaseService: FirebaseDatabaseService
) : OrderRepository {

    override fun createOrder(order: Order): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val result = databaseService.addDocument(
                collection = Constants.ORDERS_COLLECTION,
                data = order
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success(result.data ?: ""))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun getOrderById(orderId: String): Flow<Resource<Order>> {
        return databaseService.getDocument(
            collection = Constants.ORDERS_COLLECTION,
            documentId = orderId,
            clazz = Order::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val order = resource.data
                            if (order != null) {
                                emit(Resource.Success(order))
                            } else {
                                emit(Resource.Error("Order not found"))
                            }
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun getUserOrders(userId: String): Flow<Resource<List<Order>>> {
        return databaseService.getCollection(
            collection = Constants.ORDERS_COLLECTION,
            clazz = Order::class.java
        ).let { flow ->
            flow {
                flow.collect { resource ->
                    when (resource) {
                        is Resource.Success -> {
                            val userOrders = resource.data?.filter { it.userId == userId } ?: emptyList()
                            emit(Resource.Success(userOrders))
                        }
                        is Resource.Error -> emit(Resource.Error(resource.message ?: Constants.ERROR_GENERIC))
                        is Resource.Loading -> emit(Resource.Loading())
                    }
                }
            }
        }
    }

    override fun updateOrderStatus(orderId: String, status: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val updates = mapOf(
                "status" to status,
                "updatedAt" to System.currentTimeMillis()
            )
            
            val result = databaseService.updateDocument(
                collection = Constants.ORDERS_COLLECTION,
                documentId = orderId,
                updates = updates
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("Order status updated"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun cancelOrder(orderId: String): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading())
            
            val updates = mapOf(
                "status" to "CANCELLED",
                "updatedAt" to System.currentTimeMillis()
            )
            
            val result = databaseService.updateDocument(
                collection = Constants.ORDERS_COLLECTION,
                documentId = orderId,
                updates = updates
            )
            
            when (result) {
                is Resource.Success -> emit(Resource.Success("Order cancelled"))
                is Resource.Error -> emit(Resource.Error(result.message ?: Constants.ERROR_GENERIC))
                is Resource.Loading -> { /* Already emitted loading */ }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
        }
    }

    override fun trackOrder(orderId: String): Flow<Resource<Order>> {
        return getOrderById(orderId)
    }
}
