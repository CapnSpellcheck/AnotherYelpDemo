package julianpellico.paypal

import android.app.Application
import com.nostra13.universalimageloader.core.*

class YelpApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val ilConfig = ImageLoaderConfiguration.Builder(this)
        val defaultDisplayOpts = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .build()
        ilConfig.defaultDisplayImageOptions(defaultDisplayOpts)
        ImageLoader.getInstance().init(ilConfig.build())
    }
}