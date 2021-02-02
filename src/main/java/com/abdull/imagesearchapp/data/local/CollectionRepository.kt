package com.abdull.imagesearchapp.data.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */
@Singleton
class CollectionRepository @Inject constructor(private val collectionDao: CollectionDao) {

    val allCollections =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                maxSize = 100,
                enablePlaceholders = false
            )
        ) {
            collectionDao.getAllPagedCollections()
        }.flow

    val allLiveDataCollections =
        collectionDao.getAllLiveDataCollectionsList()

    suspend fun insert(collection: Collection): Long {
        return collectionDao.insert(collection)
    }

    fun update(collection: Collection) {
        CoroutineScope(IO).launch {
            collectionDao.update(collection)
        }
    }

    fun delete(collection: Collection) {
        CoroutineScope(IO).launch {
            collectionDao.delete(collection)
        }
    }

    fun deleteAllCollections() {
        CoroutineScope(IO).launch {
            collectionDao.deleteAll()
        }
    }


}