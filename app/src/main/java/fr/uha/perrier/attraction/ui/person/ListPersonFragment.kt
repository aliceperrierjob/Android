package fr.uha.perrier.attraction.ui.person

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
import fr.uha.perrier.attraction.databinding.FragmentListPersonBinding
import fr.uha.perrier.attraction.databinding.PersonItemBinding
import fr.uha.perrier.attraction.model.PersonWithDetails

@AndroidEntryPoint
class ListPersonFragment : Fragment() {

    private var _binding: FragmentListPersonBinding? = null
    private var viewModel : ListPersonViewModel? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var adapter : PersonAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(ListPersonViewModel::class.java)

        _binding = FragmentListPersonBinding.inflate(inflater, container, false)
        binding.add.setOnClickListener {
            NavHostFragment.findNavController(this).navigate(R.id.action_navigation_list_to_person)
        }
        binding.list.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(divider)
        val itemTouchHelper = ItemTouchHelper(
            ItemSwipeCallback(requireContext(), ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                object : ItemSwipeCallback.SwipeListener {
                    override fun onSwiped(direction: Int, position: Int) {
                        val person: PersonWithDetails = adapter.get(position)
                        when (direction) {
                            ItemTouchHelper.LEFT -> onDelete(person)
                            ItemTouchHelper.RIGHT -> onEdit(person)
                        }
                    }
                }
            )
        )
        itemTouchHelper.attachToRecyclerView(binding.list)

        adapter = PersonAdapter()
        binding.list.adapter = adapter

        viewModel!!.persons.observe(viewLifecycleOwner) {
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

    private fun onEdit(person: PersonWithDetails) {
        val action = ListPersonFragmentDirections.actionNavigationListToPerson()
        action.id = person.person.personid
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun onDelete(person: PersonWithDetails) {
        viewModel!!.delete(person)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class PersonAdapter : RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

        private var current : List<PersonWithDetails> = listOf()

        inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonAdapter.ViewHolder {
            val layout : PersonItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.person_item, parent, false)
            layout.lifecycleOwner = viewLifecycleOwner
            return ViewHolder(layout)
        }

        override fun onBindViewHolder(holder: PersonAdapter.ViewHolder, position: Int) {
            val person : PersonWithDetails = current[position]
            holder.binding.setVariable(BR.person, person)
        }

        override fun getItemCount(): Int {
            return current.size
        }

        fun setCurrent (persons : List<PersonWithDetails>) {
            current = persons
            notifyDataSetChanged()
        }

        fun get(position: Int): PersonWithDetails {
            return current[position]
        }

    }

}

