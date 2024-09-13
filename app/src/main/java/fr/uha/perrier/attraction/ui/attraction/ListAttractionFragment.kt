package fr.uha.perrier.attraction.ui.attraction

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
import fr.uha.perrier.attraction.databinding.AttractionItemBinding
import fr.uha.perrier.attraction.databinding.FragmentListAttractionBinding
import fr.uha.perrier.attraction.model.Attraction

@AndroidEntryPoint
class ListAttractionFragment : Fragment() {

    private var _binding: FragmentListAttractionBinding? = null
    private var viewModel : ListAttractionViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter : AttractionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(ListAttractionViewModel::class.java)

        _binding = FragmentListAttractionBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_list_to_attraction)
        }
        binding.list.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(divider)
        val itemTouchHelper = ItemTouchHelper(
            ItemSwipeCallback(requireContext(), ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                object : ItemSwipeCallback.SwipeListener {
                    override fun onSwiped(direction: Int, position: Int) {
                        val attraction: Attraction = adapter.get(position)
                        when (direction) {
                            ItemTouchHelper.LEFT -> onDelete(attraction)
                            ItemTouchHelper.RIGHT -> onEdit(attraction)
                        }
                    }
                }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.list)

        adapter = AttractionAdapter()
        binding.list.adapter = adapter

        viewModel!!.attractions.observe(viewLifecycleOwner) {
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

    private fun onEdit(attraction: Attraction) {
        val action = ListAttractionFragmentDirections.actionNavigationListToAttraction()
        action.id = attraction.aid
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun onDelete(attraction: Attraction) {
        viewModel!!.delete(attraction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class AttractionAdapter : RecyclerView.Adapter<AttractionAdapter.ViewHolder>() {

        private var current : List<Attraction> = listOf()

        inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttractionAdapter.ViewHolder {
            val layout : AttractionItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.attraction_item, parent, false)
            layout.lifecycleOwner = viewLifecycleOwner
            return ViewHolder(layout)
        }

        override fun onBindViewHolder(holder: AttractionAdapter.ViewHolder, position: Int) {
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

}

