package fr.uha.perrier.attraction.database

import android.content.Context
import androidx.room.Database;
import androidx.room.Room
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters
import fr.uha.perrier.attraction.model.*

@Database(entities = [Attraction::class, Person::class, AttractionPersonAssociation::class, Park::class, Comment::class, AttractionCommentAssociation::class, ParkAttractionAssociation::class], version = 1, exportSchema = false)
@TypeConverters(DatabaseConverters::class)
public abstract class AppDatabase : RoomDatabase() {

    companion object {
        private lateinit var instance : AppDatabase;

        @Synchronized
        fun create (context : Context) : AppDatabase {
            instance = Room.databaseBuilder(context, AppDatabase::class.java, "attractions.db").build()
            return instance
        }

        @Synchronized
        fun get () : AppDatabase {
            return instance
        }

    }

    abstract fun getAttractionDao () : AttractionDao
    abstract fun getParkDao () : ParkDao
   // abstract fun getCommentDao() : CommentDao
    abstract fun getPersonDao() : PersonDao

}