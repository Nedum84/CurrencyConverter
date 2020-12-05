package com.currencyconverter.app.ui.fragment

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.currencyconverter.app.room.DatabaseRoom
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : BottomSheetDialogFragment(), CoroutineScope{
    val thisContext: Activity by lazy{ requireActivity() }
    val application:Application by lazy { requireNotNull(this.activity).application }
    val db: DatabaseRoom by lazy { DatabaseRoom.getDatabaseInstance(application) }

    private lateinit var job: Job


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = Job()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}