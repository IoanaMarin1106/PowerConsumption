package com.example.powerconsumptionapp.cpuinfo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.*


class ItemAdapter(
    private val itemsList: List<GridItem>
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private  lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cpu_row_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = itemsList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            leftCpuCoreNumber.text = itemsList[position].leftCpuCoreNumber
            leftCpuCoreFrequency.text = itemsList[position].leftCpuCoreFrequency
            leftCpuProgressBar.setProgress(itemsList[position].leftCpuCoreUsage)
            "${itemsList[position].leftCpuCoreUsage}%".also { leftCpuCoreUsage.text = it }

            rightCpuCoreNumber.text = itemsList[position].rightCpuCoreNumber
            rightCpuCoreFrequency.text = itemsList[position].rightCpuCoreFrequency
            rightCpuProgressBar.setProgress(itemsList[position].rightCpuCoreUsage)
            "${itemsList[position].rightCpuCoreUsage}%".also { rightCpuCoreUsage.text = it }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val leftCpuCoreNumber: TextView = itemView.findViewById(R.id.leftCpuCoreNumber)
        val leftCpuCoreFrequency: TextView = itemView.findViewById(R.id.leftCpuCoreFrequency)
        val leftCpuCoreUsage: TextView = itemView.findViewById(R.id.leftCpuCoreUsage)

        val rightCpuCoreNumber: TextView = itemView.findViewById(R.id.rightCpuCoreNumber)
        val rightCpuCoreFrequency: TextView = itemView.findViewById(R.id.rightCpuCoreFrequency)
        val rightCpuCoreUsage: TextView = itemView.findViewById(R.id.rightCpuCoreUsage)

        val leftCpuProgressBar: TextRoundCornerProgressBar = itemView.findViewById(R.id.leftCpuProgressBar)
        val rightCpuProgressBar: TextRoundCornerProgressBar = itemView.findViewById(R.id.rightCpuProgressBar)
    }
}