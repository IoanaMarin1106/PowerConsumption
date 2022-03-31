package com.example.powerconsumptionapp.batteryview

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.example.powerconsumptionapp.R
import com.example.powerconsumptionapp.databinding.FragmentBatteryViewBinding
import com.example.powerconsumptionapp.model.BatteryViewModel
import java.util.*
import kotlin.math.roundToInt


class BatteryViewFragment() : Fragment() {

    private lateinit var binding: FragmentBatteryViewBinding
    private val batteryViewModel: BatteryViewModel by activityViewModels()

    private var screenWidth: Int = 0

    init {
        screenWidth = Resources.getSystem().displayMetrics.widthPixels / 2
    }

    companion object {
        @JvmStatic var batteryStatus: Intent? = null
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            batteryFragment = this@BatteryViewFragment
            viewModel = batteryViewModel
        }

        // compute the battery intent
        getBatteryStatus()

        binding.apply {
            batteryInfoBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.VISIBLE,
                    View.GONE,
                    View.GONE
                )
            }

            batterySaverBttn.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.VISIBLE,
                    View.GONE
                )
            }

            batteryLevelStatisticsBttn?.setOnClickListener {
                batteryViewModel.containerHandler(
                    batteryInfoContainer!!,
                    batterySaverContainer!!,
                    levelStatisticsContainer!!,
                    View.GONE,
                    View.GONE,
                    View.VISIBLE
                )

            }

            batteryTempStatisticsBttn?.setOnClickListener {
                Toast.makeText(context, "aici aici", Toast.LENGTH_SHORT).show()
            }

            bttn4?.setOnClickListener {
                Toast.makeText(context, "shkbcdeshbfclsedhbcfesw", Toast.LENGTH_SHORT).show()
            }
        }

        // setup battery level indicator
        showBatteryInfo()
    }

    private fun showBatteryInfo() {
        batteryViewModel.showBatteryInfo()

        // Set progress bar
        batteryViewModel.batteryPct.apply {
            binding.tvBatteryPercentage.text = this.value.toString()
            binding.batteryLevelIndicator.progress = this.value!!
        }

        // Check if battery is charging or not
        if (batteryViewModel.chargingStatus.value == BatteryManager.BATTERY_STATUS_CHARGING) {
            binding.apply {
                chargingBattery.visibility = View.VISIBLE
                tvBatteryPercentage.visibility = View.GONE
            }
        } else {
            binding.apply {
                chargingBattery.visibility = View.GONE
                tvBatteryPercentage.visibility = View.VISIBLE
            }
        }
    }

    private fun getBatteryStatus() {
        BatteryViewFragment.batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context?.registerReceiver(null, it)
        }
    }

    private fun drawBatteryLevel() {
        val bitmap: Bitmap = createBitmap(700, 1000, Bitmap.Config.ARGB_8888)
        val canvas: Canvas = Canvas(bitmap)

        var shapeDrawable: ShapeDrawable

        //rectangle positions
        var left = 20
        var right = (screenWidth / 2) - left
        var top = 20
        var bottom = 400

        //draw rectangle shape to canvas
        shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.setBounds(left, top, right, bottom)
        var hexColor = "#${Integer.toHexString(ContextCompat.getColor(context!!, R.color.indigo_light))}"
        shapeDrawable.paint.color = Color.parseColor(hexColor)
        shapeDrawable.draw(canvas)

        //rectangle positions
        left = 20
        var shRight = (batteryViewModel.procentage.value?.times((right - left)))?.roundToInt()?.plus(left)
        top = 20
        bottom = 400

        Toast.makeText(context, "${right} + ${shRight} \n ${batteryViewModel.batteryPct.value} ", Toast.LENGTH_LONG).show()

        //draw rectangle shape to canvas
        shapeDrawable = ShapeDrawable(RectShape())
        shapeDrawable.setBounds(left, top, shRight!!, bottom)
        var hexColor2 = "#${Integer.toHexString(ContextCompat.getColor(context!!, R.color.indigo_dark))}"
        shapeDrawable.paint.color = Color.parseColor(hexColor2)
        shapeDrawable.draw(canvas)

        // bitmap hold the updated pixels & set bitmap as background to ImageView
//        binding.batteryPctImg.background = BitmapDrawable(resources, bitmap)
    }
}