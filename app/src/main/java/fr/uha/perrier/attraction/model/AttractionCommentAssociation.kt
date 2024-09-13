package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "acas",
    primaryKeys = ["aid", "cid"],
    indices = [Index("aid"), Index("cid")]
)
class AttractionCommentAssociation (
        val aid : Long,
        val cid : Long
    ) {

}
