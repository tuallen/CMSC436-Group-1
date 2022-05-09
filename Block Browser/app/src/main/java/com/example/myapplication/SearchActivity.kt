package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.assessment.NeighborhoodAssessment
import java.util.regex.Pattern

class SearchActivity : AppCompatActivity() {
    private lateinit var mPrefs : SharedPreferences
    private lateinit var mNeighborhoodEditText : EditText
    private lateinit var mCityEditText : EditText
    private lateinit var mHubButton : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize views
        mNeighborhoodEditText = findViewById<View>(R.id.searchNeighborhoodEditText) as EditText
        mCityEditText = findViewById<View>(R.id.searchCityEditText) as EditText
        mHubButton = findViewById<View>(R.id.searchButton) as Button

        mPrefs = getSharedPreferences("block", Context.MODE_PRIVATE) // initialize saved preferences
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

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(this, NOT_COMPLETE, Toast.LENGTH_LONG).show()
            }

        }
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
        private const val TAG = "Search-Activity"
        private const val NOT_COMPLETE = "Enter a neighborhood and city!"
    }
}