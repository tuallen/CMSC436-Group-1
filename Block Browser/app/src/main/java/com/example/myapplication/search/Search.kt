package com.example.myapplication.search

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.SearchActivity
import com.example.myapplication.neighborhood_hub.NeighborhoodHub
import java.util.regex.Pattern

class Search() : Fragment() {
    private lateinit var root : View
    private lateinit var mPrefs : SharedPreferences
    private lateinit var mNeighborhoodEditText : EditText
    private lateinit var mCityEditText : EditText
    private lateinit var mHubButton : Button

    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        root = inflater.inflate(com.example.myapplication.R.layout.search_layout, container, false)
        // Initialize views
        mNeighborhoodEditText = root.findViewById<View>(R.id.searchNeighborhoodEditText) as EditText
        mCityEditText = root.findViewById<View>(R.id.searchCityEditText) as EditText
        mHubButton = root.findViewById<View>(R.id.searchButton) as Button

        mPrefs = mCallback.getSharedPreferences("block", Context.MODE_PRIVATE) // initialize saved preferences
        // Load neighborhood and city
        var neighborhood = mPrefs.getString("neighborhood", null)
        var city = mPrefs.getString("city", null)
        if (neighborhood != null && city != null) {
            mNeighborhoodEditText.setText(neighborhood)
            mCityEditText.setText(city)
        }

        mHubButton.setOnClickListener{
            // Change neighborhood and city
            neighborhood = capitalize(mNeighborhoodEditText.text.toString()).replace("\\s+".toRegex(), " ").trim()
            city = capitalize(mCityEditText.text.toString()).replace("\\s+".toRegex(), " ").trim()
            if (neighborhood!!.replace("\\s".toRegex(), "") != "" && city!!.replace("\\s".toRegex(), "") != "") {
                var editor = mPrefs.edit()
                editor.putString("neighborhood", neighborhood)
                editor.putString("city", city)
                editor.apply()
                activity?.supportFragmentManager!!.beginTransaction().replace(R.id.flContent, NeighborhoodHub()).commit()
                Toast.makeText(mCallback, "Switched neighborhoods!", Toast.LENGTH_LONG).show()
            }
            else{
                Toast.makeText(mCallback, NOT_COMPLETE, Toast.LENGTH_LONG).show()
            }
        }
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    private fun capitalize(str: String?): String {
        val sb = StringBuffer()
        val matcher = Pattern.compile("\\b(\\w)").matcher(str)
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase())
        }
        matcher.appendTail(sb)
        return sb.toString()
    }

    companion object {
        private const val TAG = "Search-Fragment"
        private const val NOT_COMPLETE = "Enter a neighborhood and city!"
    }

}