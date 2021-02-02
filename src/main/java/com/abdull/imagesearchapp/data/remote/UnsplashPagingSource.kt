package com.abdull.imagesearchapp.data.remote

import android.util.Log
import androidx.paging.PagingSource
import com.abdull.imagesearchapp.api.remote.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Abdullah Alqahtani on 10/3/2020.
 */

private const val UNSPLASH_STARTING_PAGE_INDEX = 1
class UnsplashPagingSource(
    private val unsplashApi : UnsplashApi,
    private val query : String
) : PagingSource<Int, UnsplashPhoto>() {

    companion object{
        private const val TAG = "UnsplashPagingSource"
    }


    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashPhoto> {
        val position = params.key ?: UNSPLASH_STARTING_PAGE_INDEX

       return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize)
            val photos = response.results

           Log.d(TAG, "load: photos size = ${photos.size} ")
           LoadResult.Page(
               data = photos,
               prevKey = if(position == UNSPLASH_STARTING_PAGE_INDEX) null else position -1,
               nextKey = if(photos.isEmpty()) null else position + 1
           )
        } catch (exception: IOException){
           LoadResult.Error(exception)

       } catch (exception: HttpException){
           LoadResult.Error(exception)
       }
    }
}