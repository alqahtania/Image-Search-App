package com.abdull.imagesearchapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/4/2020.
 */
@Database(entities = [Collection::class, Photo::class], version = 1, exportSchema = false)
@Singleton
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao
    abstract fun collectionDao(): CollectionDao
    companion object {
        const val dbName = "unsplash_photos"

    }
}