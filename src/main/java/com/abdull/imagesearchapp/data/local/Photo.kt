package com.abdull.imagesearchapp.data.local

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

/**
 * Created by Abdullah Alqahtani on 10/4/2020.
 */

@Entity(
    indices = arrayOf(Index(value = ["photo_id"])),
    foreignKeys = [
        ForeignKey(
            entity = Collection::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class Photo(
    @ColumnInfo(name = "collection_id") val collectionId: Long,
    @ColumnInfo(name = "photo_id") val photoId: String,
    @ColumnInfo(name = "photo_description") val photoDescription: String?,
    @ColumnInfo(name = "photo_raw") val photoRaw: String?,
    @ColumnInfo(name = "photo_full") val photoFull: String?,
    @ColumnInfo(name = "photo_regular") val photoRegular: String?,
    @ColumnInfo(name = "photo_small") val photoSmall: String?,
    @ColumnInfo(name = "photo_thumb") val photoThumb: String?,
    @ColumnInfo(name = "creator_name") val user: String,
    @ColumnInfo(name = "creator_username") val userName: String
) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0


    @Ignore
    fun getAttributionUrl(): String {
        return "https://unsplash.com/$userName?utm_source=ImageSearchApp&utm_medium=referral"
    }


}