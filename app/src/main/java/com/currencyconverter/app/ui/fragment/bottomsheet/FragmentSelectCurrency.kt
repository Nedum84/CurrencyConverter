package com.currencyconverter.app.ui.fragment.bottomsheet

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.currencyconverter.app.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.currencyconverter.app.adapter.AdapterCurrency
import com.currencyconverter.app.adapter.SymbolClickListener
import com.currencyconverter.app.databinding.FragmentSelectCurrencyBinding
import com.currencyconverter.app.viewmodel.ModelCurrency
import com.currencyconverter.app.ui.fragment.BaseFragment

class FragmentSelectCurrency : BaseFragment() {
    lateinit var binding:FragmentSelectCurrencyBinding
    lateinit var modelCurrency: ModelCurrency
    lateinit var ADAPTER: AdapterCurrency


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val viewModelFactory = ModelCurrency.Factory(requireActivity().application)
        modelCurrency = requireActivity().run{
            ViewModelProvider(this, viewModelFactory).get(ModelCurrency::class.java)
        }

        modelCurrency.queryString.observe(viewLifecycleOwner, { query->
            query?.getContentIfNotHandled()?.let {
                modelCurrency.currency(it).observe(viewLifecycleOwner, Observer {
                    it?.let {
                        ADAPTER.list = it
                        checkEmpty()
                    }
                })
            }
        })
        searchCourses()
    }

    override fun onStart() {
        super.onStart()
        modelCurrency.setSearchQuery("")
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_currency, container, false)

        val viewModelFactory = ModelCurrency.Factory(requireActivity().application)
        modelCurrency = ViewModelProvider(this, viewModelFactory).get(ModelCurrency::class.java)
        binding.apply {
            lifecycleOwner = this@FragmentSelectCurrency
        }

        ADAPTER = AdapterCurrency(SymbolClickListener {
            modelCurrency.setCurSymbol(it)
            dialog?.dismiss()
        })

        binding.recyclerCurrency.apply {
            adapter = ADAPTER
            layoutManager= LinearLayoutManager(activity)
            itemAnimator = DefaultItemAnimator()
        }

        modelCurrency.feedBack.observe(viewLifecycleOwner, Observer {
            checkEmpty(true)
        })


        binding.closeDialogBtn.setOnClickListener {
            dialog?.dismiss()
        }

        //Refresh Currency list
        binding.refreshCurrency.setOnClickListener {
            modelCurrency.refreshCurrency()
        }

        return binding.root
    }
    private fun searchCourses(searchView: SearchView = binding.searchCurrency){
        searchView.queryHint = "Search currency or country..."
        searchView.setIconifiedByDefault(false)


        val searchText = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as TextView
        searchText.setTextColor(Color.BLACK)
        searchText.setHintTextColor(Color.BLACK)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                modelCurrency.setSearchQuery(query!!)
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                modelCurrency.setSearchQuery(query)
                return true
            }
        })
    }

    private fun checkEmpty(is_network_error:Boolean = false){
        if (is_network_error){
            binding.noCurrencyFound.visibility = if (ADAPTER.itemCount==0) View.VISIBLE else View.GONE
            binding.refreshTitle.text = "No internet connection"
        }else{
            binding.refreshTitle.text = "No currency found"
            binding.noCurrencyFound.visibility = if (ADAPTER.itemCount==0) View.VISIBLE else View.GONE
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            behavior.peekHeight = PEEK_HEIGHT_AUTO
//            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels
            behavior.peekHeight = Resources.getSystem().displayMetrics.heightPixels-200
        }
    }

}
