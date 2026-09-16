package com.marco.calignano.nocinoapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface NocinoDao {

    @Upsert
    suspend fun upsertMix(mix: Mix): Long

    @Upsert
    suspend fun upsertSpirits(spirits: List<Spirit>)
    
    @Upsert
    suspend fun upsertSpirit(spirit: Spirit)

    @Upsert
    suspend fun upsertSpices(spices: List<Spice>)

    @Upsert
    suspend fun upsertSpice(spice: Spice)

    @Query("SELECT * FROM mixes ORDER BY name ASC")
    fun getAllMixes(): Flow<List<Mix>>

    @Query("SELECT * FROM MixSummary")
    fun getMixSummaries(): Flow<List<MixSummary>>

    @Query("SELECT * FROM MixSummary WHERE mixId = :mixId")
    fun getMixSummaryById(mixId: Long): Flow<List<MixSummary>>
}
