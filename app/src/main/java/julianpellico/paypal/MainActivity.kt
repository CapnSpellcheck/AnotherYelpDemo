package julianpellico.paypal

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.BoolRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.*
import com.android.volley.Response
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import julianpellico.paypal.databinding.ActivityMainBinding
import julianpellico.paypal.databinding.RvBusinessResultBinding
import java.util.*

private val tag = "SearchResultActivity"

class MainActivity : AppCompatActivity() {
//    lateinit var recyclerView: RecyclerView
    lateinit var binding: ActivityMainBinding
    val viewModel: ResultsViewModel by viewModels()

    val adapter: ListAdapter<PositionBusinessResult, DataBindingViewHolder>?
        get() = binding.recycler.adapter as ListAdapter<PositionBusinessResult, DataBindingViewHolder>?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.listeners = viewModel
//        recyclerView = findViewById(R.id.recycler)
        lifecycle.addObserver(viewModel)

        viewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
        }
        viewModel.resultsLiveData.observe(this) { resultList ->
            if (adapter == null) {
                binding.recycler.adapter = BusinessResultAdapter()
            }
            adapter!!.submitList(resultList)
        }

    }

}

class DataBindingViewHolder(val binding: ViewDataBinding)
    : RecyclerView.ViewHolder(binding.root)

class BusinessResultAdapter
    : ListAdapter<PositionBusinessResult, DataBindingViewHolder>(ResultsViewModel.diffCallback)
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataBindingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RvBusinessResultBinding.inflate(inflater, parent, false)
        val holder = DataBindingViewHolder(binding)
        return holder
    }

    override fun onBindViewHolder(holder: DataBindingViewHolder, position: Int) {
        // not called
    }

    override fun onBindViewHolder(
        holder: DataBindingViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val binding = (holder.binding as RvBusinessResultBinding)
        val positionBizResult = getItem(position)
        binding.position = positionBizResult.position
        if (payloads.isEmpty()) {
            binding.business = positionBizResult.business
        }
    }
}
