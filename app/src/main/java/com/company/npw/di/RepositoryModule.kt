package com.company.npw.di

import com.company.npw.data.repository.AuthRepositoryImpl
import com.company.npw.data.repository.CartRepositoryImpl
import com.company.npw.data.repository.OrderRepositoryImpl
import com.company.npw.data.repository.ProductRepositoryImpl
import com.company.npw.data.repository.UserRepositoryImpl
import com.company.npw.domain.repository.AuthRepository
import com.company.npw.domain.repository.CartRepository
import com.company.npw.domain.repository.OrderRepository
import com.company.npw.domain.repository.ProductRepository
import com.company.npw.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository
}
