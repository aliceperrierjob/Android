package fr.uha.perrier.attraction.model

import androidx.room.Embedded

class ParkWithDetails (
    @Embedded
    var park : Park,
    var memberCount : Int,
) { }