package julianpellico.paypal

import com.android.volley.*
import com.android.volley.toolbox.*
import com.beust.klaxon.*


object YelpAPI {

    private val requestQueue: RequestQueue = run {
        val network = BasicNetwork(HurlStack())
        val queue = RequestQueue(NoCache(), network).apply {
            start()
        }
        queue
    }

    fun businessSearch(location: String = "New York City",
                       listener: ResponseHandler<BusinessSearchResponse>) {
        val request = AuthenticatedJSONRequest(
            Request.Method.GET,
            "https://api.yelp.com/v3/businesses/search?location=$location&limit=$MAX_PER_PAGE",
            object : ResponseHandler<JsonObject> {
                override fun onResponse(response: JsonObject) {
                    val count = response.long("total") ?: 0
                    val businessArray = response.array<JsonObject>("businesses")
                    val businessResults = businessArray?.map { BusinessResult(it, location) } ?: emptyList()
                    val parsedResult = BusinessSearchResponse(count, businessResults)
                    listener.onResponse(parsedResult)
                }
                override fun onErrorResponse(error: VolleyError?) {
                    listener.onErrorResponse(error)
                }
            })

        requestQueue.add(request)
    }

    const val MAX_PER_PAGE = 50
}

class AuthenticatedJSONRequest(method: Int, url: String, listener: ResponseHandler<JsonObject>)
    : KlaxonRequest(method, url, listener)
{
    override fun getHeaders(): MutableMap<String, String> {
        return mutableMapOf("Authorization" to "Bearer OKT42_lgUc9PdxhmEu0K13WudgCzRf4FP_3ZARbJQEkwc4pS3ZV9h1BgJXw-UE8s5e5uGSod53_0ZoD8GAacWJGk1yiim85ZqtjxheoKvRMUMtOfiiRPmP5jaDPKXHYx")
    }
}