package com.example.mytaxi.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mytaxi.R
import com.example.mytaxi.databinding.DialogPermissionBinding
import com.example.mytaxi.databinding.FragmentHomePageBinding
import com.example.mytaxi.utils.NavOption
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class HomePage : Fragment(R.layout.fragment_home_page), OnMapReadyCallback,
    GoogleMap.OnMapClickListener {

    private val binding by viewBinding(FragmentHomePageBinding::bind)
    private var map:GoogleMap?= null
    private lateinit var placesClient: PlacesClient
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val defaultLocation = LatLng(41.29402605019309, 69.24612778462361)
    private var locationPermissionGranted = false
    private var lastKnownLocation: Location? = null
    private var pos=0
    private var mapPingList = arrayListOf(R.drawable.map_pin_red,R.drawable.map_pin)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        navigationItemClick()
        binding.navigationView.itemIconTintList = null

        binding.homeScreen.drawerBtn.setOnClickListener {
            binding.navigationDrawer.openDrawer(GravityCompat.START)
        }

        Places.initialize(requireContext(), getString(R.string.map_key))
        placesClient = Places.createClient(requireContext())

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.homeScreen.myLocationBtn.setOnClickListener {
            getDeviceLocation()
        }

    }

    private fun navigationItemClick() {
        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.my_trips -> {
                    findNavController().navigate(R.id.tripsPage, null, NavOption.getNavOptions())
                    binding.navigationDrawer.close()
                }
                R.id.payment_methods -> {

                }
                R.id.favorite_addresses -> {

                }
            }
            true
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map=map

        map.setOnMapClickListener(this)
        map.setOnMapClickListener {
            val geocoder = Geocoder(requireContext())
            val list = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            map.clear()
            binding.homeScreen.locationAddressTv.text = list[0].getAddressLine(0)
            map.addMarker(
                MarkerOptions()
                    .position(it)
                    .icon(BitmapDescriptorFactory.fromResource(mapPingList[pos]))
            )

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM.toFloat()))
        }


        getLocationPermission()

        updateLocationUI()

        getDeviceLocation()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.navigationDrawer.isOpen) {
                    binding.navigationDrawer.close()
                } else {
                    activity?.finish()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackCallback)

    }
    private fun getDeviceLocation() {
        map?.clear()
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {

                            val geocoder = Geocoder(requireContext())
                            val list = geocoder.getFromLocation(lastKnownLocation?.latitude!!, lastKnownLocation?.longitude!!, 1)

                            binding.homeScreen.locationAddressTv.text = list[0].getAddressLine(0).toString()

                            map?.addMarker(MarkerOptions()
                                .position(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude))
                                .icon(BitmapDescriptorFactory.fromResource(mapPingList[pos])
                                )
                            )


                            map?.animateCamera(CameraUpdateFactory
                                .newLatLngZoom(LatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }

    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = false
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }

    }

    private fun getLocationPermission() {
        Dexter.withContext(requireContext())
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .withListener(object :MultiplePermissionsListener{
                override fun onPermissionsChecked(multiple_per: MultiplePermissionsReport?) {
                    if(multiple_per?.areAllPermissionsGranted() == true){
                        locationPermissionGranted = true
                        getDeviceLocation()
                        updateLocationUI()
                    }else{
                        if(multiple_per?.isAnyPermissionPermanentlyDenied!!){
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri: Uri = Uri.fromParts("package", activity?.packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    }
                }


                override fun onPermissionRationaleShouldBeShown(
                    requests: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    val dialogMap = AlertDialog.Builder(requireContext())
                    val dialogPermissionBinding = DialogPermissionBinding.inflate(layoutInflater)
                    dialogMap.setView(dialogPermissionBinding.root)
                    val builderMap = dialogMap.create()
                    builderMap.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    dialogPermissionBinding.cancelCv.setOnClickListener {
                        token?.cancelPermissionRequest()
                        builderMap.dismiss()
                    }

                    dialogPermissionBinding.allowCv.setOnClickListener {
                        token?.continuePermissionRequest()
                        builderMap.dismiss()
                    }
                    builderMap.show()
                }
            }).withErrorListener {
                Log.d(TAG, "getLocationPermission: Error")
            }.check()




    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        updateLocationUI()
    }

    companion object {
        private val TAG = HomePage::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    }

    override fun onMapClick(latLang: LatLng) {
        map?.setOnMapClickListener {
            val geocoder = Geocoder(requireContext())
            val list = geocoder.getFromLocation(it.latitude, it.longitude, 1)
            map?.clear()
            binding.homeScreen.locationAddressTv.text = list[0].getAddressLine(0)
            map?.addMarker(
                MarkerOptions()
                    .position(it)
                    .icon(BitmapDescriptorFactory.fromResource(mapPingList[pos]))
            )

            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM.toFloat()))
        }

    }


}