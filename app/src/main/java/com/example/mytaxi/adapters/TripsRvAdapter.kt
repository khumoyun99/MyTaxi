package com.example.mytaxi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaxi.R
import com.example.mytaxi.databinding.ItemTripsBinding
import com.example.mytaxi.utils.NavOption

class TripsRvAdapter(val tripsList:MutableMap<String,ArrayList<Int>>):
    RecyclerView.Adapter<TripsRvAdapter.MyTripsViewHolder>() {


    inner class MyTripsViewHolder(val itemTripsBinding: ItemTripsBinding):RecyclerView.ViewHolder(itemTripsBinding.root){

        fun onBind(item1:MutableMap<String,ArrayList<Int>>,position: Int){

            val keysList = ArrayList(item1.keys)


            val itemRvAdapter = ItemRvAdapter(item1[keysList[position]]?: arrayListOf(),object :OnItemTouchClickListener{
                override fun onSingleItemClick() {
                    itemView.findNavController().navigate(R.id.singleTripPage,null, NavOption.getNavOptions())
                }
            })
            itemTripsBinding.itemRv.adapter = itemRvAdapter
            itemTripsBinding.dataTv.text = keysList[position]

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTripsViewHolder {
        return MyTripsViewHolder(ItemTripsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyTripsViewHolder, position: Int) {
        holder.onBind(tripsList,position)
    }

    override fun getItemCount(): Int {
        return tripsList.keys.size
    }

}