package com.example.mytaxi.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.mytaxi.R
import com.example.mytaxi.adapters.TripsRvAdapter
import com.example.mytaxi.databinding.FragmentTripsPageBinding
import com.example.mytaxi.utils.NavOption

class TripsPage : Fragment(R.layout.fragment_trips_page) {

    private val binding by viewBinding(FragmentTripsPageBinding::bind)
    private lateinit var tripsRvAdapter: TripsRvAdapter
    private lateinit var tripLists:MutableMap<String,ArrayList<Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val onBackCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.homePage,null, NavOption.getNavOptions())
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val list:ArrayList<Int> = arrayListOf(R.drawable.delivery_car,R.drawable.taxi_car,R.drawable.bussiness_car)
        tripLists = mutableMapOf()
        tripLists["6 Июля, Вторник"]=list
        tripLists["5 Июля, Вторник"]=list

        tripsRvAdapter = TripsRvAdapter(tripLists)
        binding.tripsRv.adapter = tripsRvAdapter


        binding.tripsToolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.homePage,null,NavOption.getNavOptions())
        }
    }



}