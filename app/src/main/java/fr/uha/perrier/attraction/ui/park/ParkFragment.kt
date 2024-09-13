package fr.uha.perrier.attraction.ui.park

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fr.uha.perrier.android.helper.Picture
import fr.uha.perrier.attraction.BR
import fr.uha.perrier.attraction.R
import fr.uha.perrier.attraction.databinding.FragmentParkBinding
import fr.uha.perrier.attraction.databinding.AttractionCollectionItemBinding
import fr.uha.perrier.attraction.model.Attraction
import fr.uha.perrier.attraction.ui.PickerAttractionFragment

@AndroidEntryPoint
class ParkFragment : Fragment() {

    private var _binding: FragmentParkBinding? = null
    private var parkViewModel : ParkViewModel? = null
    private var picture : Picture? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter : AttractionCollectionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        parentFragmentManager.setFragmentResultListener(ATTRACTIONCOLLECTION, this, FragmentResultListener {
                requestKey, result ->
            val attractionCollectionId : Long = result.getLong(PickerAttractionFragment.ATTRACTION)
            parkViewModel!!.addAttractionCollection(attractionCollectionId)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        parkViewModel =
            ViewModelProvider(this).get(ParkViewModel::class.java)

        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_park, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        binding.setAddAttractionCollection {
            var action = ParkFragmentDirections.actionNavigationParkToAttractionPickerFragment(ATTRACTIONCOLLECTION)
            NavHostFragment.findNavController(this).navigate(action)
        }

        binding.list.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(divider)

        adapter = AttractionCollectionAdapter()
        binding.list.adapter = adapter

        binding.vm = parkViewModel
        val menuHost : MenuHost = requireActivity()

        parkViewModel!!.attractionsCollection.observe(viewLifecycleOwner) {
            adapter.setCurrent(it)
        }

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

        val id = ParkFragmentArgs.fromBundle(requireArguments()).id
        if (id == 0L) {
            parkViewModel!!.createPark()
        } else {
            parkViewModel!!.setId(id)
        }
    }

    inner class AttractionCollectionAdapter : RecyclerView.Adapter<AttractionCollectionAdapter.ViewHolder>() {

        private var current : List<Attraction> = listOf()

        inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root), View.OnLongClickListener {

            override fun onLongClick(p0: View?): Boolean {
                val attraction : Attraction = current[adapterPosition]
                onRemove(attraction)
                return true
            }

            init {
                binding.root.setOnLongClickListener(this)
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionCollectionAdapter.ViewHolder {
            val layout : AttractionCollectionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.attraction_collection_item, parent, false)
            layout.lifecycleOwner = viewLifecycleOwner
            return ViewHolder(layout)
        }

        override fun onBindViewHolder(holder: AttractionCollectionAdapter.ViewHolder, position: Int) {
            val attraction : Attraction = current[position]
            holder.binding.setVariable(BR.attraction, attraction)
        }

        override fun getItemCount(): Int {
            return current.size
        }

        fun setCurrent (attractions : List<Attraction>) {
            current = attractions
            notifyDataSetChanged()
        }

        fun get(position: Int): Attraction {
            return current[position]
        }

    }

    private fun onRemove(attraction: Attraction) {
        parkViewModel!!.removeAttraction(attraction.aid)
    }

    fun onSave () : Boolean {
        parkViewModel?.save ()
        NavHostFragment.findNavController(this).popBackStack()
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val ATTRACTIONCOLLECTION: String = "ATTRACTIONCOLLECTION"
    }

}