package fr.uha.perrier.attraction.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import fr.uha.perrier.attraction.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ParkDao {

    @Query("SELECT * "
        + ", (SELECT COUNT(*) FROM ppas PPA WHERE PPA.pid = T.pid) AS memberCount"
        + " FROM parks as T")
    fun getAll () : Flow<List<ParkWithDetails>>

    @Transaction
    @Query("SELECT * FROM parks WHERE pid = :id")
    fun getById (id : Long) : Flow<FullPark>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (park: Park) : Long

    @Delete
    suspend fun delete (park : Park)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAssociation (a: ParkAttractionAssociation)

    @Delete
    suspend fun deleteAssociation (a: ParkAttractionAssociation)
}