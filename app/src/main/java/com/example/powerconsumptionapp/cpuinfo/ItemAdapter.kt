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
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.startfragment.CardAdapter
import com.example.powerconsumptionapp.startfragment.StarterFragmentDirections
import com.example.powerconsumptionapp.startfragment.Util
import com.example.powerconsumptionapp.startfragment.buttonsList


class ItemAdapter(
    private val itemsList: List<GridItem>,
    private val context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(itemsList.get(position))
    }
    override fun getItemCount(): Int = itemsList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        fun bind(gridItem: GridItem) {

        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }


    }

}