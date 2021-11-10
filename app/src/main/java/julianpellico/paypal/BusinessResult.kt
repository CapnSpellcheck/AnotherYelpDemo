package julianpellico.paypal

import androidx.annotation.NonNull
import androidx.room.*
import com.beust.klaxon.*
import kotlin.math.roundToInt

@Entity
data class BusinessResult(
    val name: String,
    val imageURL: String,
    val address1: String?,
    val address2: String?,
    val locality: String?,
    @ColumnInfo(index = true) val rating: Float?,
    @ColumnInfo(index = true) val reviewCount: Int?,
    @ColumnInfo(index = true) val price: String?,
    val distance: Float?, // meters
    val openNow: Boolean = true,
    @NonNull @PrimaryKey val yelpID: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE, index = true) val queryTerm: String,
) {
    constructor(obj: JsonObject, query: String) : this(obj.string("name")!!,
             obj.string("image_url")!!,
             obj.obj("location")?.string("address1"),
             obj.obj("location")?.string("address2"),
             obj.obj("location")?.string("city"),
             obj.double("rating")?.toFloat(),
             obj.int("review_count"),
             obj.string("price") ?: "¢",
             obj.double("distance")?.toFloat(),
             yelpID = obj.string("id")!!,
             queryTerm = query,
        )

    val address: String
        get()  = listOf(address1, address2, locality).filter { it != null && it != "" }
            .joinToString()

    val ratingAndCount: String
        get() = "$rating ($reviewCount)"

    val km: String?
        get() {
            return distance?.let {
                val km = (it / 1000).roundToInt()
                return "$km km"
            }
        }

    fun numberedName(number: Int): String = "${number}. $name"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BusinessResult

        if (name != other.name) return false
        if (imageURL != other.imageURL) return false
        if (address1 != other.address1) return false
        if (address2 != other.address2) return false
        if (locality != other.locality) return false
        if (rating != other.rating) return false
        if (reviewCount != other.reviewCount) return false
        if (price != other.price) return false
        if (distance != other.distance) return false
        if (openNow != other.openNow) return false
        if (yelpID != other.yelpID) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + imageURL.hashCode()
        result = 31 * result + (address1?.hashCode() ?: 0)
        result = 31 * result + (address2?.hashCode() ?: 0)
        result = 31 * result + (locality?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (reviewCount ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        result = 31 * result + (distance?.hashCode() ?: 0)
        result = 31 * result + openNow.hashCode()
        result = 31 * result + (yelpID?.hashCode() ?: 0)
        return result
    }
}

data class BusinessSearchResponse(val totalCount: Long, val results: List<BusinessResult>)
data class PositionBusinessResult(val business: BusinessResult, val position: Int)
data class PositionChangePayload(val position: Int)