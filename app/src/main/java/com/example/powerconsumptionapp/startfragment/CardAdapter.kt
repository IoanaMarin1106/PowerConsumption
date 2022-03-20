package com.example.powerconsumptionapp.startfragment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.powerconsumptionapp.databinding.CardLayoutBinding
import kotlin.coroutines.coroutineContext


class CardAdapter(
    private val buttons: List<ButtonsInfo>,
) : RecyclerView.Adapter<CardViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(from, parent, false)
        context = parent.context
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val buttonView: CardView = holder.bindButton(buttons[position])

        holder.itemView.setOnClickListener(View.OnClickListener {
            @Override
            fun onClick(v: View) {
                Toast.makeText(context, "${buttonView.id}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun getItemCount(): Int = buttons.size



}