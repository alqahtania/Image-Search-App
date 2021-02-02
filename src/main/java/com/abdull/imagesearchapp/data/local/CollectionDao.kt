package com.abdull.imagesearchapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */
@Dao
interface CollectionDao {

    @Insert
    suspend fun insert(collection: Collection) : Long

    @Update
    suspend fun update(collection: Collection)

    @Delete
    suspend fun delete(collection: Collection)


    @Query("DELETE FROM Collection")
    suspend fun deleteAll()

    @Query("SELECT * FROM Collection")
    fun getAllPagedCollections() : PagingSource<Int, Collection>

    @Query("SELECT * FROM Collection")
    fun getAllLiveDataCollectionsList() : LiveData<List<Collection>>


}