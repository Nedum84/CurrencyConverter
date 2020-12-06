package com.currencyconverter.app.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.currencyconverter.app.R
import java.util.*

object ClassImageBinding {

    //For Image view Adapter
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: String?  = ""){
        if (!imgUrlString.isNullOrEmpty()){
            try {
                Glide.with(view.context)
    //                .load(imgUrlString)
                    .load("https://flagcdn.com/h60/${imgUrlString.toLowerCase(Locale.getDefault())}.png")
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.ic_launcher_background)//default image on loading
                            .error(R.drawable.ic_launcher_background)//without n/w, this img shows
                            .dontAnimate()
                            .centerCrop()
                    )
                    .thumbnail(.1f)
                    .into(view)
            } catch (e: Exception) {
                e.printStackTrace()
                Glide.with(view.context)
                    .load(R.drawable.ic_launcher_background)
                    .into(view)
            }
        }
    }

    //For Int Images...
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: Int){
        Glide.with(view.context)
            .load(imgUrlString)
            .into(view)
    }

}