package fr.uha.perrier.attraction.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class FullPark (
    @Embedded
    var park : Park,

    @Relation(
        parentColumn = "pid",
        entityColumn = "aid",
        associateBy = Junction(ParkAttractionAssociation::class)
    )
    var attractions : MutableList<Attraction>
) { }