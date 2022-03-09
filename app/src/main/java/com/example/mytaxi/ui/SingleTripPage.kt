package com.example.mytaxi.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytaxi.R
import com.example.mytaxi.databinding.FragmentSingleTripPageBinding
import com.example.mytaxi.utils.NavOption
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior


class SingleTripPage : Fragment(),OnMapReadyCallback {

    private lateinit var binding:FragmentSingleTripPageBinding
    private lateinit var bottomSheetBehavior:BottomSheetBehavior<ConstraintLayout>
    private lateinit var map:GoogleMap

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_single) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetLayout.bottomSheet)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingleTripPageBinding.inflate(inflater,container,false)

        binding.backImg.setOnClickListener {
            findNavController().navigate(R.id.tripsPage, null,NavOption.getNavOptions())
        }

        return binding.root
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val uzbekistan = LatLng(41.29402605019309, 69.24612778462361)

        map.addMarker(MarkerOptions()
            .position(uzbekistan)
            .title("My Taxi")
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_pin_red)))

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(uzbekistan,16f))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.tripsPage, null,NavOption.getNavOptions())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(onBackCallback)
    }


}