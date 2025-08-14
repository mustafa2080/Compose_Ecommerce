package com.company.npw.data.remote.firebase.database

import com.company.npw.core.util.Constants
import com.company.npw.core.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDatabaseService @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase
) {
    
    fun <T> getDocument(
        collection: String,
        documentId: String,
        clazz: Class<T>
    ): Flow<Resource<T?>> = callbackFlow {
        trySend(Resource.Loading())
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data = snapshot.getValue(clazz)
                    trySend(Resource.Success(data))
                } catch (e: Exception) {
                    trySend(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        
        val reference = firebaseDatabase.reference.child(collection).child(documentId)
        reference.addValueEventListener(listener)
        
        awaitClose {
            reference.removeEventListener(listener)
        }
    }
    
    fun <T> getCollection(
        collection: String,
        clazz: Class<T>
    ): Flow<Resource<List<T>>> = callbackFlow {
        trySend(Resource.Loading())
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val list = mutableListOf<T>()
                    for (childSnapshot in snapshot.children) {
                        val item = childSnapshot.getValue(clazz)
                        item?.let { list.add(it) }
                    }
                    trySend(Resource.Success(list))
                } catch (e: Exception) {
                    trySend(Resource.Error(e.message ?: Constants.ERROR_GENERIC))
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(Resource.Error(error.message))
            }
        }
        
        val reference = firebaseDatabase.reference.child(collection)
        reference.addValueEventListener(listener)
        
        awaitClose {
            reference.removeEventListener(listener)
        }
    }
    
    suspend fun <T> setDocument(
        collection: String,
        documentId: String,
        data: T
    ): Resource<String> {
        return try {
            firebaseDatabase.reference
                .child(collection)
                .child(documentId)
                .setValue(data)
                .await()
            Resource.Success("Document saved successfully")
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
    
    suspend fun <T> addDocument(
        collection: String,
        data: T
    ): Resource<String> {
        return try {
            val reference = firebaseDatabase.reference.child(collection).push()
            reference.setValue(data).await()
            Resource.Success(reference.key ?: "")
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
    
    suspend fun updateDocument(
        collection: String,
        documentId: String,
        updates: Map<String, Any>
    ): Resource<String> {
        return try {
            firebaseDatabase.reference
                .child(collection)
                .child(documentId)
                .updateChildren(updates)
                .await()
            Resource.Success("Document updated successfully")
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
    
    suspend fun deleteDocument(
        collection: String,
        documentId: String
    ): Resource<String> {
        return try {
            firebaseDatabase.reference
                .child(collection)
                .child(documentId)
                .removeValue()
                .await()
            Resource.Success("Document deleted successfully")
        } catch (e: Exception) {
            Resource.Error(e.message ?: Constants.ERROR_GENERIC)
        }
    }
}
