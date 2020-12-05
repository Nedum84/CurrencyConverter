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
import com.currencyconverter.app.viewmodel.ModelConverter
import com.currencyconverter.app.viewmodel.ModelCurrency

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }


        initViewModel()
        clickListeners()
    }

    private fun initViewModel() {

        val viewModelFactory = ModelCurrency.Factory(application)
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
                modelConvert.convert()
            }
        })

        modelConvert.fromAmount.observe(this, {
            it?.let {
                modelConvert.convert()
            }
        })
    }

    private fun clickListeners() {

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

enum class ActiveCurrency {
    FROM_CURRENCY,
    TO_CURRENCY
}

