package com.example.mytaxi.utils

import androidx.navigation.NavOptions
import com.example.mytaxi.R

class NavOption {
    companion object{

        fun getNavOptions(): NavOptions {
            return NavOptions.Builder()
                .setEnterAnim(R.anim.enter)
                .setExitAnim(R.anim.exit)
                .setPopEnterAnim(R.anim.pop_enter)
                .setPopExitAnim(R.anim.pop_exit)
                .build()
        }
    }
}