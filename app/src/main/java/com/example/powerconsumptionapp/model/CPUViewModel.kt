package com.example.powerconsumptionapp.model

import android.widget.LinearLayout
import androidx.lifecycle.ViewModel

class CPUViewModel: ViewModel() {
    fun containerHandler(
        cpuInfoContainer: LinearLayout,
        statisticsContainer: LinearLayout,
        cpuInfoVisibility: Int,
        statisticsVisibility: Int
    ) {
        cpuInfoVisibility.also { cpuInfoContainer.visibility = it }
        statisticsVisibility.also { statisticsContainer.visibility = it }
    }
}