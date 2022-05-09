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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class NeighborhoodAssessment : Fragment() {
    private lateinit var root : View
    private lateinit var blockID: String
    private lateinit var deviceID: String
    private lateinit var db : FirebaseFirestore
    private var mDoc : QueryDocumentSnapshot? = null

    private lateinit var mHousing: EditText
    private lateinit var mNeighborhood: EditText
    private lateinit var mTransportation: EditText
    private lateinit var mEnvironment: EditText
    private lateinit var mHealth: EditText
    private lateinit var mEngagement: EditText
    private lateinit var mOpportunity: EditText
    private lateinit var mReview: EditText
    private lateinit var mSubmitButton: Button
    private lateinit var mDeleteButton: Button
    private lateinit var street: TextView
    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.d(TAG, "Entered onCreateView()")

        root = inflater.inflate(R.layout.assess_layout, container, false)
        deviceID = Settings.Secure.getString(mCallback.contentResolver, Settings.Secure.ANDROID_ID)
        db = Firebase.firestore // Reference to database

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        blockID = "TEST"

        //Block ID from search
        street = root.findViewById(R.id.street_name)
        val bundle = this.arguments
        if (bundle != null) {
            blockID = bundle.getString("BlockID").toString()
            Log.i("Tag", blockID)
            street.text = blockID
        }

        // Get block ID

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
            submitClicked()
        }

        // Initialize delete button
        mDeleteButton = root.findViewById<View>(R.id.buttonDeleteAssessment) as Button
        mDeleteButton.setOnClickListener {
            deleteClicked()
        }

        loadData() // load existing assessment

        Log.d(TAG, "Finished onCreate()")
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    // load existing assessment, if it exists
    private fun loadData() {
        db.collection("assessments")
            .whereEqualTo("device", deviceID)
            .whereEqualTo("block", blockID)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // update EditText fields to existing assessment
                    mDoc = result.elementAt(0)
                    if (mDoc!!.getLong("housing") != null) mHousing.setText(mDoc!!.getLong("housing")!!.toString())
                    if (mDoc!!.getLong("neighborhood") != null) mNeighborhood.setText(mDoc!!.getLong("neighborhood")!!.toString())
                    if (mDoc!!.getLong("transportation") != null) mTransportation.setText(mDoc!!.getLong("transportation")!!.toString())
                    if (mDoc!!.getLong("environment") != null) mEnvironment.setText(mDoc!!.getLong("environment")!!.toString())
                    if (mDoc!!.getLong("health") != null) mHealth.setText(mDoc!!.getLong("health")!!.toString())
                    if (mDoc!!.getLong("engagement") != null) mEngagement.setText(mDoc!!.getLong("engagement")!!.toString())
                    if (mDoc!!.getLong("opportunity") != null) mOpportunity.setText(mDoc!!.getLong("opportunity")!!.toString())
                    if (mDoc!!.getString("review") != null) mReview.setText(mDoc!!.getString("review")!!)
                    // Delete all other assessments with the same block and device id
                    for (document in result) {
                        if (document != mDoc) document.reference.delete()
                    }
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "FAILED")
            }

    }

    // Submit assessment
    private fun submitClicked() {
        Log.d(TAG, "Entered enterClicked()")

        // Parse assessment into hashmap
        val assessment = HashMap<String, Any>()
        assessment["device"] = deviceID
        assessment["block"] = blockID
        if (mHousing.text.toString() != "") assessment["housing"] = Integer.parseInt(mHousing.text.toString())
        if (mNeighborhood.text.toString() != "") assessment["neighborhood"] = Integer.parseInt(mNeighborhood.text.toString())
        if (mTransportation.text.toString() != "") assessment["transportation"] = Integer.parseInt(mTransportation.text.toString())
        if (mEnvironment.text.toString() != "") assessment["environment"] = Integer.parseInt(mEnvironment.text.toString())
        if (mHealth.text.toString() != "") assessment["health"] = Integer.parseInt(mHealth.text.toString())
        if (mEngagement.text.toString() != "") assessment["engagement"] = Integer.parseInt(mEngagement.text.toString())
        if (mOpportunity.text.toString() != "") assessment["opportunity"] = Integer.parseInt(mOpportunity.text.toString())

        // Submit assessment if there is no existing assessment
        if (assessment.size >= 9) {
            assessment["review"] = mReview.text.toString()
            if (mDoc == null) {
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
            }
            // Edit existing assessment
            else {
                mDoc!!.reference
                    .update(assessment as Map<String, Any>)
                    .addOnSuccessListener {
                        Toast.makeText(mCallback, EDIT_SUCCESS, Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(mCallback, EDIT_FAILED, Toast.LENGTH_LONG).show()
                    }
            }
            loadData()
        }
        // Delete assessment
        else if (assessment.size == 2){
            deleteClicked()
        }
        // Not filled out
        else{
            Toast.makeText(mCallback, NOT_COMPLETE, Toast.LENGTH_LONG).show()
        }
    }

    // Delete assessment
    private fun deleteClicked() {
        // Delete from database
        if (mDoc != null) mDoc!!.reference.delete()
        mDoc = null

        // Reset fields
        mHousing.setText("")
        mNeighborhood.setText("")
        mTransportation.setText("")
        mEnvironment.setText("")
        mHealth.setText("")
        mEngagement.setText("")
        mOpportunity.setText("")
        mReview.setText("")
        Toast.makeText(mCallback, DELETE_SUCCESSFUL, Toast.LENGTH_LONG).show()
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
        private const val SUBMIT_SUCCESS = "Assessment submitted!"
        private const val SUBMIT_FAILED = "Failed to submit assessment!"
        private const val EDIT_SUCCESS = "Assessment updated!"
        private const val EDIT_FAILED = "Failed to update assessment!"
        private const val DELETE_SUCCESSFUL = "Deleted assessment!"
        private const val NOT_COMPLETE = "Fill out all score fields to submit, or erase all score fields to delete."
    }

}

