package com.marco.calignano.nocinoapp.data

import androidx.room.DatabaseView

@DatabaseView(
    """
    SELECT
        m.mixId,
        m.name AS mixName,
        m.waterContent,
        m.sugarSyrupContent,
        s.name AS spiritName,
        s.alcoholPercentage AS spiritAlcoholPercentage,
        s.quantity AS spiritQuantity,
        sp.name AS spiceName,
        sp.quantity AS spiceQuantity
    FROM mixes AS m
    LEFT JOIN spirits AS s ON s.mixId = m.mixId
    LEFT JOIN spices AS sp ON m.mixId = sp.mixId
"""
)
data class MixSummary(
    val mixId: Long,
    val mixName: String,
    val waterContent: Int,
    val sugarSyrupContent: Int,
    val spiritName: String?,
    val spiritAlcoholPercentage: Double?,
    val spiritQuantity: Int?,
    val spiceName: String?,
    val spiceQuantity: Int?
)
