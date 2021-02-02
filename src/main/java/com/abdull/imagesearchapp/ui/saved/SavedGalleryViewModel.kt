package com.abdull.imagesearchapp.ui.saved

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.abdull.imagesearchapp.data.local.Photo
import com.abdull.imagesearchapp.data.local.PhotoRepository

/**
 * Created by Abdullah Alqahtani on 10/4/2020.
 */
class SavedGalleryViewModel @ViewModelInject constructor(
    private val photoRepository : PhotoRepository
) : ViewModel() {


    val photos = photoRepository.allPhotos().cachedIn(viewModelScope)

    private var currentViewPhoto = MutableLiveData("")

    fun getAllPagedPhotosByCollection(collectionId : Long) =
        photoRepository.getAllPagedPhotosByCollection(collectionId)

    val findPhotoById = currentViewPhoto.switchMap {
        photoId -> photoRepository.findPhotoById(photoId)
    }
    fun insert(photo : Photo){
        photoRepository.insert(photo)
    }

    fun update(photo : Photo){
        photoRepository.update(photo)
    }

    fun delete(photo : Photo){
        photoRepository.delete(photo)
    }
    fun deleteAll(){
        photoRepository.deleteAllPhotos()
    }
    fun deleteByPhotoId(photoId : String){
        photoRepository.deleteByPhotoId(photoId)
    }

    fun findPhotoById(photoId : String){
        currentViewPhoto.value = photoId
    }


}