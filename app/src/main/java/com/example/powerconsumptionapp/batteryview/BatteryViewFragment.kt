package com.example.powerconsumptionapp.batteryview

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import com.google.android.material.tabs.TabLayout


class BatteryViewFragment() : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()
    private lateinit var pagerAdapter: BatteryPagerAdapter
    private lateinit var viewPager: ViewPager

    private var screenWidth: Int = 0

    init {
        screenWidth = Resources.getSystem().displayMetrics.widthPixels / 2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_battery_view,
            container,
            false
        )

        // Set up the ViewPager with the sections adapter
        viewPager = binding.batteryViewPager

        //Create the adapter that will return a fragment for each of the three primary
        // sections of the fragment.
        pagerAdapter = BatteryPagerAdapter(fragmentManager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = binding.batteryTabs
        tabLayout.setupWithViewPager(viewPager)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            batteryFragment = this@BatteryViewFragment
            viewModel = batteryViewModel
        }
    }

    class BatteryPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = InformationFragment.newInstance()
                1 -> fragment = OptimizerFragment.newInstance()
                2 -> fragment = StatisticsFragment.newInstance()
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Information"
                1 -> return "Optimizer"
                2 -> return "Statistics"
            }
            return null
        }
    }

}


