package com.example.weatherwork.ui.spotter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SpotterViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Spotter Fragment"
    }
    val text: LiveData<String> = _text
}