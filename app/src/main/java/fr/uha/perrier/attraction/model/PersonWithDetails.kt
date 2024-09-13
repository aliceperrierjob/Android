package fr.uha.perrier.attraction.model

import androidx.room.Embedded

class PersonWithDetails (
    @Embedded
    var person: Person,
    var attractionCount : Int,
) { }