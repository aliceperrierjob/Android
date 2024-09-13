package fr.uha.perrier.attraction.database

import androidx.room.TypeConverter
import java.util.*

object DatabaseConverters {

    @TypeConverter
    fun long2Date (date : Long) : Date? {
        return if (date == -1L) null else Date(date)
    }

    @TypeConverter
    fun date2Long (date : Date?) : Long {
        return date?.time ?: -1L
    }

}