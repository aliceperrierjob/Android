package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "ppas",
    primaryKeys = ["pid", "aid"],
    indices = [Index("pid"), Index("aid")]
)
class ParkAttractionAssociation (
    val pid : Long,
    val aid : Long
) {

}