package fr.uha.perrier.attraction.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.uha.perrier.attraction.database.AttractionDao
import fr.uha.perrier.attraction.model.Attraction
import javax.inject.Inject

@HiltViewModel
class PickerAttractionViewModel @Inject constructor (
    private val dao : AttractionDao
) : ViewModel() {

    val attractions : LiveData<List<Attraction>> = dao.getAll().asLiveData()

    companion object {
        private val TAG = PickerAttractionViewModel::class.java.simpleName
    }

}