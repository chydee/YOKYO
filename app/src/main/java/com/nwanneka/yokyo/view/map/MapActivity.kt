package com.nwanneka.yokyo.view.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.nwanneka.yokyo.R
import com.nwanneka.yokyo.data.Logg
import com.nwanneka.yokyo.view.main.modals.CreateLogModal
import com.nwanneka.yokyo.view.main.modals.CreateLogModalDelegate
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class MapActivity : AppCompatActivity(), OnMapReadyCallback, CreateLogModalDelegate {

    private var mMap: GoogleMap? = null

    private var mapFragment: SupportMapFragment? = null
    private var mLocationRequest: LocationRequest? = null
    private var mCurrLocationMarker: Marker? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var addressLine: String? = null
    private var time: String? = null
    private var date: String? = null

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                Log.i("MapsActivity", "Location: " + location.latitude + " " + location.longitude)
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker!!.remove()
                }

                //Place current location marker
                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                mCurrLocationMarker = mMap!!.addMarker(markerOptions)

                //move map camera
                mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11f))
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        supportActionBar!!.title = "Select location"

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?

        if (mapFragment != null) {
            mapFragment!!.getMapAsync(this)
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * <p>
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 120000 // two minute interval

        mLocationRequest!!.fastestInterval = 120000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            //Location Permission already granted
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest!!, mLocationCallback, Looper.myLooper()!!)
            mMap?.isMyLocationEnabled = true
        } else {
            //Request Location Permission
            checkLocationPermission()
        }

        mMap!!.setOnMapClickListener { latLng: LatLng ->
            getAddress(latLng.latitude, latLng.longitude)

            val bundle: Bundle = Bundle()
            //bundle.putParcelable(EXTRA_DOCUMENT, documents)
            val createLogModal = CreateLogModal(this@MapActivity)
            createLogModal.arguments = bundle
            createLogModal.isCancelable = true
            createLogModal.show(supportFragmentManager, createLogModal.tag)

            /*val returnIntent = Intent()
            returnIntent.putExtra("location", addressLine)
            setResult(RESULT_OK, returnIntent)
            finish()*/
        }
    }

    override fun onPause() {
        super.onPause()

        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
    }

    private val MY_PERMISSIONS_REQUEST_LOCATION = 99

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { _, _ -> //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@MapActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(this@MapActivity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val obj = addresses[0]
            addressLine = obj.getAddressLine(0)
            /*add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();*/Log.v("IGA", "Address$addressLine")
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentTimeAndDate() {

    }

    override fun onCancel(log: Logg) {

    }

    override fun onSave(log: Logg) {

    }
}