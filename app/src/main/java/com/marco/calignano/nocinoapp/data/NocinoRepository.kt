package com.marco.calignano.nocinoapp.data

import kotlinx.coroutines.flow.Flow

class NocinoRepository(private val nocinoDao: NocinoDao) {

    /**
     * Retrieves a real-time list of all Nocino mixes from the database.
     */
    fun collectAllNocinos(): Flow<List<Mix>> {
        return nocinoDao.getAllMixes()
    }

    /**
     * Retrieves a detailed view of a specific Nocino mix, including all its
     * spirit and spice components.
     * @param mixId The ID of the mix to retrieve.
     */
    fun collectNocinoById(mixId: Long): Flow<List<MixSummary>> {
        return nocinoDao.getMixSummaryById(mixId)
    }

    /**
     * Inserts a new Nocino recipe into the database. This includes the main
     * mix details, plus all its associated spirits and spices.
     * @param mix The main details of the mix.
     * @param spirits The list of spirits to associate with the mix.
     * @param spices The list of spices to associate with the mix.
     * @return The ID of the newly created mix.
     */
    suspend fun insertNewNocino(mix: Mix, spirits: List<Spirit>, spices: List<Spice>): Long {
        // Insert the mix and get its newly generated ID
        val newMixId = nocinoDao.upsertMix(mix)

        // Associate the new mixId with each spirit and insert them
        val spiritsWithMixId = spirits.map { it.copy(mixId = newMixId) }
        nocinoDao.upsertSpirits(spiritsWithMixId)

        // Associate the new mixId with each spice and insert them
        val spicesWithMixId = spices.map { it.copy(mixId = newMixId) }
        nocinoDao.upsertSpices(spicesWithMixId)

        return newMixId
    }

    /**
     * Updates an existing Nocino recipe. This will update the mix details
     * and overwrite its associated spirits and spices.
     * @param mix The mix to update. Must have a valid ID.
     * @param spirits The new list of spirits for the mix.
     * @param spices The new list of spices for the mix.
     */
    suspend fun updateNocino(mix: Mix, spirits: List<Spirit>, spices: List<Spice>) {
        // The mixId must be valid for an update
        val mixId = mix.mixId
        require(mixId != 0L) { "Mix ID must be valid for an update." }

        // Upsert the mix details
        nocinoDao.upsertMix(mix)

        // Associate the mixId with each spirit and upsert them
        val spiritsWithMixId = spirits.map { it.copy(mixId = mixId) }
        nocinoDao.upsertSpirits(spiritsWithMixId)

        // Associate the mixId with each spice and upsert them
        val spicesWithMixId = spices.map { it.copy(mixId = mixId) }
        nocinoDao.upsertSpices(spicesWithMixId)
    }

    /**
     * Adds a single spirit to a mix.
     * @param spirit The spirit to add. Must have a valid mixId.
     */
    suspend fun addSpirit(spirit: Spirit) {
        require(spirit.mixId != 0L) { "Spirit must be associated with a valid mix." }
        nocinoDao.upsertSpirits(listOf(spirit))
    }

    /**
     * Adds a single spice to a mix.
     * @param spice The spice to add. Must have a valid mixId.
     */
    suspend fun addSpice(spice: Spice) {
        require(spice.mixId != 0L) { "Spice must be associated with a valid mix." }
        nocinoDao.upsertSpices(listOf(spice))
    }
}