package com.abdull.imagesearchapp.ui.collection

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.abdull.imagesearchapp.data.local.Collection
import com.abdull.imagesearchapp.data.local.CollectionRepository

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */
class CollectionViewModel @ViewModelInject constructor(
    private val collectionRepository: CollectionRepository
) : ViewModel() {

    val collections = collectionRepository.allCollections.cachedIn(viewModelScope)

    val allLiveDataCollections = collectionRepository.allLiveDataCollections

    suspend fun insert(collection : Collection) : Long{
        return collectionRepository.insert(collection)
    }

    fun update(collection: Collection){
        collectionRepository.update(collection)
    }

    fun delete(collection: Collection){
        collectionRepository.delete(collection)
    }

    fun deleteAllCollections(){
        collectionRepository.deleteAllCollections()
    }
}