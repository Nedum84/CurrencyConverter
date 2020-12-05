package com.currencyconverter.app.bindingadapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.currencyconverter.app.R

object ClassImageBinding {

    //For Image view Adapter
    @JvmStatic // add this line !!
    @BindingAdapter("imagePath")
    fun bindImageFromUrl(view: ImageView, imgUrlString: String?  = ""){
        if (!imgUrlString.isNullOrEmpty()){
            Glide.with(view.context)
                .load(imgUrlString)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_dot)//default image on loading
                        .error(R.drawable.ic_dot)//without n/w, this img shows
//                        .dontAnimate()
//                        .fitCenter()
                )
                .thumbnail(.1f)
                .into(view)
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