package com.marco.calignano.nocinoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mixes")
data class Mix(
    @PrimaryKey(autoGenerate = true)
    val mixId: Long = 0,
    val name: String,
    val waterContent: Int,
    val sugarSyrupContent: Int
)
