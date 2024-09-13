package fr.uha.perrier.attraction.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import fr.uha.perrier.attraction.model.Attraction
import fr.uha.perrier.attraction.model.AttractionCommentAssociation
import kotlinx.coroutines.flow.Flow

@Dao
interface AttractionDao {

    @Query("SELECT * FROM attractions")
    fun getAll () : Flow<List<Attraction>>

    @Query("SELECT * FROM attractions WHERE aid = :id")
    fun getById (id : Long) : Flow<Attraction>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create (attraction: Attraction) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update (attraction: Attraction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (attraction: Attraction)

    @Delete
    suspend fun delete (attraction : Attraction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAssociation (a: AttractionCommentAssociation)

    @Delete
    suspend fun deleteAssociation (a: AttractionCommentAssociation)


}