package fr.uha.perrier.attraction.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

class FullPerson (
    @Embedded
    var person : Person,


    @Relation(
        parentColumn = "personid",
        entityColumn = "aid",
        associateBy = Junction(AttractionPersonAssociation::class)
    )
    var attractions : MutableList<Attraction>
) { }