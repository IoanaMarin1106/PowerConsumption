package com.example.powerconsumptionapp.startfragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.powerconsumptionapp.R

class CardAdapter(
    private val buttons: List<ButtonsInfo>,
    private val parentFragment: StarterFragment
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    private  lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.ViewHolder, position: Int) {
        holder.cardTitle.text = buttonsList[position].buttonTitle
        holder.cardIcon.setImageResource(buttonsList[position].buttonImage)

        holder.view.setOnClickListener {
//            Toast.makeText(holder.view.context, "Item clicked ->" + buttonsList[position].buttonTitle, Toast.LENGTH_LONG).show()
            when(buttonsList[position].buttonTitle) {
                Util.Button.BATTERY_VIEW.title -> {
                    val action = StarterFragmentDirections.actionStarterFragmentToBatteryViewFragment()
                    NavHostFragment.findNavController(parentFragment).navigate(action)
                }
                Util.Button.CPU_INFO.title -> {
                    val action = StarterFragmentDirections.actionStarterFragmentToCPUInfo()
                    NavHostFragment.findNavController(parentFragment).navigate(action)
                }
                Util.Button.PERFORMANCE_MANAGER.title -> {
                    val action = StarterFragmentDirections.actionStarterFragmentToPerformanceManagerFragment()
                    NavHostFragment.findNavController(parentFragment).navigate(action)
                }
                Util.Button.SETTINGS.title -> Toast.makeText(holder.view.context, "nu am ecran de settings inca :)", Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun getItemCount(): Int = buttons.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.title)
        val cardIcon: ImageView = itemView.findViewById(R.id.icon)
        val view = itemView
    }
}