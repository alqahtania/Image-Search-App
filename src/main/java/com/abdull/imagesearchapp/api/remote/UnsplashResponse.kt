package com.abdull.imagesearchapp.api.remote

import com.abdull.imagesearchapp.data.remote.UnsplashPhoto

/**
 * Created by Abdullah Alqahtani on 10/3/2020.
 */
data class UnsplashResponse(
    val results: List<UnsplashPhoto>
)