package fr.uha.perrier.attraction.ui.attraction

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.android.livedata.Transformations
import fr.uha.perrier.attraction.database.AttractionDao
import fr.uha.perrier.attraction.model.Category
import fr.uha.perrier.attraction.model.Attraction
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttractionViewModel @Inject constructor (
    private val dao : AttractionDao
)
    : ViewModel() {

    private val id = MutableLiveData<Long> ()

    private val _attraction : LiveData<Attraction> = Transformations.switchMap(id)
        {
            id -> dao.getById(id).asLiveData()
        }

    var name : MutableLiveData<String> = Transformations.map(_attraction, { it.name })
    var attractionpark : MutableLiveData<String> = Transformations.map(_attraction, { it.attractionpark})
    var place : MutableLiveData<String> = Transformations.map(_attraction, { it.place })
    val categoryPosition: MutableLiveData<Int> = Transformations.map(_attraction) { t -> AttractionViewModel.getCategoryIndexFromCategory(t.category) }
    var sizeMin : MutableLiveData<String> = Transformations.map(_attraction, { it.sizeMin })
    var picture : MutableLiveData<String> = Transformations.map(_attraction, { it.picture })

    val attraction: LiveData<Attraction> = _attraction



    fun setPicture (picture : String?) {
        this.picture.value = picture
    }

    fun save() = viewModelScope.launch {
        val toSave : Attraction = Attraction(
            aid=id.value!!,
            name = name.value!!,
            attractionpark = attractionpark.value!!,
            place = place.value!!,
            category =Category.values()[categoryPosition.value!!],
            sizeMin= sizeMin.value!!,
            picture = picture.value
        )
        dao.update(toSave)
    }

    fun setId (id : Long)  {
        this.id.value = id
    }

    fun createAttraction() = viewModelScope.launch {
        val attraction = Attraction(0, "", "", "", Category.NO,"")
        val pid = dao.create(attraction)
        setId(pid)
    }

    companion object {
        private val TAG = AttractionViewModel::class.java.simpleName


        fun getCategoryIndexFromCategory(category: Category?): Int {
            return if (category == null) -1 else category.ordinal
        }

        fun getCategoryFromCategoryIndex(index: Int): String {
            if (index == -1) return ""
            return if (index >= Category.values().size) "" else Category.values()[index].name
        }
    }

}