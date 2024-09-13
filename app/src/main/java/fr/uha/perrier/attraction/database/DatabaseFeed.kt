package fr.uha.perrier.attraction.database

import fr.uha.perrier.attraction.model.*
import fr.uha.perrier.attraction.model.Type.Type
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class DatabaseFeed {

    fun feed () {
        val executor : Executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            MainScope().launch {
            //    val cids = feedComment()
                val pids = feedAttraction()
                feedPerson(pids)
                feedPark (pids)
            }
        })
    }

    private suspend fun feedAttraction() : LongArray {
        val dao = AppDatabase.get().getAttractionDao()
        var ids : LongArray = LongArray(4)
        ids[0] = dao.create(Attraction(0,"Pirates des Caraibes", "Disneyland Paris", "Paris - France", Category.FAMILLE, "Pas de restriction", ))
        ids[1] = dao.create(Attraction(0,"Big Thunder Montain", "Disneyland Paris", "Paris - France", Category.SENSATION, "102 cm", ))
        ids[2] = dao.create(Attraction(0,"Blue Fire", "Europapark", "Rust - Allemagne", Category.SENSATION, "130 cm", ))
        ids[3] = dao.create(Attraction(0,"Dugdrop", "Rulantica", "Rust - Allemagne", Category.SENSATION, "140 cm", ))

       /* dao.addAssociation (AttractionCommentAssociation(ids[1], comments[0]))
        dao.addAssociation (AttractionCommentAssociation(ids[1], comments[1]))
        dao.addAssociation (AttractionCommentAssociation(ids[1], comments[2]))
        dao.addAssociation (AttractionCommentAssociation(ids[1], comments[3]))*/
        return ids
    }

    private suspend fun feedPark( attractions : LongArray) {
        val dao = AppDatabase.get().getParkDao()
        val tidDisney : Long = dao.upsert(Park(0, "Disneyland Paris", "9h30", "21h00", Type.ATTRACTION,"Marne-la-Vallée - FRANCE","56€","120€"))
        dao.addAssociation (ParkAttractionAssociation(tidDisney, attractions[0]))
        dao.addAssociation (ParkAttractionAssociation(tidDisney, attractions[1]))
        val tidRulantica : Long = dao.upsert(Park(0, "Rulantica", "10h00", "22h00", Type.AQUATIQUE,"Rust - ALLEMAGNE","42€","52€"))
        dao.addAssociation (ParkAttractionAssociation(tidRulantica, attractions[3]))
    }

    private suspend fun feedPerson( attractions : LongArray) {
        val dao = AppDatabase.get().getPersonDao()
        val pid : Long = dao.upsert(Person(0, "Alice", "Perrier", 22))
        dao.addAssociation (AttractionPersonAssociation(pid, attractions[0]))
        dao.addAssociation (AttractionPersonAssociation(pid, attractions[1]))
        val tidRulantica : Long = dao.upsert(Person(0, "Alix", "Yang", 11))
        dao.addAssociation (AttractionPersonAssociation(tidRulantica, attractions[3]))
    }

    /*private suspend fun feedComment() : LongArray {
        val dao = AppDatabase.get().getCommentDao()
        var comments : LongArray = LongArray(4)
        comments[0] = dao.create(Comment(0,"Alice", "C'était bien", 4))
        comments[1] = dao.create(Comment(0,"Alice 2", "Non autorisé aux cardiaques", 2))
        comments[2] = dao.create(Comment(0,"Alice 3", "Nul", 0))
        comments[3] = dao.create(Comment(0,"Alice 4", "Attente trop longue", 2))
        return comments
    }*/


}