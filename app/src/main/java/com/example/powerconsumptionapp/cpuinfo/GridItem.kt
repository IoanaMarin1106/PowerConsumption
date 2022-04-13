package com.example.powerconsumptionapp.cpuinfo

var itemsList = mutableListOf<GridItem>()

data class GridItem (
    var leftCpuCoreNumber: String,
    var leftCpuCoreFrequency: String,
    var leftCpuCoreUsage: Int,
    var rightCpuCoreNumber: String,
    var rightCpuCoreFrequency: String,
    var rightCpuCoreUsage: Int,
    val id: Int? = itemsList.size
)