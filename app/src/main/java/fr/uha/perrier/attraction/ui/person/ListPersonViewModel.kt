package fr.uha.perrier.attraction.ui.person
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.attraction.database.AppDatabase
import fr.uha.perrier.attraction.database.PersonDao

import fr.uha.perrier.attraction.model.PersonWithDetails
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ListPersonViewModel @Inject constructor (
    private val dao : PersonDao
) : ViewModel() {

    val persons : LiveData<List<PersonWithDetails>> = dao.getAll().asLiveData()

    fun delete(person: PersonWithDetails) {
        val executor : Executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            viewModelScope.launch {
                AppDatabase.get().getPersonDao().delete(person.person)
            }
        })
    }

    companion object {
        private val TAG = ListPersonViewModel::class.java.simpleName
    }

}