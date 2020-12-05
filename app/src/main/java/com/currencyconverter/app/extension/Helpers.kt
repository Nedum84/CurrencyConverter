package com.currencyconverter.app.extension

import android.content.Context
import android.widget.Toast
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

fun Context.toast(message:String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


fun <T: Any> String.toListModel(item:T):List<T>{
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val type = Types.newParameterizedType(MutableList::class.java, item::class.java)
    val adapter: JsonAdapter<List<T>> = moshi.adapter(type)
    val modelList: List<T> = adapter.fromJson(this)!!

    return modelList
}
inline fun <reified T : Any> test(t: T) {
    println(T::class)
}

fun List<String>.replaceWithNewImgPath(htmlString: String, serverResponse:String?):String{
    var content = htmlString

    val eachImg = serverResponse?.split(",,,")
    var idx = 0
    eachImg?.forEach {
        val bothUrl = it.trim().split("===")
        if (bothUrl.size>1){
            val  newUrl = bothUrl[1]
            content = content.replace(this[idx], "\'$newUrl\'")
        }
        idx++
    }

    return content
}
