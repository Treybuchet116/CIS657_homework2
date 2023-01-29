package com.treybuchet.homework2_cis657

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.treybuchet.homework2_cis657.databinding.ActivityMainBinding
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnClear.setOnClickListener{
            binding.Lat1.text.clear()
            binding.Long1.text.clear()
            binding.Lat2.text.clear()
            binding.Long2.text.clear()
            binding.tvDistance.text = "Distance:"
            binding.tvBearing.text = "Bearing:"
        }

        binding.btnCalculate.setOnClickListener{
            if (binding.Lat1.text.isNotEmpty() and binding.Long1.text.isNotEmpty() and binding.Lat2.text.isNotEmpty() and binding.Long2.text.isNotEmpty()) {
                val lat1 = binding.Lat1.text.toString().toDouble()
                val long1 = binding.Long1.text.toString().toDouble()
                val lat2 = binding.Lat2.text.toString().toDouble()
                val long2 = binding.Long2.text.toString().toDouble()

                val df = DecimalFormat("#.###")
                df.roundingMode = RoundingMode.HALF_UP
                val distance = computeDistance(lat1, long1, lat2, long2)
                val bearing = computeBearing(lat1, long1, lat2, long2)

                val distanceText = "Distance:  " + df.format(distance).toString() + " km"
                val bearingText = "Bearing:  " + df.format(bearing).toString() + " degrees"
                binding.tvDistance.text = distanceText
                binding.tvBearing.text = bearingText
            }
            if (binding.Lat1.text.isEmpty()) {
                binding.Lat1.error = "Must be a number"
            }
            if (binding.Long1.text.isEmpty()) {
                binding.Long1.error = "Must be a number"
            }
            if (binding.Lat2.text.isEmpty()) {
                binding.Lat2.error = "Must be a number"
            }
            if (binding.Long2.text.isEmpty()) {
                binding.Long2.error = "Must be a number"
            }
        }
    }

    private fun toRadians(angleInDegrees:Double): Double {
        return (angleInDegrees * PI) / 180
    }
    private fun toDegrees(angleInRadians:Double): Double {
        return (angleInRadians * 180) / PI
    }
    private fun computeDistance(lat1:Double, long1:Double, lat2:Double, long2:Double): Double {
        val R = 6371
        val dLat = ((lat2 - lat1) * PI) / 180
        val dLong = ((long2 - long1) * PI) / 180
        val a = sin(dLat / 2) * sin(dLat / 2) + cos((lat1 * PI) / 180) *
                cos((lat2 * PI) / 180) * sin(dLong / 2) * sin(dLong / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val d = R * c
        return d
    }

    private fun computeBearing(lat1:Double, long1:Double, lat2:Double, long2:Double): Double {
        val startLat = toRadians(lat1)
        val startLong = toRadians(long1)
        val destLat = toRadians(lat2)
        val destLong = toRadians(long2)

        val y = sin(destLong - startLong) * cos(destLat)
        val x = cos(startLat) * sin(destLat) - sin(startLat) * cos(destLat) *
                cos(destLong - startLong)
        val bearing = atan2(y, x)
        return ((bearing + 360) % 360)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val ret = super.dispatchTouchEvent(ev)
        ev?.let { event ->
            if (event.action == MotionEvent.ACTION_UP) {
                currentFocus?.let { view ->
                    if (view is EditText) {
                        val touchCoordinates = IntArray(2)
                        view.getLocationOnScreen(touchCoordinates)
                        val x: Float = event.rawX + view.getLeft() - touchCoordinates[0]
                        val y: Float = event.rawY + view.getTop() - touchCoordinates[1]
                        if (x < view.getLeft() || x >= view.getRight() || y < view.getTop() || y > view.getBottom()) {
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(view.windowToken, 0)
                            view.clearFocus()
                        }
                    }
                }
            }
        }
        return ret
    }



}