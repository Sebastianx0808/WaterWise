package com.example.waterwise.ui.tips

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ConservationViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply{
        value = "This is a conservation fragment"
    }
    val text: LiveData<String> = _text
}