package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import fr.uha.perrier.attraction.model.Type.Type

@Entity(tableName = "parks")
data class Park (
    @PrimaryKey(autoGenerate = true)
    val pid : Long = 0,
    val name : String = "",
    val startHour : String ="",
    val endHour : String ="",
    val type : Type = Type.NO,
    val place : String ="",
    val priceMin : String ="",
    val priceMax : String ="",
) {
}