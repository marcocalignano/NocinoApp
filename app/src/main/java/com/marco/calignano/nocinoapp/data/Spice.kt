package com.marco.calignano.nocinoapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "spices",
    foreignKeys = [
        ForeignKey(
            entity = Mix::class,
            parentColumns = ["mixId"],
            childColumns = ["mixId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Spice(
    @PrimaryKey(autoGenerate = true)
    val spiceId: Long = 0,
    val mixId: Long,
    val name: String,
    val quantity: Int
)