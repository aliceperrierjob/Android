package fr.uha.perrier.attraction.ui.park

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fr.uha.perrier.android.ui.ItemSwipeCallback
import fr.uha.perrier.attraction.R
import fr.uha.perrier.attraction.BR
import fr.uha.perrier.attraction.database.DatabaseFeed
import fr.uha.perrier.attraction.databinding.FragmentListParkBinding
import fr.uha.perrier.attraction.databinding.ParkItemBinding
import fr.uha.perrier.attraction.model.ParkWithDetails

@AndroidEntryPoint
class ListParkFragment : Fragment() {

    private var _binding: FragmentListParkBinding? = null
    private var viewModel : ListParkViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter : ParkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(ListParkViewModel::class.java)

        _binding = FragmentListParkBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_list_to_park)
        }
        binding.list.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(divider)
        val itemTouchHelper = ItemTouchHelper(
            ItemSwipeCallback(requireContext(), ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                object : ItemSwipeCallback.SwipeListener {
                    override fun onSwiped(direction: Int, position: Int) {
                        val park: ParkWithDetails = adapter.get(position)
                        when (direction) {
                            ItemTouchHelper.LEFT -> onDelete(park)
                            ItemTouchHelper.RIGHT -> onEdit(park)
                        }
                    }
                }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.list)

        adapter = ParkAdapter()
        binding.list.adapter = adapter

        viewModel!!.parks.observe(viewLifecycleOwner) {
            adapter.setCurrent(it)
        }
        val menuHost : MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.populate_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                   R.id.menu_populate -> DatabaseFeed().feed()
                }
                return false
            }

        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
        return binding.root
    }

    private fun onEdit(park: ParkWithDetails) {
        val action = ListParkFragmentDirections.actionNavigationListToPark()
        action.id = park.park.pid
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun onDelete(park: ParkWithDetails) {
        viewModel!!.delete(park)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class ParkAdapter : RecyclerView.Adapter<ParkAdapter.ViewHolder>() {

        private var current : List<ParkWithDetails> = listOf()

        inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkAdapter.ViewHolder {
            val layout : ParkItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.park_item, parent, false)
            layout.lifecycleOwner = viewLifecycleOwner
            return ViewHolder(layout)
        }

        override fun onBindViewHolder(holder: ParkAdapter.ViewHolder, position: Int) {
            val park : ParkWithDetails = current[position]
            holder.binding.setVariable(BR.park, park)
        }

        override fun getItemCount(): Int {
            return current.size
        }

        fun setCurrent (parks : List<ParkWithDetails>) {
            current = parks
            notifyDataSetChanged()
        }

        fun get(position: Int): ParkWithDetails {
            return current[position]
        }

    }

}

