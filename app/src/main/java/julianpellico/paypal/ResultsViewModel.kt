package julianpellico.paypal

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.IdRes
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import com.android.volley.VolleyError
import java.util.*

private val tag_ = "ResultsViewModel"

enum class ResultSortMode {
    BestMatch, Rating, ReviewCount, Distance
}
enum class ResultFilter {
    OpenNow, Cost1, Cost2, Cost3, Cost4
}

interface ResultsListeners {
    fun onSortChanged(@IdRes checkedID: Int)
    fun onFilterChanged(mode: ResultFilter, checked: Boolean)
}

class ResultsViewModel : ViewModel(), ResponseHandler<BusinessSearchResponse>, ResultsListeners, LifecycleObserver {
    private val errorLD = MutableLiveData<Exception>()
    private val resultsLD = MutableLiveData<List<PositionBusinessResult>>()
    private lateinit var response: BusinessSearchResponse
    private var sortMode: ResultSortMode = ResultSortMode.BestMatch
    private var filterMode: EnumSet<ResultFilter> = EnumSet.noneOf(ResultFilter::class.java)
//    private var currentView: List<BusinessResult> = emptyList()

    val errorLiveData: LiveData<Exception>
        get() = errorLD
    val resultsLiveData: LiveData<List<PositionBusinessResult>>
    get() = resultsLD

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun loadInitial() {
        YelpAPI.businessSearch(listener = this)
    }

    private fun addFilter(filter: ResultFilter) {
        filterMode.add(filter)
        renderResults()
    }

    private fun removeFilter(filter: ResultFilter) {
        filterMode.remove(filter)
        renderResults()
    }

    private fun renderResults() {
        var currentView = when (sortMode) {
            ResultSortMode.BestMatch ->
                response.results
            ResultSortMode.Rating ->
                response.results.sortedByDescending { it.rating }
            ResultSortMode.ReviewCount ->
                response.results.sortedByDescending { it.reviewCount }
            ResultSortMode.Distance ->
                response.results.sortedBy { it.distance }
        }
        if (!filterMode.isEmpty()) {
            currentView = currentView.filter { business ->
                if (filterMode.contains(ResultFilter.OpenNow) && !business.openNow)
                    return@filter false
                if (!filterMode.contains(ResultFilter.Cost1) && business.price == "$")
                    return@filter false
                if (!filterMode.contains(ResultFilter.Cost2) && business.price == "$$")
                    return@filter false
                if (!filterMode.contains(ResultFilter.Cost3) && business.price == "$$$")
                    return@filter false
                if (!filterMode.contains(ResultFilter.Cost4) && business.price == "$$$$")
                    return@filter false

                return@filter true
            }
        }

        resultsLD.postValue(currentView.mapIndexed { index, businessResult ->
            PositionBusinessResult(businessResult, index)
        })
    }

    override fun onResponse(response: BusinessSearchResponse) {
        this.response = response
        Log.i(tag_, "Got response: $response")
        renderResults()
    }

    override fun onErrorResponse(error: VolleyError) {
        Log.d(tag_, "Got error: $error")
        errorLD.value = error
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<PositionBusinessResult>() {
            override fun areItemsTheSame(
                oldItem: PositionBusinessResult,
                newItem: PositionBusinessResult
            ): Boolean {
                return oldItem.business.yelpID == newItem.business.yelpID
            }

            override fun areContentsTheSame(
                oldItem: PositionBusinessResult,
                newItem: PositionBusinessResult
            ): Boolean = oldItem == newItem

            override fun getChangePayload(
                oldItem: PositionBusinessResult,
                newItem: PositionBusinessResult
            ): Any? {
                return PositionChangePayload(newItem.position)
            }
        }
    }

    override fun onSortChanged(checkedID: Int) {
        sortMode = when (checkedID) {
            R.id.sortDefault -> ResultSortMode.BestMatch
            R.id.sortDistance -> ResultSortMode.Distance
            R.id.sortRating -> ResultSortMode.Rating
            R.id.sortMostReviewed -> ResultSortMode.ReviewCount
            else -> throw IllegalStateException()
        }
        renderResults()
    }

    override fun onFilterChanged(mode: ResultFilter, checked: Boolean) {
        if (checked)
            addFilter(mode)
        else
            removeFilter(mode)
    }
}