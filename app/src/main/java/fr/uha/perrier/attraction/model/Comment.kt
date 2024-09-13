package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
class Comment (
    @PrimaryKey(autoGenerate = true)
        val cid : Long = 0,
        val user : String = "",
        val comment : String = "",
        val note : Int = 0,
        ) {
            override fun toString(): String {
                    return "Comment(id=$cid, user='$user', comment='$comment', note='$note')"
            }
}