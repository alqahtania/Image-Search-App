package com.abdull.imagesearchapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*


/**
 * Created by Abdullah Alqahtani on 10/4/2020.
 */

@Dao
interface PhotoDao {

    @Insert
    suspend fun insert(photo : Photo)

    @Update
    suspend fun update(photo : Photo)

    @Delete
    suspend fun delete(photo : Photo)

    @Query("DELETE FROM Photo WHERE photo_id = :photoId")
    suspend fun deleteByPhotoId(photoId : String)

    @Query("DELETE FROM Photo")
    suspend fun deleteAll()

    @Query("SELECT * FROM Photo")
    fun getAllPaged() : PagingSource<Int, Photo>

    @Query("SELECT * FROM Photo WHERE collection_id = :collectionId")
    fun getAllPagedPhotosByCollection(collectionId : Long) : PagingSource<Int, Photo>

    @Query("SELECT photo_id FROM Photo WHERE photo_id = :photoId")
    fun findPhotoById(photoId : String) : LiveData<String>


}