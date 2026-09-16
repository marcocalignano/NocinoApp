package com.marco.calignano.nocinoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "spirits",
    foreignKeys = [ForeignKey(
        entity = Mix::class,
        parentColumns = ["mixId"],
        childColumns = ["mixId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Spirit(
    @PrimaryKey(autoGenerate = true)
    val spiritId: Long = 0,
    @ColumnInfo(index = true)
    val mixId: Long,
    val name: String,
    val alcoholPercentage: Double,
    val quantity: Int
)
