package com.example.weatherwork.ui.map

import android.Manifest
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherwork.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,LocationListener {
//
    private lateinit var mMap: GoogleMap
    private lateinit var mapViewModel: MapViewModel
    private var locationManager: LocationManager? = null
    var mLastLocation: Location? = null
    var mCurrLocationMarker: Marker? = null
    internal lateinit var mLocationResult: LocationRequest
    internal lateinit var mLocationCallback: LocationCallback

    internal lateinit var mLocationRequest: LocationRequest
    internal var mGoogleApiClient: GoogleApiClient? = null
    internal var mFusedLocationClient: FusedLocationProviderClient? = null

    private val MIN_TIME: Long = 400
    private val MIN_DISTANCE = 1000f

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mapViewModel =
                ViewModelProvider(this).get(MapViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        mapViewModel.text.observe(viewLifecycleOwner, Observer {
        })
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment

        /*val mapFragment = activity?.supportFragmentManager!!
            .findFragmentById(R.id.map) as SupportMapFragment*/
        mapFragment.getMapAsync(this)


       /* locationManager = getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            MIN_TIME,
            MIN_DISTANCE,
            this
        ); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER*/
        return root
    }
    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireContext())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    override fun onMapReady(gmap: GoogleMap?) {
        mMap = gmap!!

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mMap.isMyLocationEnabled = true;
            } else {
                //Request Location Permission
                //checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
            mMap.isMyLocationEnabled = true;
        }


/*
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true;
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }
    override fun onLocationChanged(location: Location) {

        mLastLocation = location
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker!!.remove()
        }
        //Place current location marker
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        mCurrLocationMarker = mMap!!.addMarker(markerOptions)

        //move map camera
        mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

        //stop location updates
        if (mGoogleApiClient != null) {
            mFusedLocationClient?.removeLocationUpdates(mLocationCallback)
        }
    }

    override fun onConnected(p0: Bundle?) {
        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 1000
        mLocationRequest.fastestInterval = 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            //mFusedLocationClient?.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper()!!)
        }
    }

    override fun onConnectionSuspended(p0: Int) {
        Toast.makeText(requireContext(),"connection suspended", Toast.LENGTH_SHORT).show()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(requireContext(),"connection failed", Toast.LENGTH_SHORT).show()
    }
}