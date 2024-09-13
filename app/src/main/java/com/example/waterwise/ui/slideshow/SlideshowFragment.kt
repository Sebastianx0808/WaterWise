package com.example.waterwise.ui.slideshow

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.waterwise.R
import com.example.waterwise.databinding.FragmentSlideshowBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private lateinit var weatherApiService: WeatherApiService
    private lateinit var soilData: SoilData
    val handler = Handler(Looper.getMainLooper())

    private lateinit var autoWateringSwitch: Switch
    private lateinit var locationEditText: EditText
    private lateinit var getLocationButton: Button
    private lateinit var sendLocationButton: Button
    private lateinit var weatherDetailsTextView: TextView
    private lateinit var expanderLayout: LinearLayout
    private lateinit var wateringConditions: TextView
    private lateinit var wateringSeekBar: SeekBar
    private lateinit var sprinklerSwitch: Switch

    private lateinit var jobNameEditText: EditText
    private lateinit var jobType: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var startWateringButton: Button
    private lateinit var manualWateringProgressBar: SeekBar



    private val sprinklerDuration = 10000 // 10 seconds for demo

    private var fetchCount = 0 // Counter variable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Initialize Retrofit
        weatherApiService = RetrofitClient.retrofitInstance.create(WeatherApiService::class.java)
        soilData = SoilDataGenerator.generateDummySoilData()

        // Initialize UI components
        locationEditText = binding.locationEditText
        getLocationButton = binding.getLocationButton
        sendLocationButton = binding.sendLocationButton
        weatherDetailsTextView = binding.weatherDetails
        autoWateringSwitch = binding.autoWateringSwitch
        expanderLayout = binding.expanderLayout
        wateringSeekBar = binding.wateringSeekBar
        sprinklerSwitch = binding.sprinklerSwitch

        jobNameEditText = binding.jobNameEditText
        jobType = binding.jobType
        timePicker = binding.timePicker
        startWateringButton = binding.startWateringButton
        manualWateringProgressBar = binding.manualWateringProgressBar



        getLocationButton.setOnClickListener {
            // Request location access permission and fetch location
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
        }

        sendLocationButton.setOnClickListener {
            fetchWeatherData(locationEditText.text.toString())
        }

        autoWateringSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                expanderLayout.visibility = View.VISIBLE
                fetchWeatherData(locationEditText.text.toString())
            } else {
                expanderLayout.visibility = View.GONE
                stopSprinkler()
            }
        }
        startWateringButton.setOnClickListener{
            val jobName = jobNameEditText.text.toString()
            val job = jobType.text.toString()
            val hours = timePicker.hour
            val minutes = timePicker.minute

            val totalSeconds = hours * 3600 + minutes *60
            startManualWatering(totalSeconds)
        }


        return root
    }

    private fun fetchLocation() {
        val location = "London"
        locationEditText.setText(location)
    }

    private fun fetchWeatherData(location: String) {
        val call = weatherApiService.getWeather(
            location,
            "51a375c193cb2a17ff0edcadf3d36ecd"
        ) // Replace with actual city and API key
        call.enqueue(object : Callback<WeatherResponse> {
            override fun onResponse(
                call: Call<WeatherResponse>,
                response: Response<WeatherResponse>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    val temperature = weatherResponse?.main?.temp ?: 0.0
                    val windspeed = weatherResponse?.wind?.speed ?: 0.0
                    val description = weatherResponse?.weather?.firstOrNull()?.description ?: "No data"

                    val weatherInfo = "Temperature: $temperature°C\n" +
                            "Wind Speed: $windspeed m/s\n" +
                            "Description: $description"

                    weatherDetailsTextView.text = weatherInfo



                    val soilMoisture = calculateSoilMositure(soilData)
                    val wateringIndex = calculateWateringIndex(temperature, soilMoisture, windspeed)
                    if (wateringIndex > 0.3) {
                        startSprinkler()
                    } else {
                        stopSprinkler()
                    }
                } else {
                    weatherDetailsTextView.text = "Error fetching weather data"
                }
            }

            private fun calculateSoilMositure(soilData: SoilData): Double {
                val soilMoistureThresholds = listOf(
                    200,
                    400,
                    600,
                    800
                ) // Adjust these values based on your specific soil sensor
                val soilMoistureValues = listOf(
                    0.0,
                    0.2,
                    0.5,
                    0.8,
                    1.0
                ) // Adjust these values based on your specific requirements

                val soilMoistureReading = soilData.moisture
                for (i in soilMoistureThresholds.indices) {
                    if (soilMoistureReading < soilMoistureThresholds[i]) {
                        return soilMoistureValues[i]
                    }
                }

                return soilMoistureValues.last()
            }

            override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                weatherDetailsTextView.text = "Failed to connect to weather API"
            }
        })
    }

    private fun calculateWateringIndex(temperature: Double, soilMoisture: Double,windSpeed: Double): Double {
        val temperatureFactor = calculateTemperatureFactor(temperature)
        val soilMoistureFactor = calculateSoilMoistureFactor(soilMoisture)

        val windSpeedFactor = calculateWindSpeedFactor(windSpeed)

        val weightingFactors = listOf(0.3, 0.4, 0.2, 0.1) // Adjust these values based on your specific requirements
        val factors = listOf(temperatureFactor, soilMoistureFactor, windSpeedFactor)

        var wateringIndex = 0.0
        for (i in factors.indices) {
            wateringIndex += factors[i] * weightingFactors[i]
        }

        return wateringIndex
    }

    private fun calculateTemperatureFactor(temperature: Double): Double {
        if (temperature < 15) {
            return 0.0 // Too cold
        } else if (temperature < 20) {
            return 0.2 // Cool
        } else if (temperature < 25) {
            return 0.5 // Ideal
        } else if (temperature < 30) {
            return 0.8 // Warm
        } else {
            return 1.0 // Hot
        }
    }

    private fun calculateSoilMoistureFactor(soilMoisture: Double): Double {
        if (soilMoisture < 20) {
            return 0.0 // Too dry
        } else if (soilMoisture < 40) {
            return 0.2 // Dry
        } else if (soilMoisture < 60) {
            return 0.5 // Ideal
        } else if (soilMoisture < 80) {
            return 0.8 // Moist
        } else {
            return 1.0 // Waterlogged
        }
    }


    private fun calculateWindSpeedFactor(windSpeed: Double): Double {
        if (windSpeed < 5) {
            return 0.0 // Too low
        } else if (windSpeed < 10) {
            return 0.2 // Low
        } else if (windSpeed < 15) {
            return 0.5 // Ideal
        } else if (windSpeed < 20) {
            return 0.8 // High
        } else {
            return 1.0 // Too high
        }
    }



    private fun startSprinkler() {
        Log.d("SlideshowFragment", "startSprinkler called")
        Toast.makeText(context, "Sprinkler ON", Toast.LENGTH_SHORT).show()
        sprinklerSwitch.isChecked = true
        wateringSeekBar.visibility = View.VISIBLE
        wateringSeekBar.progress = 0

        if (sprinklerDuration == 0) {
            Log.e("SlideshowFragment", "Sprinkler duration is zero!")
            return
        }

        wateringSeekBar.max = (sprinklerDuration / 1000).toInt()
        val startTime = System.currentTimeMillis()


        val runnable = object : Runnable {
            override fun run() {
                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                wateringSeekBar.progress = elapsedTime.toInt()
                if (elapsedTime < sprinklerDuration / 1000) {
                    // Use postDelayed with a weak reference to the runnable
                    handler.postDelayed(this, 1000)
                } else {
                    stopSprinkler()
                    // Remove the runnable from the message queue
                    handler.removeCallbacks(this)
                }
            }

        }
        handler.post(runnable)
    }

    private fun stopSprinkler() {
        Toast.makeText(context, "Sprinkler OFF", Toast.LENGTH_SHORT).show()
        // Remove the runnable from the message queue
        handler.removeCallbacksAndMessages(null)
        // Update the UI on the main thread
        activity?.runOnUiThread {
            wateringSeekBar.progress = 0
            wateringSeekBar.visibility = View.GONE
            sprinklerSwitch.isChecked = false
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun startManualWatering(durationInSeconds: Int) {
        Toast.makeText(context, "Manual Watering Started", Toast.LENGTH_SHORT).show()
        manualWateringProgressBar.progress = 0
        manualWateringProgressBar.max = 20
        val startTime = 0
        handler.post(object : Runnable {
            override fun run() {
                val elapsedTime = (System.currentTimeMillis() - startTime) / 1000
                val progress = (elapsedTime.toFloat() / durationInSeconds.toFloat() * manualWateringProgressBar.max).toInt()
                manualWateringProgressBar.progress = progress
                if (elapsedTime < durationInSeconds) {
                    handler.postDelayed(this, 1000)
                } else {
                    stopManualWatering()
                }
            }
        })
    }

    private fun stopManualWatering() {
        Toast.makeText(context, "Manual Watering Stopped", Toast.LENGTH_SHORT).show()
        manualWateringProgressBar.progress = 0
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
