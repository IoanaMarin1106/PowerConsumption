package com.example.powerconsumptionapp.startfragment

var buttonsList = mutableListOf<ButtonsInfo>()

data class ButtonsInfo(
    var buttonTitle: String,
    var buttonImage: Int,
    val id: Int? = buttonsList.size
)