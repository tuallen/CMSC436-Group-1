package com.example.blockassessment.ui.survey

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blockassessment.R

class Survey : Fragment() {

    companion object {
        fun newInstance() = Survey()
    }

    private lateinit var viewModel: SurveyViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.survey_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SurveyViewModel::class.java)
        // TODO: Use the ViewModel
    }

}