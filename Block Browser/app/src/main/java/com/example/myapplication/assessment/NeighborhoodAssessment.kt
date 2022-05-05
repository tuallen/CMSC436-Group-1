package com.example.myapplication.assessment

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NeighborhoodAssessment : Fragment() {
    private lateinit var root : View;
    private lateinit var blockID: String;
    private lateinit var deviceID: String;
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

    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d(TAG, "Entered onCreate()")
        root = inflater.inflate(com.example.myapplication.R.layout.assess_layout, container, false)
        deviceID = Settings.Secure.getString(mCallback.contentResolver, Settings.Secure.ANDROID_ID)
        // Get block ID
        blockID = "TEST"
//        if (intent.hasExtra("blockID")) {
//            blockID = intent.getStringExtra("blockID").toString()
//        }
        db = Firebase.firestore // Reference to database
//        db.collection("assessments")


        // Set title
//        mCallback.title = if (intent.hasExtra("blockName")) { // TODO: blockName
//            intent.getStringExtra("blockName").toString()
//        } else {
//            resources.getString(R.string.default_survey_title)
//        }

        // Initialize EditText fields
        mHousing = root.findViewById<View>(R.id.editTextHousing) as EditText
        mNeighborhood = root.findViewById<View>(R.id.editTextNeighborhood) as EditText
        mTransportation = root.findViewById<View>(R.id.editTextTransportation) as EditText
        mEnvironment = root.findViewById<View>(R.id.editTextEnvironment) as EditText
        mHealth = root.findViewById<View>(R.id.editTextHealth) as EditText
        mEngagement = root.findViewById<View>(R.id.editTextEngagement) as EditText
        mOpportunity = root.findViewById<View>(R.id.editTextOpportunity) as EditText
        mReview = root.findViewById<View>(R.id.editTextReview) as EditText
        // Limit score EditText fields to integers from 1 to 100
        mHousing.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mNeighborhood.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mTransportation.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mEnvironment.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mHealth.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mEngagement.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))
        mOpportunity.filters = arrayOf<InputFilter>(MinMaxFilter(1, 100))

        // Initialize submit button
        mSubmitButton = root.findViewById<View>(R.id.buttonSubmitAssessment) as Button
        mSubmitButton.setOnClickListener {
            enterClicked()
        }
        Log.d(TAG, "Finished onCreate()")

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    // Submit assessment
    private fun enterClicked() {
        Log.d(TAG, "Entered enterClicked()")

        // Parse assessment into hashmap
        val assessment = hashMapOf(
            "device" to deviceID,
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
                Toast.makeText(mCallback, SUBMIT_SUCCESS, Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                Toast.makeText(mCallback, SUBMIT_FAILED, Toast.LENGTH_LONG).show()
            }

        // Close activity // TODO: Intent, if necessary
        Log.d(TAG, "Finished enterClicked()")
//        finish()
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

