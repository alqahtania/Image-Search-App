package com.abdull.imagesearchapp.data.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.abdull.imagesearchapp.api.remote.UnsplashApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/3/2020.
 */
@Singleton
class UnsplashRepository @Inject constructor(private val unsplashApi: UnsplashApi) {

    fun getSearchResults(query : String) =
        Pager(
            config = PagingConfig(
                prefetchDistance = 80,
                pageSize = 100, // this will go to params.loadSize in UnsplashPagingSource class response val
                maxSize = 260, // specifies what number of items in list to hold before start to drop data
                              // if we don't specify the maxSize number the list will keep growing and use a lot of memory
               enablePlaceholders = false
            ),
            pagingSourceFactory = { UnsplashPagingSource(unsplashApi, query) }
        ).liveData
}