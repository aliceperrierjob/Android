package fr.uha.perrier.attraction.database

import androidx.room.*
import fr.uha.perrier.attraction.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {

    @Query("SELECT * "
            + ", (SELECT COUNT(*) FROM apas APA WHERE APA.personid = P.personid) AS attractionCount"
            + " FROM persons as P")
    fun getAll () : Flow<List<PersonWithDetails>>

    @Transaction
    @Query("SELECT * FROM persons WHERE personid = :id")
    fun getById (id : Long) : Flow<FullPerson>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun create (person: Person) : Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert (person: Person) : Long

    @Delete
    suspend fun delete (person : Person)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAssociation (a: AttractionPersonAssociation)

    @Delete
    suspend fun deleteAssociation (a: AttractionPersonAssociation)


}