package com.example.waterwise.ui.home

import WaterUsageAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.waterwise.R
import com.example.waterwise.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var waterUsageAdapter: WaterUsageAdapter
    private lateinit var waterUsageRecords: MutableList<WaterUsageRecord>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val userNameTextView: TextView = binding.userNameHeader
        val userName = "John Doe"
        userNameTextView.text = userName

        val carouselImages = listOf(R.drawable.image1, R.drawable.image1, R.drawable.image3)
        if (carouselImages.isNotEmpty()){
            Log.d("HomeFragment", "Carousel images loaded")
        } else {
            Log.d("HomeFragment", "No images loaded")
        }
        val carouselAdapter = CarouselAdapter(carouselImages)
        binding.carouselViewpager.adapter = carouselAdapter

        binding.button1Container.setOnClickListener{
            showModal("Button 1 clicked")
        }

        waterUsageRecords = generateSimulatedData().toMutableList()

        val recyclerView: RecyclerView = binding.recyclerViewUsage
        recyclerView.layoutManager = LinearLayoutManager(context)
        waterUsageAdapter = WaterUsageAdapter(waterUsageRecords)
        recyclerView.adapter = waterUsageAdapter

        val sortButton : Button = binding.root.findViewById(R.id.sort_button)
        sortButton.setOnClickListener{
            sortWaterUsageData()
        }

        return root
    }

    private fun showModal(message:String){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Info")
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss()}
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateSimulatedData(): List<WaterUsageRecord> {
        val records = mutableListOf<WaterUsageRecord>()
        val dates = arrayOf("2024-09-01", "2024-09-02", "2024-09-03", "2024-09-04", "2024-09-05", "2024-09-06", "2024-09-07", "2024-09-08", "2024-09-09", "2024-09-10",
            "2024-09-11", "2024-09-12", "2024-09-13", "2024-09-14", "2024-09-15",
            "2024-09-16", "2024-09-17", "2024-09-18", "2024-09-19", "2024-09-20",
            "2024-09-21", "2024-09-22", "2024-09-23", "2024-09-24", "2024-09-25",
            "2024-09-26", "2024-09-27")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault()) // Full day name

        for (date in dates) {
            val usage = (300..1000).random().toDouble()
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(date)!!
            val dayOfWeek = dayFormat.format(calendar.time)
            records.add(WaterUsageRecord(date, usage.toString(), dayOfWeek))
        }

        return records
    }
    private fun sortWaterUsageData() {
        waterUsageRecords.sortByDescending { it.usage }
        waterUsageAdapter.notifyDataSetChanged()
    }


}
