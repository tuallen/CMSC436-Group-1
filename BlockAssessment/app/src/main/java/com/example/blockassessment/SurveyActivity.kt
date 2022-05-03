package com.example.blockassessment

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SurveyActivity : AppCompatActivity() {
    lateinit var db : FirebaseFirestore
    private lateinit var mTitle: TextView
    lateinit var mHousing: EditText
    lateinit var mNeighborhood: EditText
    lateinit var mTransportation: EditText
    lateinit var mEnvironment: EditText
    lateinit var mHealth: EditText
    lateinit var mEngagement: EditText
    lateinit var mOpportunity: EditText
    lateinit var mReview: EditText
    private lateinit var mSubmitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Entered onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        db = Firebase.firestore // Reference to database

        // Initialize title
        mTitle = findViewById<View>(R.id.textViewTitle) as TextView
        if (intent.hasExtra("blockName")) { // TODO: blockName
            mTitle.text = intent.getStringExtra("blockName")
        } else {
            mTitle.text = resources.getString(R.string.default_survey_title)
        }

        // Initialize EditText fields
        mHousing = findViewById<View>(R.id.editTextHousing) as EditText
        mNeighborhood = findViewById<View>(R.id.editTextNeighborhood) as EditText
        mTransportation = findViewById<View>(R.id.editTextTransportation) as EditText
        mEnvironment = findViewById<View>(R.id.editTextEnvironment) as EditText
        mHealth = findViewById<View>(R.id.editTextHealth) as EditText
        mEngagement = findViewById<View>(R.id.editTextEngagement) as EditText
        mOpportunity = findViewById<View>(R.id.editTextOpportunity) as EditText
        mReview = findViewById<View>(R.id.editTextPersonalReview) as EditText
        // Limit score EditText fields to integers from 1 to 100
        mHousing.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mNeighborhood.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mTransportation.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mEnvironment.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mHealth.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mEngagement.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mOpportunity.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))

        // Initialize submit button
        mSubmitButton = findViewById<View>(R.id.buttonSubmitAssessment) as Button
        mSubmitButton.setOnClickListener {
            enterClicked()
        }
        Log.d(TAG, "Finished onCreate()")
    }

    // Submit assessment and close activity
    private fun enterClicked() {
        Log.d(TAG, "Entered enterClicked()")
        // Get block ID
        var blockID = "TEST"
        if (intent.hasExtra("blockID")) {
            blockID = intent.getStringExtra("blockID").toString()
        }

        // Parse assessment into hashmap
        val assessment = hashMapOf(
            "block" to blockID, //TODO: City information,
            "housing" to Integer.parseInt(mHousing.text.toString()),
            "neighborhood" to Integer.parseInt(mNeighborhood.text.toString()),
            "transportation" to Integer.parseInt(mTransportation.text.toString()),
            "environment" to Integer.parseInt(mEnvironment.text.toString()),
            "health" to Integer.parseInt(mHealth.text.toString()),
            "engagement" to Integer.parseInt(mEngagement.text.toString()),
            "opportunity" to Integer.parseInt(mOpportunity.text.toString()),
            "review" to mReview.text.toString()
        )

        // Push assessment to Firebase
        db.collection("reviews")
            .add(assessment)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        // Close activity // TODO: Intent, if necessary
        Log.d(TAG, "Finished enterClicked()")
        finish()
    }

    // Limit EditText field to integers from minValue to maxValue
    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dStart: Int,
            dEnd: Int
        ): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
        }
    }

    companion object {
        private const val TAG = "Survey-Activity"
    }

}