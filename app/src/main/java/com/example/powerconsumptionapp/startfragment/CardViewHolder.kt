package com.example.powerconsumptionapp.startfragment

import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.powerconsumptionapp.databinding.CardLayoutBinding

class CardViewHolder(
    private val cardCellBinding: CardLayoutBinding
) : RecyclerView.ViewHolder(cardCellBinding.root) {

    fun bindButton(button: ButtonsInfo): CardView {
        cardCellBinding.icon.setImageResource(button.buttonImage)
        cardCellBinding.title.text = button.buttonTitle
        return cardCellBinding.buttonView
    }
}