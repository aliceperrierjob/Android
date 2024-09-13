package fr.uha.perrier.attraction.ui.person

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.android.livedata.Transformations
import fr.uha.perrier.attraction.database.PersonDao
import fr.uha.perrier.attraction.model.*
import fr.uha.perrier.attraction.model.Type.Type
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor (
    private val personDao : PersonDao,
)
    : ViewModel() {

    private val id = MutableLiveData<Long> ()

    private val _fullperson : LiveData<FullPerson> = Transformations.switchMap(id)
        {
            id -> personDao.getById(id).asLiveData()
        }

    var _person : MutableLiveData<Person> = Transformations.map(_fullperson, { it.person })

    var firstname : MutableLiveData<String> = Transformations.map(_person, { it.firstName })
    var lastname : MutableLiveData<String> = Transformations.map(_person, { it.lastName })
    var age : MutableLiveData<Int> = Transformations.map(_person, { it.age })

    var attractionsCollection : MutableLiveData<List<Attraction>> = Transformations.map(_fullperson, { it.attractions})

    val person: LiveData<Person> = _person



    fun save() = viewModelScope.launch {
        val toSave : Person = Person(
            personid=id.value!!,
            firstName = firstname.value!!,
            lastName = lastname.value!!,
            age = age.value!!,
        )
        personDao.upsert(toSave)
    }

    fun setId (id : Long)  {
        this.id.value = id
    }

    fun createPerson() = viewModelScope.launch {
        val person = Person(0, "","",0)
        val pid = personDao.upsert(person)
        setId(pid)
    }

    fun addAttractionCollection (attractionId : Long) = viewModelScope.launch {
        personDao.addAssociation(
            AttractionPersonAssociation(
                id.value!!,
                attractionId
            )
        )
    }

    fun removeAttraction (attractionId : Long) = viewModelScope.launch {
        personDao.deleteAssociation(
            AttractionPersonAssociation(
                id.value!!,
                attractionId
            )
        )
    }

    companion object {
        private val TAG = PersonViewModel::class.java.simpleName

        fun getTypeIndexFromType(type: Type?): Int {
            return if (type == null) -1 else type.ordinal
        }

        fun getTypeFromTypeIndex(index: Int): String {
            if (index == -1) return ""
            return if (index >= Type.values().size) "" else Type.values()[index].name
        }
    }

}