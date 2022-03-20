package com.example.powerconsumptionapp.startfragment

import android.widget.ImageView

var buttonsList = mutableListOf<ButtonsInfo>()

data class ButtonsInfo(
    var buttonTitle: String,
    var buttonImage: Int,
    val id: Int? = buttonsList.size
)