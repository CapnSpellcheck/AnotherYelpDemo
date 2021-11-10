package julianpellico.paypal

import android.widget.CompoundButton
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.chip.ChipGroup
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader

@BindingAdapter("app:srcUri")
fun bindSrcURI(iv: ImageView, src: String?) {
    val opts = DisplayImageOptions.Builder().resetViewBeforeLoading(true)
        .build()
    ImageLoader.getInstance().displayImage(src, iv, opts)
}

@BindingAdapter("android:onCheckedChanged")
fun bindOnCheckedChanged(chipGroup: ChipGroup, listener: ChipGroup.OnCheckedChangeListener) {
    chipGroup.setOnCheckedChangeListener(listener)
}