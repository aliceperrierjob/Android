package fr.uha.perrier.attraction.ui.attraction

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.attraction.database.AppDatabase
import fr.uha.perrier.attraction.database.AttractionDao
import fr.uha.perrier.attraction.model.Attraction
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ListAttractionViewModel @Inject constructor (
    private val dao : AttractionDao
) : ViewModel() {

    val attractions : LiveData<List<Attraction>> = dao.getAll().asLiveData()

    fun delete(attraction: Attraction) {
        val executor : Executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            viewModelScope.launch {
                AppDatabase.get().getAttractionDao().delete(attraction)
            }
        })
    }

    companion object {
        private val TAG = ListAttractionViewModel::class.java.simpleName
    }

}