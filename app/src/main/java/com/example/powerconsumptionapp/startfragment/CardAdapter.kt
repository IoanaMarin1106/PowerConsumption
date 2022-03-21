package com.example.powerconsumptionapp.startfragment

import android.content.Context
import android.media.Image
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.CardLayoutBinding
import kotlin.coroutines.coroutineContext


class CardAdapter(
    private val buttons: List<ButtonsInfo>
) : RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardAdapter.ViewHolder, position: Int) {
        holder.cardTitle.text = buttonsList[position].buttonTitle
        holder.cardIcon.setImageResource(buttonsList[position].buttonImage)


        holder.view.setOnClickListener {
            Toast.makeText(holder.view.context, "Item clicked ->" + buttonsList[position].buttonTitle, Toast.LENGTH_LONG).show()
        }
    }

    override fun getItemCount(): Int = buttons.size

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val cardTitle: TextView = itemView.findViewById(R.id.title)
        val cardIcon: ImageView = itemView.findViewById(R.id.icon)
        val view = itemView
    }
}