package com.example.myapplication.map

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.neighborhood_hub.NeighborhoodHub
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

class MapFrag() : Fragment(), OnMapReadyCallback {
    //Whenever you need the context use mCallback (goes with fun onAttach)
    private lateinit var mCallback: Context

    // Database
    private lateinit var db : FirebaseFirestore
    private lateinit var blockID: String

    // Views
    private lateinit var root : View
    private lateinit var mMapView: MapView
    private lateinit var mMap: GoogleMap

    private var locations: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Log.d(TAG, "Entered Map onCreateView()")

        root = inflater.inflate(R.layout.map_layout, container, false)

        // Load data
        db = Firebase.firestore // Reference to database
        //blockID = "TEST" // This block TODO: Take from search
        loadData()

        // Load Map
        mMapView = root.findViewById(R.id.map_layout)
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        activity?.title = "Map"

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try { mCallback = context }
        catch (e: ClassCastException) { throw ClassCastException("$context must implement SelectionListener") }
    }

    // load existing assessments information
    private fun loadData() {
        Log.d(TAG, "Entered Map loadData()")

        db.collection("assessments")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    // Process all assessments for this block
                    for (document in result) {
                        blockID = document.getString("block").toString()
                        if (blockID != "TEST" && !(locations.contains(blockID))) {
                            locations.add(blockID)
                            Log.i(TAG, "Got from firebase $blockID")
                        }
                    }
                }
            }
            .addOnFailureListener{
                Log.d(TAG, "FAILED")
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "Entered MapFrag onMapReady")
        mMap = googleMap

        // TODO: Change address to neighborhood id
        // Process text for network transmission
        // https://www.javatpoint.com/kotlin-android-google-map-search-location
        // val neighborhood = "Old Town, College Park"
        var addressList: List<Address>? = null

        val geoCoder = Geocoder(mCallback)

        for (str in locations) {
            try {
                addressList = geoCoder.getFromLocationName(str, 1)

            } catch (e: IOException){
                Log.e(TAG, "${e.stackTrace}")
            }

            Log.i(TAG, "$str, $addressList")
            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList!![0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.addMarker(MarkerOptions().position(latLng).title(str))
            }

        }

        val camStart = LatLng(39.0, -77.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camStart, 5F))

        // adding on click listener to marker of google maps.
        mMap.setOnMarkerClickListener { marker -> // on marker click we are getting the title of our marker
            // which is clicked and displaying it in a toast message.
            val markerName = marker.title
            Toast.makeText(mCallback, "Clicked location is $markerName", Toast.LENGTH_SHORT)
                .show()

            var bundle = Bundle()
            bundle.putString("BlockID", markerName)
            val fragment = NeighborhoodHub()
            fragment.arguments = bundle

            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.flContent, fragment, "findThisFragment")
                ?.addToBackStack(null)
                ?.commit()

            false
        }

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
        private const val TAG = "Map-Activity"
    }

}
