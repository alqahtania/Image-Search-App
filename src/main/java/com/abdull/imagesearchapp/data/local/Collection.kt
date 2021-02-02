package com.abdull.imagesearchapp.data.local

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Created by Abdullah Alqahtani on 10/6/2020.
 */

@Entity(
    indices = arrayOf
        (
        Index(name = "index_Collection_id", value = arrayOf("id"), unique = true),
        Index(name = "index_Collection_name", value = arrayOf("name"), unique = true)
    )
)
@Parcelize
data class Collection(val name: String, val description: String?) : Parcelable {

    @PrimaryKey(autoGenerate = true)

    var id: Long = 0
}