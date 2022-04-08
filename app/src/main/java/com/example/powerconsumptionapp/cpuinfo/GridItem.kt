package com.example.powerconsumptionapp.cpuinfo

var itemsList = mutableListOf<GridItem>()

data class GridItem (
    var leftCpuCoreNumber: Int,
    var leftCpuCoreFrequency: Int,
    var leftCpuCoreUsage: Int,
    var rightCpuCoreNumber: Int,
    var rightCpuCoreFrequency: Int,
    var rightCpuCoreUsage: Int,
    val id: Int? = itemsList.size
)