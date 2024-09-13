package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "apas",
    primaryKeys = ["personid", "aid"],
    indices = [Index("personid"), Index("aid")]
)
class AttractionPersonAssociation (
    val personid : Long,
    val aid : Long
) {

}