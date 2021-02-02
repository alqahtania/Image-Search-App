package com.abdull.imagesearchapp.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.abdull.imagesearchapp.api.remote.UnsplashApi
import com.abdull.imagesearchapp.data.local.CollectionDao
import com.abdull.imagesearchapp.data.local.PhotoDao
import com.abdull.imagesearchapp.data.local.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Abdullah Alqahtani on 10/3/2020.
 */
@Module
@InstallIn(ApplicationComponent::class) //to make the scope at app level
object AppModule {

    private const val TAG = "AppModule"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        Log.d(TAG, "provideOkHttpClient: Called")
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        Log.d(TAG, "provideRetrofit: Called")
        return Retrofit.Builder()
            .baseUrl(UnsplashApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApi(retrofit: Retrofit): UnsplashApi =
        retrofit.create(UnsplashApi::class.java)

    @Provides
    @Singleton
    fun providePhotoDatabase(@ApplicationContext appContext: Context): PhotoDatabase {
        return Room.databaseBuilder(
            appContext,
            PhotoDatabase::class.java,
            PhotoDatabase.dbName
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun providePhotoDao(photoDatabase: PhotoDatabase): PhotoDao {
        return photoDatabase.photoDao()
    }

    @Provides
    fun provideCollectionDao(database : PhotoDatabase): CollectionDao{
        return database.collectionDao()
    }


}