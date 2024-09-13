package fr.uha.perrier.attraction.ui

import android.graphics.Point
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import fr.uha.perrier.attraction.BR
import fr.uha.perrier.attraction.R
import fr.uha.perrier.attraction.databinding.AttractionItemBinding
import fr.uha.perrier.attraction.databinding.PickerAttractionBinding
import fr.uha.perrier.attraction.model.Attraction

@AndroidEntryPoint
class PickerAttractionFragment : DialogFragment() {

    private lateinit var requestKey: String
    private var initial: Long = 0

    private var _binding: PickerAttractionBinding? = null
    private var viewModel : PickerAttractionViewModel? = null

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
            ViewModelProvider(this).get(PickerAttractionViewModel::class.java)

        _binding = PickerAttractionBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(binding.root.context, RecyclerView.VERTICAL, false)
        val divider = DividerItemDecoration(binding.list.context, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(divider)

        adapter = AttractionAdapter()
        binding.list.adapter = adapter

        viewModel!!.attractions.observe(viewLifecycleOwner) {
            adapter.setCurrent(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val arg = PickerAttractionFragmentArgs.fromBundle(requireArguments())
        requestKey = arg.requestKey
    }

    private fun onSelect(attraction: Attraction) {
        val result = Bundle()
        result.putLong(ATTRACTION, attraction.aid)
        parentFragmentManager.setFragmentResult(requestKey, result)
        dismiss()
    }

    private fun tryResize () {
        if (dialog == null) return
        val window = dialog?.getWindow()
        if (window == null) return
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics : WindowMetrics = window.getWindowManager().getCurrentWindowMetrics()
            val rect : Rect = metrics.bounds
            point.x = rect.right - rect.left
            point.y = rect.bottom - rect.top
        } else {
            val display = window.getWindowManager().defaultDisplay
            display.getSize(point)
        }
        window.setLayout((point.x * 0.75).toInt(), (point.y * 0.75).toInt())
        window.setGravity(Gravity.CENTER)
    }

    override fun onResume() {
        tryResize()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    inner class AttractionAdapter : RecyclerView.Adapter<AttractionAdapter.ViewHolder>() {

        private var current : List<Attraction> = listOf()

        inner class ViewHolder(val binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

            override fun onClick(p0: View?) {
                val attraction : Attraction = current[adapterPosition]
                onSelect(attraction)
            }

            init {
                binding.root.setOnClickListener(this)
            }
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

    companion object {
        const val ATTRACTION: String = "attraction"
    }
}
