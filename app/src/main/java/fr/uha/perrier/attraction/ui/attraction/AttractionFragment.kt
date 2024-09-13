package fr.uha.perrier.attraction.ui.attraction

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import fr.uha.perrier.android.helper.Picture
import fr.uha.perrier.attraction.R
import fr.uha.perrier.attraction.databinding.FragmentAttractionBinding

@AndroidEntryPoint
class AttractionFragment : Fragment() {

    private var _binding: FragmentAttractionBinding? = null
    private var attractionViewModel : AttractionViewModel? = null
    private var picture : Picture? = null

    private var captureFullPhoto : ActivityResultLauncher<Uri> = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) {
        result ->
            if (result) {
                if (picture == null) picture = Picture()
                val picture : String? = picture!!.getPhotoPath()
                attractionViewModel!!.setPicture(picture)
            }
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        attractionViewModel =
            ViewModelProvider(this).get(AttractionViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_attraction, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = attractionViewModel
        binding.takePicture = View.OnClickListener {
            if (picture == null) picture = Picture()
            val uri : Uri? = picture!!.createPhotoFile(requireContext())
            captureFullPhoto.launch(uri)
        }
        val menuHost : MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.menu_save -> return onSave()
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = AttractionFragmentArgs.fromBundle(requireArguments()).id
        if (id == 0L) {
            attractionViewModel!!.createAttraction()
        } else {
            attractionViewModel!!.setId(id)
        }
    }

    fun onSave () : Boolean {
        attractionViewModel?.save ()
        NavHostFragment.findNavController(this).popBackStack()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}