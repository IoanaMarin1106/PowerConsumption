package com.example.powerconsumptionapp.cpuinfo

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentCPUInfoBinding
import com.example.powerconsumptionapp.model.CPUViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class CPUInfoFragment : Fragment() {

    private lateinit var binding: FragmentCPUInfoBinding
    private val cpuViewModel: CPUViewModel by activityViewModels()
    private lateinit var pagerAdapter: CPUPagerAdapter
    private lateinit var viewPager: ViewPager
    private var selectedTab = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate (
            inflater,
            R.layout.fragment_c_p_u_info,
            container,
            false
        )

        // Set up the ViewPager with the sections adapter
        viewPager = binding.cpuViewPager

        //Create the adapter that will return a fragment for each of the three primary
        // sections of the fragment.
        pagerAdapter = CPUPagerAdapter(childFragmentManager)
        viewPager.adapter = pagerAdapter

        val tabLayout: TabLayout = binding.cpuTabs
        tabLayout.setupWithViewPager(viewPager)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            cpuFragment = this@CPUInfoFragment
            viewModel = cpuViewModel

            cpuTabs.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    selectedTab = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })

            fab.setOnClickListener {
                val dialogText = when (selectedTab) {
                    0 -> getString(R.string.help_text)
                    1 -> "Text aici pt statistics"
                    else -> {getString(R.string.help_text)}
                }
                cpuViewModel.showDialog((activity as MainActivity), getString(R.string.help), dialogText)
            }
        }
    }

    class CPUPagerAdapter(fm: FragmentManager?) :
        FragmentPagerAdapter(fm!!) {
        override fun getItem(position: Int): Fragment {
            var fragment: Fragment? = null
            when (position) {
                0 -> fragment = CPUInformationFragment.newInstance()
                1 -> fragment = CPUStatisticsFragment.newInstance()
            }
            return fragment!!
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return "Information"
                1 -> return "Statistics"
            }
            return null
        }
    }
}