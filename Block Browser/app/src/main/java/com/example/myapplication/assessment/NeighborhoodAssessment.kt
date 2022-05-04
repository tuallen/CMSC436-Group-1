package com.example.myapplication.assessment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class NeighborhoodAssessment : Fragment() {
    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(com.example.myapplication.R.layout.assess_layout, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

}

