package julianpellico.paypal

import androidx.room.*

@Database(entities = [BusinessResult::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessResultDAO(): BusinessResultDAO
}

@Dao
interface BusinessResultDAO {
    @Query("SELECT * FROM BusinessResult WHERE queryTerm = :term")
    fun getForLocation(term: String): List<BusinessResult>

    @Insert
    fun insertAll(results: List<BusinessResult>)
}