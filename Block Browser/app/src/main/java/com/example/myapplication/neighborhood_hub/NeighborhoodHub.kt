package com.example.myapplication.neighborhood_hub

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.IOException


class NeighborhoodHub: Fragment(), OnMapReadyCallback {
    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context

    // Database
    private lateinit var db : FirebaseFirestore
    private lateinit var blockID: String

    // Views
    private lateinit var root : View
    private lateinit var sReviewScroll : LinearLayout
    private lateinit var sHousing: TextView
    private lateinit var sNeighborhood: TextView
    private lateinit var sTransportation: TextView
    private lateinit var sEnvironment: TextView
    private lateinit var sHealth: TextView
    private lateinit var sEngagement: TextView
    private lateinit var sOpportunity: TextView
    private lateinit var mMapView: MapView
    //private lateinit var btnMap: Button

    private lateinit var mMap: GoogleMap

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d(TAG, "Entered Hub onCreateView()")

        // Get views
        root = inflater.inflate(R.layout.hub_layout, container, false)
        sReviewScroll = root.findViewById<View>(R.id.reviewScroll) as LinearLayout
        sHousing = root.findViewById<View>(R.id.housingScore) as TextView
        sNeighborhood = root.findViewById<View>(R.id.neighborhoodScore) as TextView
        sTransportation = root.findViewById<View>(R.id.transportationScore) as TextView
        sEnvironment = root.findViewById<View>(R.id.environmentScore) as TextView
        sHealth = root.findViewById<View>(R.id.healthScore) as TextView
        sEngagement = root.findViewById<View>(R.id.engagementScore) as TextView
        sOpportunity = root.findViewById<View>(R.id.opportunityScore) as TextView

        //btnMap = root.findViewById(R.id.mapBtn)
        //btnMap.setOnClickListener {  }
        mMapView = root.findViewById(R.id.mapHub)
        /*
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        */
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        // Load data
        db = Firebase.firestore // Reference to database
        blockID = "TEST" // This block TODO: Take from search
        loadData()

        return root
    }

    override fun onAttach(context: Context) {
        // load existing assessment, if it exists
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    // load existing assessments information
    private fun loadData() {
        Log.d(TAG, "Entered Hub loadData()")

        db.collection("assessments")
            .whereEqualTo("block", blockID)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Average score accumulators
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

                        // Build rating string
                        val ratingString = "\nHousing: $housingScore, " +
                                    "Neighborhood: $neighborhoodScore, " +
                                    "Transportation: $transportationScore, " +
                                    "Environment: $environmentScore, " +
                                    "Health: $healthScore, " +
                                    "Engagement: $engagementScore, " +
                                    "Opportunity: $opportunityScore\n"

                        if (review != "") {
                            // Add text view to reviews
                            val textViewResult = TextView(mCallback)
                            val textViewResultParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                7.0f
                            )
                            textViewResultParams.setMargins(70, 20, 70, 0)
                            textViewResult.layoutParams = textViewResultParams
                            textViewResult.text = review // + ratingString
                            sReviewScroll.addView(textViewResult)
                        }
                    }

                    // Calculate average scores from accumulators
                    housingAverage /= result.size()
                    neighborhoodAverage /= result.size()
                    transportationAverage /= result.size()
                    environmentAverage /= result.size()
                    healthAverage /= result.size()
                    engagementAverage /= result.size()
                    opportunityAverage /= result.size()

                    // Update score text views
                    sHousing.text = housingAverage.toInt().toString()
                    sNeighborhood.text = neighborhoodAverage.toInt().toString()
                    sTransportation.text = transportationAverage.toInt().toString()
                    sEnvironment.text = environmentAverage.toInt().toString()
                    sHealth.text = healthAverage.toInt().toString()
                    sEngagement.text = engagementAverage.toInt().toString()
                    sOpportunity.text = opportunityAverage.toInt().toString()
                }
                else {
                    // No scores yet
                    sHousing.text = "No score yet!"
                    sNeighborhood.text = "No score yet!"
                    sTransportation.text = "No score yet!"
                    sEnvironment.text = "No score yet!"
                    sHealth.text = "No score yet!"
                    sEngagement.text = "No score yet!"
                    sOpportunity.text = "No score yet!"

                }

                // No reviews yet
                if (sReviewScroll.childCount == 0){
                    val textViewResult = TextView(mCallback)
                    val textViewResultParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        7.0f
                    )
                    textViewResultParams.setMargins(70, 0, 70, 0)
                    textViewResult.layoutParams = textViewResultParams
                    textViewResult.text = "No reviews yet!"
                    sReviewScroll.addView(textViewResult)
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "FAILED")
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "Entered onMapReady")
        mMap = googleMap
        //map.setMinZoomPreference(6.0f)
        //map.setMaxZoomPreference(14.0f)

        // TODO: Change address to neighborhood id
        // Process text for network transmission
        // https://www.javatpoint.com/kotlin-android-google-map-search-location
        val neighborhood = "Old Town, College Park"
        var addressList: List<Address>? = null

        val geoCoder = Geocoder(mCallback)
        try {
            addressList = geoCoder.getFromLocationName(neighborhood, 1)

        } catch (e: IOException){
            Log.e(TAG, "${e.stackTrace}")
        }

        Log.i(TAG, "${addressList.toString()}")
        val address = addressList!![0]
        val latLng = LatLng(address.latitude, address.longitude)
        mMap.addMarker(MarkerOptions().position(latLng).title(neighborhood))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15F))


        /*
        // Create Intent object for starting Google Maps application
        val geoIntent = Intent(
            Intent.ACTION_VIEW, Uri
                .parse("geo:0,0?q=$address")
        )
        */

        /*
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
         */
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    companion object {
        private const val TAG = "Hub-Activity"
    }

}
