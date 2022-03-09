package com.example.mytaxi.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mytaxi.databinding.OneTripBinding

class ItemRvAdapter(val itemList: ArrayList<Int>,val listener:OnItemTouchClickListener):RecyclerView.Adapter<ItemRvAdapter.MyItemViewHolder>() {

    inner class MyItemViewHolder(val oneTipBinding: OneTripBinding):RecyclerView.ViewHolder(oneTipBinding.root){
        fun onBind(item:Int){

            oneTipBinding.apply {
                carImg.setImageResource(item)
            }

            oneTipBinding.itemCardView.setOnClickListener {
                listener.onSingleItemClick()
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        return MyItemViewHolder(OneTripBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

}