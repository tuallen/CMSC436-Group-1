package com.example.myapplication.neighborhood_hub

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NeighborhoodHub: Fragment(){
    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context

    private lateinit var root : View
    private lateinit var blockID: String
    private lateinit var deviceID: String
    private lateinit var db : FirebaseFirestore

    private lateinit var sReviewScroll : LinearLayout

    // TextViews to display scores
    private lateinit var sHousing: TextView
    private lateinit var sNeighborhood: TextView
    private lateinit var sTransportation: TextView
    private lateinit var sEnvironment: TextView
    private lateinit var sHealth: TextView
    private lateinit var sEngagement: TextView
    private lateinit var sOpportunity: TextView

    private lateinit var btnMap: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d(TAG, "Entered Hub onCreateView()")

        root = inflater.inflate(R.layout.hub_layout, container, false)
        deviceID = Settings.Secure.getString(mCallback.contentResolver, Settings.Secure.ANDROID_ID)
        db = Firebase.firestore // Reference to database

        blockID = "TEST"

        // Get views
        sReviewScroll = root.findViewById<View>(R.id.reviewScroll) as LinearLayout
        sHousing = root.findViewById<View>(R.id.housingScore) as TextView
        sNeighborhood = root.findViewById<View>(R.id.neighborhoodScore) as TextView
        sTransportation = root.findViewById<View>(R.id.transportationScore) as TextView
        sEnvironment = root.findViewById<View>(R.id.environmentScore) as TextView
        sHealth = root.findViewById<View>(R.id.healthScore) as TextView
        sEngagement = root.findViewById<View>(R.id.engagementScore) as TextView
        sOpportunity = root.findViewById<View>(R.id.opportunityScore) as TextView

        btnMap = root.findViewById(R.id.mapBtn)
        btnMap.setOnClickListener {  }

        loadData()

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    // load existing assessment, if it exists
    private fun loadData() {
        Log.d(TAG, "Entered Hub loadData()")

        db.collection("assessments")
            .whereEqualTo("block", blockID)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    var housingAverage = 0.0
                    var neighborhoodAverage = 0.0
                    var transportationAverage = 0.0
                    var environmentAverage = 0.0
                    var healthAverage = 0.0
                    var engagementAverage = 0.0
                    var opportunityAverage = 0.0

                    // Process all assessments for this block
                    for (document in result) {
                        // Unpack document
                        val housingScore = document.getLong("housing")!!.toInt()
                        val neighborhoodScore = document.getLong("neighborhood")!!.toInt()
                        val transportationScore = document.getLong("transportation")!!.toInt()
                        val environmentScore = document.getLong("environment")!!.toInt()
                        val healthScore = document.getLong("health")!!.toInt()
                        val engagementScore = document.getLong("engagement")!!.toInt()
                        val opportunityScore = document.getLong("opportunity")!!.toInt()
                        val review = document.getString("review")

                        // Add scores to average
                        housingAverage += housingScore
                        neighborhoodAverage += neighborhoodScore
                        transportationAverage += transportationScore
                        environmentAverage += environmentScore
                        healthAverage += healthScore
                        engagementAverage += engagementScore
                        opportunityAverage += opportunityScore

                        // Build Add review to display
                        val assessmentString = "$review\n" //+
//                            "Housing: $housingScore, " +
//                                    "Neighborhood: $neighborhoodScore, " +
//                                    "Transportation: $transportationScore, " +
//                                    "Environment: $environmentScore, " +
//                                    "Health: $healthScore, " +
//                                    "Engagement: $engagementScore, " +
//                                    "Opportunity: $opportunityScore\n"

                        val textViewResult = TextView(mCallback)
                        val textViewResultParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            7.0f
                        )
                        textViewResultParams.setMargins(70, 20, 70, 0)
                        textViewResult.layoutParams = textViewResultParams
                        textViewResult.text = assessmentString
                        sReviewScroll.addView(textViewResult)
                    }
                    housingAverage /= result.size()
                    neighborhoodAverage /= result.size()
                    transportationAverage /= result.size()
                    environmentAverage /= result.size()
                    healthAverage /= result.size()
                    engagementAverage /= result.size()
                    opportunityAverage /= result.size()

                    sHousing.text = housingAverage.toInt().toString()
                    sNeighborhood.text = neighborhoodAverage.toInt().toString()
                    sTransportation.text = transportationAverage.toInt().toString()
                    sEnvironment.text = environmentAverage.toInt().toString()
                    sHealth.text = healthAverage.toInt().toString()
                    sEngagement.text = engagementAverage.toInt().toString()
                    sOpportunity.text = opportunityAverage.toInt().toString()
                }
                else {
                    sHousing.text = "No reviews yet."
                    sNeighborhood.text = "No reviews yet."
                    sTransportation.text = "No reviews yet."
                    sEnvironment.text = "No reviews yet."
                    sHealth.text = "No reviews yet."
                    sEngagement.text = "No reviews yet."
                    sOpportunity.text = "No reviews yet."
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "FAILED")
            }

    }

    private fun viewMap() {
        /* TODO: Launch viewMap activity
            if able, make map be a small view on hub
         */
        /*
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
         */
    }

    /*
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
     */

    companion object {
        private const val TAG = "Hub-Activity"
    }

}
