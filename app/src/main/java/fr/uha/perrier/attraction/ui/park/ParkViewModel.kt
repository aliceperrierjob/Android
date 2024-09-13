package fr.uha.perrier.attraction.ui.park

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.android.livedata.Transformations
import fr.uha.perrier.attraction.database.ParkDao
import fr.uha.perrier.attraction.model.*
import fr.uha.perrier.attraction.model.Type.Type
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParkViewModel @Inject constructor (
    private val parkDao : ParkDao,
)
    : ViewModel() {

    private val id = MutableLiveData<Long> ()

    private val _fullpark : LiveData<FullPark> = Transformations.switchMap(id)
        {
            id -> parkDao.getById(id).asLiveData()
        }

    var _park : MutableLiveData<Park> = Transformations.map(_fullpark, { it.park })

    var name : MutableLiveData<String> = Transformations.map(_park, { it.name })
    var startHour : MutableLiveData<String> = Transformations.map(_park, { it.startHour })
    var endHour : MutableLiveData<String> = Transformations.map(_park, { it.endHour })
    var place : MutableLiveData<String> = Transformations.map(_park, { it.place })
    val typePosition: MutableLiveData<Int> = Transformations.map(_park) { p -> ParkViewModel.getTypeIndexFromType(p.type) }
    var priceMin : MutableLiveData<String> = Transformations.map(_park, { it.priceMin })
    var priceMax : MutableLiveData<String> = Transformations.map(_park, { it.priceMax })

    var attractionsCollection : MutableLiveData<List<Attraction>> = Transformations.map(_fullpark, { it.attractions})

    val park: LiveData<Park> = _park



    fun save() = viewModelScope.launch {
        val toSave : Park = Park(
            pid=id.value!!,
            name = name.value!!,
            startHour = startHour.value!!,
            endHour = endHour.value!!,
            type = Type.values()[typePosition.value!!],
            place = place.value!!,
            priceMin = priceMin.value!!,
            priceMax = priceMax.value!!,
        )
        parkDao.upsert(toSave)
    }

    fun setId (id : Long)  {
        this.id.value = id
    }

    fun createPark() = viewModelScope.launch {
        val park = Park(0, "","","",Type.NO,"","","")
        val pid = parkDao.upsert(park)
        setId(pid)
    }

    fun addAttractionCollection (attractionId : Long) = viewModelScope.launch {
        parkDao.addAssociation(
            ParkAttractionAssociation(
                id.value!!,
                attractionId
            )
        )
    }

    fun removeAttraction (attractionId : Long) = viewModelScope.launch {
        parkDao.deleteAssociation(
            ParkAttractionAssociation(
                id.value!!,
                attractionId
            )
        )
    }

    companion object {
        private val TAG = ParkViewModel::class.java.simpleName

        fun getTypeIndexFromType(type: Type?): Int {
            return if (type == null) -1 else type.ordinal
        }

        fun getTypeFromTypeIndex(index: Int): String {
            if (index == -1) return ""
            return if (index >= Type.values().size) "" else Type.values()[index].name
        }
    }

}