package fr.uha.perrier.attraction.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "persons")
class Person (
    @PrimaryKey(autoGenerate = true)
    val personid : Long = 0,
    val firstName : String = "",
    val lastName : String = "",
    val age : Int = 0
    ) {
        override fun toString(): String {
                return "Person(id=$personid, firstName='$firstName', lastName='$lastName', age='$age')"
        }
}