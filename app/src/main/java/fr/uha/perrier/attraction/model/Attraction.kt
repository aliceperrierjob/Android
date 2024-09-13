package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attractions")
class Attraction (
    @PrimaryKey(autoGenerate = true)
    val aid : Long = 0,
    val name : String = "",
    val attractionpark : String = "",
    val place : String = "",
    val category: Category = Category.NO,
    val sizeMin : String = "",
    val picture : String? = null
    ) {
        override fun toString(): String {
                return "Attraction(id=$aid, name='$name', attractionpark='$attractionpark', place='$place', category=$category, sizeMin=$sizeMin)"
        }
}
