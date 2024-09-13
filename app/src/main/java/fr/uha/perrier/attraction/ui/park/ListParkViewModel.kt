package fr.uha.perrier.attraction.ui.park

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.attraction.database.AppDatabase
import fr.uha.perrier.attraction.database.ParkDao
import fr.uha.perrier.attraction.model.ParkWithDetails
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class ListParkViewModel @Inject constructor (
    private val dao : ParkDao
) : ViewModel() {

    val parks : LiveData<List<ParkWithDetails>> = dao.getAll().asLiveData()

    fun delete(park: ParkWithDetails) {
        val executor : Executor = Executors.newSingleThreadExecutor()
        executor.execute(Runnable {
            viewModelScope.launch {
                AppDatabase.get().getParkDao().delete(park.park)
            }
        })
    }

    companion object {
        private val TAG = ListParkViewModel::class.java.simpleName
    }

}