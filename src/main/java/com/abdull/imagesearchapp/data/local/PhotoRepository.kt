package com.abdull.imagesearchapp.data.local

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/4/2020.
 */
@Singleton
class PhotoRepository @Inject constructor(private val photoDao: PhotoDao) {

//    suspend fun getAllPaged() : Flow<PagingData<Photo>> {
//
//        return Pager(
//            config = PagingConfig(
//                pageSize = 60,
//                maxSize = 180,
//                enablePlaceholders = false
//            )){
//            photoDao.getAllPaged()
//        }.flow
//    }

    fun allPhotos() =
        Pager(
            config = PagingConfig(
                pageSize = 60,
                maxSize = 180,
                enablePlaceholders = false
            )){
                photoDao.getAllPaged()
            }.flow

    fun getAllPagedPhotosByCollection(collectionId: Long) =
        Pager(
            config = PagingConfig(
                pageSize = 60,
                maxSize = 180,
                enablePlaceholders = false
            )){
            photoDao.getAllPagedPhotosByCollection(collectionId)
        }.flow



    fun insert(photo: Photo) {
        CoroutineScope(IO).launch {
            photoDao.insert(photo)
        }
    }

    fun update(photo: Photo) {
        CoroutineScope(IO).launch {
            photoDao.update(photo)
        }
    }

    fun delete(photo: Photo) {
        CoroutineScope(IO).launch {
            photoDao.delete(photo)
        }
    }

    fun deleteByPhotoId(photoId : String){
        CoroutineScope(IO).launch {
            photoDao.deleteByPhotoId(photoId)
        }
    }

    fun deleteAllPhotos() {
        CoroutineScope(IO).launch {
            photoDao.deleteAll()
        }
    }

     fun findPhotoById(photoId: String): LiveData<String> {
        return photoDao.findPhotoById(photoId)
    }


}