package com.example.weatherwork.ui.cst

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CSTViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is CST Fragment"
    }
    val text: LiveData<String> = _text
}