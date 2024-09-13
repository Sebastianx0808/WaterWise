package com.example.waterwise.ui.gallery

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.waterwise.R
import com.example.waterwise.databinding.FragmentGalleryBinding
import java.text.SimpleDateFormat
import java.util.*

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val CHANNEL_ID = "leak_detection_channel"
    private val REQUEST_CODE_PERMISSION = 1001

    private lateinit var loggerLayout: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    private var logCount = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val imageView: ImageView = root.findViewById(R.id.image_view)
        val imageContainer: CardView = root.findViewById(R.id.image_container)

        loggerLayout = root.findViewById(R.id.logger_linear_layout)

        imageContainer.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        imageContainer.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER

        // Initialize leak detection simulation
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            simulateLeakDetection()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_PERMISSION)
        }

        simulateIoLogger()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null) // Stop the handler when the view is destroyed
    }

    private fun simulateLeakDetection() {
        val notificationRunnable = Runnable {
            showNotification("High Severity Water Leakage Detected!")
            handler.postDelayed({
                hideNotification()
            }, 20000) // Hide notification after 20 seconds
        }
        handler.postDelayed(notificationRunnable, 500000) // Trigger after 5 minutes
    }

    private fun showNotification(message: String) {
        createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // Replace with your notification icon
            .setContentTitle("Water Leakage Alert")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        NotificationManagerCompat.from(requireContext()).notify(1, notificationBuilder.build())
    }

    private fun hideNotification() {
        NotificationManagerCompat.from(requireContext()).cancel(1)
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Leak Detection Channel"
            val descriptionText = "Channel for leak detection notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                simulateLeakDetection()
            } else {
                Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simulateIoLogger() {
        val loggerRunnable = object : Runnable {
            override fun run() {
                // Generate log data
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val sensorId = UUID.randomUUID().toString().substring(0, 8)
                val pipeJunctionNumber = (1..5).random()
                val pipeInletNumber = (1..3).random()
                val sensorLocation = listOf("Bathroom", "Kitchen", "Living Room").random()

                // Create a new TextView for the log entry
                val logEntry = TextView(requireContext()).apply {
                    text = "Timestamp: $timestamp\nSensor ID: $sensorId\nPipe Junction: $pipeJunctionNumber\n" +
                            "Pipe Inlet: $pipeInletNumber\nLocation: $sensorLocation\n"
                    setPadding(16, 16, 16, 16)
                }

                // Add the log entry to the logger layout
                loggerLayout.addView(logEntry)

                // Scroll to the latest log in the ScrollView
                binding.loggerScrollView.post {
                    binding.loggerScrollView.fullScroll(View.FOCUS_DOWN)
                }

                logCount++

                // Randomly trigger a pipe leakage error after 4 logs
                if (logCount >= 8 && (0..10).random() > 7) {
                    showNotification("Pipe Leakage Detected at Junction $pipeJunctionNumber!")
                    logCount = 0 // Reset after error
                }

                // Schedule the next log after 3 seconds
                handler.postDelayed(this, 3000)
            }
        }

        handler.post(loggerRunnable) // Start the logging
    }

}
