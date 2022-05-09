package com.example.myapplication.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.R
import com.example.myapplication.assessment.NeighborhoodAssessment
import com.example.myapplication.neighborhood_hub.NeighborhoodHub
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.List.of
import java.util.regex.Pattern



class Search() : Fragment() {

    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context
    private lateinit var blockID: String
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstancIeState: Bundle?): View? {
        var root = inflater.inflate(com.example.myapplication.R.layout.search_layout, container, false)
        var search = root.findViewById<SearchView>(R.id.searchView)
        searchConfig(search)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        db = Firebase.firestore


        return root
    }

    private fun searchConfig(search: SearchView){
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String): Boolean {
                var i = search.query.toString()
                var temp: String = i
                blockID = i
                var b: Boolean = Pattern.matches("\\s?[a-zA-Z\\s]*, \\s?[a-zA-Z\\s]*\\s?", i )


                if(b){
                    db.collection("assessments")
                        .get()
                        .addOnSuccessListener { result ->
                            if (!result.isEmpty) {
                                // Process all assessments for this block
                                for (document in result) {
                                    if (document.getString("block") == blockID) {
                                        Log.i("Tag", "tarr222")
                                        sendToHub()
                                    }
                                }
                            }else{
                                sendToAssessments()
                            }
                        }
                }else{
                    Toast.makeText(mCallback, "Please follow the format \n \"Street Name, City Name\"", Toast.LENGTH_LONG).show()
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {

                return false
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()

    }

    fun sendToAssessments(){
        var bundle = Bundle()
        bundle.putString("BlockID", blockID)
        val fragment = NeighborhoodAssessment()
        fragment.arguments = bundle
        getActivity()?.getSupportFragmentManager()?.beginTransaction()
            ?.replace(R.id.flContent, fragment, "findThisFragment")
            ?.addToBackStack(null)
            ?.commit();
    }

    fun sendToHub(){
        var bundle2 = Bundle()
        bundle2.putString("BlockID", blockID)
        val fragment = NeighborhoodHub()
        fragment.arguments = bundle2
        getActivity()?.getSupportFragmentManager()?.beginTransaction()
            ?.replace(R.id.flContent, fragment, "findThisFragment")
            ?.addToBackStack(null)
            ?.commit();
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

}
