package com.example.blockassessment

import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SurveyActivity : AppCompatActivity() {
    private lateinit var db : FirebaseFirestore
    private lateinit var mHousing: EditText
    private lateinit var mNeighborhood: EditText
    private lateinit var mTransportation: EditText
    private lateinit var mEnvironment: EditText
    private lateinit var mHealth: EditText
    private lateinit var mEngagement: EditText
    private lateinit var mOpportunity: EditText
    private lateinit var mReview: EditText
    private lateinit var mSubmitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "Entered onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_survey)

        db = Firebase.firestore // Reference to database

        // Set title
        title = if (intent.hasExtra("blockName")) { // TODO: blockName
                    intent.getStringExtra("blockName").toString()
                } else {
                    resources.getString(R.string.default_survey_title)
                }

        // Initialize EditText fields
        mHousing = findViewById<View>(R.id.editTextHousing) as EditText
        mNeighborhood = findViewById<View>(R.id.editTextNeighborhood) as EditText
        mTransportation = findViewById<View>(R.id.editTextTransportation) as EditText
        mEnvironment = findViewById<View>(R.id.editTextEnvironment) as EditText
        mHealth = findViewById<View>(R.id.editTextHealth) as EditText
        mEngagement = findViewById<View>(R.id.editTextEngagement) as EditText
        mOpportunity = findViewById<View>(R.id.editTextOpportunity) as EditText
        mReview = findViewById<View>(R.id.editTextReview) as EditText
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
        db.collection("assessments")
            .add(assessment)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Toast.makeText(applicationContext, SUBMIT_SUCCESS, Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(applicationContext, SUBMIT_FAILED, Toast.LENGTH_LONG).show()
            }

        var housing : Double? = null
        var neighborhood : Double?  = null
        var transportation : Double?  = null
        var environment : Double?  = null
        var health : Double?  = null
        var engagement : Double?  = null
        var opportunity : Double?  = null
        val mBlockCollection = db.collection("assessments").whereEqualTo("block", "test")
        mBlockCollection.get()
            .addOnSuccessListener { result ->
                val numDocs = result.size()
                if (numDocs > 0) {

                    neighborhood = 0.0
                    transportation = 0.0
                    environment = 0.0
                    health = 0.0
                    engagement = 0.0
                    opportunity = 0.0
                    for (document in result) {
                        if (document.getLong("housing") != null){
                            if (housing == null) housing = 0.0
                            housing!! += document.getDouble("housing")!!
                        }
                        if (document.getLong("neighborhood") != null) neighborhood += document.getLong("neighborhood")!!
                        if (document.getLong("transportation") != null) transportation += document.getLong("transportation")!!
                        if (document.getLong("environment") != null) environment += document.getLong("environment")!!
                        if (document.getLong("health") != null) health += document.getLong("health")!!
                        if (document.getLong("engagement") != null) engagement += document.getLong("engagement")!!
                        if (document.getLong("opportunity") != null) opportunity += document.getLong("opportunity")!!
                    }
                    housing /= result.size()
                    neighborhood /= result.size()
                    transportation /= result.size()
                    transportation /= result.size()
                    health /= result.size()
                    engagement /= result.size()
                    opportunity /= result.size()
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
        if (mBlockCollection.si)

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
        private const val SUBMIT_SUCCESS = "Assessment Submitted!"
        private const val SUBMIT_FAILED = "Failed to submit assessment!"
    }

}