package com.currencyconverter.app.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager


class ClassUtilities() {



    @SuppressLint("SourceLockedOrientationActivity")
    fun lockScreen(context: Context?){
        //lock screen
        val orientation = context!!.resources.configuration.orientation//activity!!.requestedOrientation(to get 10 more values)
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // code for portrait mode
            try {
                (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } catch (e: Exception) {}
        } else {
            // code for landscape mode
            try {
                (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } catch (e: Exception) {}
        }
        //lock screen
//        OR
//        activity!!.requestedOrientation = orientation
        //OR
//        activity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }
    fun unlockScreen(context: Context?){
        try {
            (context as Activity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        } catch (e: Exception) {}
    }

    fun hideKeyboard(view: View?, activity: Activity?){
        val view = activity?.currentFocus
        try {
            activity?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            if(view != null){
                val inputManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {}
    }

    fun copyToClipBoard(str:String, ctx:Context){
        val clipboard =  ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("text_label", str.trim())
        clipboard.setPrimaryClip(clip)
    }





}