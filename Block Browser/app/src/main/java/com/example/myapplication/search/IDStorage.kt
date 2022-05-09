package com.example.myapplication.search

import androidx.lifecycle.ViewModel

class IDStorage: ViewModel() {
    private lateinit var array: ArrayList<String>

    fun addID(ID: String){
        array.add(ID)
    }

    fun findID(ID: String): Boolean {
        for(items in array){
            if(items.toString() == ID){
                return true
            }
        }
        return false
    }
}