package com.currencyconverter.app.ui.activity

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.currencyconverter.app.R
import com.currencyconverter.app.databinding.ActivityMainBinding
import com.currencyconverter.app.ui.fragment.bottomsheet.FragmentSelectCurrency
import com.currencyconverter.app.utils.ClassAlertDialog
import com.currencyconverter.app.utils.ClassUtilities
import com.currencyconverter.app.viewmodel.ModelConverter
import com.currencyconverter.app.viewmodel.ModelCurrency
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    val binding:ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }

    private lateinit var modelCurrency: ModelCurrency
    private val modelConvert: ModelConverter by lazy {
        ViewModelProvider(
            this,
            ModelConverter.Factory(application)
        ).get(ModelConverter::class.java)
    }
    var activeCurrency = ActiveCurrency.FROM_CURRENCY


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.apply {
            lifecycleOwner = this@MainActivity
            modelConverter = modelConvert
        }

        //CHANGE STATUS BAR TEXT COLOR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        //LOCK SCREEN TO ONLY PORTRAIT
        ClassUtilities().lockScreen(this);


        initViewModel()
        clickListeners()
    }

    private fun initViewModel() {

//        CURRENCY VIEWMODEL INIT
        val viewModelFactory = ModelCurrency.Factory(application, this)
        modelCurrency = ViewModelProvider(this, viewModelFactory).get(ModelCurrency::class.java)
        modelCurrency.curCurrency.observe(this, {
            it?.getContentIfNotHandled().let { c ->
                when (activeCurrency) {
                    ActiveCurrency.FROM_CURRENCY -> {
                        modelConvert.setFromCurrency(c!!)
                    }
                    ActiveCurrency.TO_CURRENCY -> {
                        modelConvert.setToCurrency(c!!)
                    }
                }
                //Checking if user has already entered a value. Make it unity if user hasn't added something
                val fromAmount = binding.fromCurrencyInput.text.toString().trim()
                if(fromAmount.isEmpty())
                    modelConvert.setFromAmount("1")
                else
                    convert()
            }
        })

        //OBSERVE CHANGES IN THE "FROM" INPUT FIELDS FOR CONVERSION
        modelConvert.fromAmount.observe(this, {
            it?.let {
                convert()
            }
        })
    }

    private fun convert(){
        CoroutineScope(Dispatchers.Main).launch {
            modelConvert.convert()
        }
    }

    private fun clickListeners() {

        //SETTING THE CURRENT CLICK CURRENCY CONVERSION BUTTON 
        binding.fromCurrencyTextWrapper.setOnClickListener{
            activeCurrency = ActiveCurrency.FROM_CURRENCY
            FragmentSelectCurrency().apply {
                show(supportFragmentManager, tag)
            }
        }
        binding.toCurrencyTextWrapper.setOnClickListener{
            activeCurrency = ActiveCurrency.TO_CURRENCY
            FragmentSelectCurrency().apply {
                show(supportFragmentManager, tag)
            }
        }
        binding.convertBtn.setOnClickListener {
            convertCurrency()
        }

        //CONVERT CURRENCY FOR EVERY TEXT ENTERED BY THE USER
        binding.fromCurrencyInput.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.trim().isNotEmpty())
                    modelConvert.setFromAmount(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun convertCurrency() {
        val fromAmount = binding.fromCurrencyInput.text.toString().trim();
        if (fromAmount.isEmpty()){
            modelConvert.setFromAmount("1")
        }else{
            modelConvert.setFromAmount(fromAmount)
        }
    }

}
//CHECK THE CURRENT CURRENCY TO LISTEN TO
enum class ActiveCurrency {
    FROM_CURRENCY,
    TO_CURRENCY
}

