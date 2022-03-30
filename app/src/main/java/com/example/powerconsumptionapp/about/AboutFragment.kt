package com.example.powerconsumptionapp.about

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.databinding.DataBindingUtil
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentAboutBinding
import android.view.animation.DecelerateInterpolator




class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_about,
            container,
            false
        )

        return binding.root
    }
}