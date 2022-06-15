package com.example.powerconsumptionapp.about

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.powerconsumptionapp.MainActivity
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.ChoiceChipBinding
import com.example.powerconsumptionapp.databinding.FragmentAboutBinding
import com.example.powerconsumptionapp.general.Util
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding

    private val batteryChipsList = arrayListOf("Battery Health", "Temperature", "Optimum Temperature", "App Standby Bucket", "Doze mode", "Screen Timeout")
    private val cpuChipsList = arrayListOf("CPU", "Frequency", "Governor", "CPU Load", "Core")
    private val governorsList = arrayListOf("Performance", "Powersave", "Schedutil", "Conservative", "Userspace", "Ondemand")
    private val appStandbyBucketList = arrayListOf("Active", "Working set", "Frequent", "Rare", "Restricted")
    private val tipsList = arrayListOf("How to charge your battery?", "Don't use device while charging", "Avoid idle charging", "Tip to use fast charging", "Only charge when your battery gets to 50%",
                                    "Restart or reboot your device", "Use the correct charger")

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            helperCard.setOnClickListener {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.helper_title), getString(R.string.helper_message))
            }

            // Add battery chips and CPU chips
            addChips(batteryChipGroup, cpuChipGroup, governorsChipGroup, appStandbyBucketChipGroup, tipsAndTricksChipGroup)

            batteryInformationButton.setOnClickListener {
                chipHandler(it as MaterialButton, batteryChipGroup)
            }

            cpuInformationButton.setOnClickListener {
                chipHandler(it as MaterialButton, cpuChipGroup)
            }

            cpuGovernorsInformationButton.setOnClickListener {
                chipHandler(it as MaterialButton, governorsChipGroup)
            }

            appStandbyBucketInformationButton.setOnClickListener {
                chipHandler(it as MaterialButton, appStandbyBucketChipGroup)
            }

            tipsAndTricksButton.setOnClickListener {
                chipHandler(it as MaterialButton, tipsAndTricksChipGroup)
            }

            showCommandsButton.setOnClickListener {
                copyCommands(commandsContainer, it as MaterialButton)
            }

            readCommandButton.setOnClickListener {
                Util.setClipboard(requireContext(), getString(R.string.read_command))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            writeCommandButton.setOnClickListener {
                Util.setClipboard(requireContext(), getString(R.string.write_command))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyCommands(
        commandsContainer: LinearLayout,
        showCommandsButton: MaterialButton,
    ) {
        if (commandsContainer.visibility == View.GONE) {
            commandsContainer.visibility = View.VISIBLE
            showCommandsButton.background.alpha = 0
        } else {
            commandsContainer.visibility = View.GONE
            showCommandsButton.background.alpha = 255
        }
    }

    private fun addChips(
        batteryChipGroup: ChipGroup,
        cpuChipGroup: ChipGroup,
        governorsChipGroup: ChipGroup,
        appStandbyBucketChipGroup: ChipGroup,
        tipsAndTricksChipGroup: ChipGroup
    ) {
        for (chipName in batteryChipsList) {
            batteryChipGroup.addView(createChip(chipName))
        }

        for (chipName in cpuChipsList) {
            cpuChipGroup.addView(createChip(chipName))
        }

        for (chipName in governorsList) {
            governorsChipGroup.addView(createChip(chipName))
        }

        for (chipName in appStandbyBucketList) {
            appStandbyBucketChipGroup.addView(createChip(chipName))
        }

        for (chipName in tipsList) {
            tipsAndTricksChipGroup.addView(createChip(chipName))
        }
    }

    private fun chipHandler(materialButton: MaterialButton, chipGroup: ChipGroup) {
        if (chipGroup.visibility == View.GONE) {
            chipGroup.visibility = View.VISIBLE
            materialButton.background.alpha = 0
        } else {
            chipGroup.visibility = View.GONE
            materialButton.background.alpha = 255
        }
    }

    private fun createChip(chipTitle: String): Chip {
        val chip = ChoiceChipBinding.inflate(layoutInflater).root
        chip.apply {
            chipTitle.also { text = it }
            setTextAppearanceResource(R.style.ChipTextStyle)
            chipBackgroundColor = ContextCompat.getColorStateList(requireContext(), R.color.deep_purple_dark)
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    chipClickHandler(chipTitle)
                }
            }
        }
        return chip
    }

    private fun chipClickHandler(chipTitle: String) {
        when (chipTitle) {
            getString(R.string.battery_health) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.battery_health), getString(R.string.battery_health_info))
            }

            getString(R.string.temperature) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.temperature), getString(R.string.temperature_info))
            }

            getString(R.string.optimum_battery_temp) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.optimum_battery_temp), getString(R.string.optimum_battery_temp_info))

            }

            getString(R.string.app_standby_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.app_standby_bucket), getString(R.string.app_standby_bucket))
            }

            getString(R.string.doze_mode_title) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.doze_mode_title), getString(R.string.doze_mode_information))
            }

            getString(R.string.screen_timeout) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.screen_timeout), getString(R.string.screen_timeout_info))
            }

            getString(R.string.frequency) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.frequency), getString(R.string.frequency_info))
            }

            getString(R.string.governor) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.governor), getString(R.string.governor_info))
            }

            getString(R.string.cpu_load) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.cpu_load), getString(R.string.cpu_load_info))
            }

            getString(R.string.core) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.core), getString(R.string.core_info))
            }

            getString(R.string.cpu) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.cpu), getString(R.string.cpu_info))
            }

            getString(R.string.conservative) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.conservative), getString(R.string.conservative_info))
            }

            getString(R.string.powersave) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.powersave), getString(R.string.powersave_info))
            }

            getString(R.string.schedutil) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.schedutil), getString(R.string.schedutil_info))
            }

            getString(R.string.performance) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.performance), getString(R.string.performance_info))
            }

            getString(R.string.ondemand) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.ondemand), getString(R.string.ondemand_info))
            }

            getString(R.string.userspace) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.userspace), getString(R.string.userspace_info))
            }

            getString(R.string.active_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.active_bucket), getString(R.string.active_info))
            }

            getString(R.string.rare_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.rare_bucket), getString(R.string.rare_info))
            }

            getString(R.string.working_set_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.working_set_bucket), getString(R.string.working_set_info))
            }

            getString(R.string.frequent_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.frequent_bucket), getString(R.string.frequent_info))
            }

            getString(R.string.restricted_bucket) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.restricted_bucket), getString(R.string.restricted_info))
            }

            // Tips & Tricks
            getString(R.string.restart_device) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.restart_device), getString(R.string.restart_device_msg))
            }

            getString(R.string.how_to_charge_battery) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.how_to_charge_battery), getString(R.string.how_to_charge_battery_msg))
            }

            getString(R.string.do_not_use_device) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.do_not_use_device), getString(R.string.do_not_use_device_msg))
            }

            getString(R.string.idle_charging) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.idle_charging), getString(R.string.idle_charging_msg))
            }

            getString(R.string.only_charge_when) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.only_charge_when), getString(R.string.only_charge_when_msg))
            }

            getString(R.string.correct_charger) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.correct_charger), getString(R.string.correct_charger_msg))
            }

            getString(R.string.fast_charging) -> {
                Util.showDialog((requireActivity() as MainActivity), getString(R.string.fast_charging), getString(R.string.fast_charging_msg))
            }
        }
    }
}