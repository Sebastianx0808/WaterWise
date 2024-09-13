package com.example.waterwise.ui.tips

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.waterwise.R
import com.example.waterwise.databinding.FragmentTipsBinding

class ConservationFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    private val waterTips = arrayOf(
        "Turn off the tap while brushing your teeth to save water.",
        "Use a bucket instead of a hose when washing your car.",
        "Install water-saving showerheads and faucets.",
        "Collect rainwater for gardening and other non-potable uses.",
        "Fix leaky faucets to prevent wasting water.",
        "Run your dishwasher and laundry machine only when full.",
        "Reduce water use by using a broom instead of a hose to clean driveways."
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val conservationViewModel = ViewModelProvider(this)[ConservationViewModel::class.java]
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up the image buttons with water conservation tips
        val tipButtons = arrayOf(
            binding.tip1,
            binding.tip2,
            binding.tip3,
            binding.tip4,
            binding.tip5,
            binding.tip6,
            binding.tip7
        )

        for (i in tipButtons.indices) {
            tipButtons[i].setOnClickListener {
                showWaterTipDialog(waterTips[i])
            }
        }

        // Load YouTube videos into WebView
        val webView1 = binding.videoWebView1
        val webView2 = binding.videoWebView2
        val webView3 = binding.videoWebView3
        val webView4 = binding.videoWebView4

        loadYouTubeVideo(webView1, "dsW6vFRqd2E")
        loadYouTubeVideo(webView2, "GVmBWQ7Zrzk")
        loadYouTubeVideo(webView3, "WUap2lD24zU")
        loadYouTubeVideo(webView4, "SYPFon69vKs")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadYouTubeVideo(webView: WebView, videoId: String) {
        webView.settings.javaScriptEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.webViewClient = WebViewClient()

        val embedHTML = """
            <iframe width="100%" height="100%" src="https://www.youtube.com/embed/$videoId" 
            frameborder="0" allowfullscreen></iframe>
        """.trimIndent()

        webView.loadData(embedHTML, "text/html", "utf-8")
    }

    private fun showWaterTipDialog(tip: String) {
        // Create a dialog instance
        val dialog = Dialog(requireContext())

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.dialog_tip)

        // Find the TextView and Button from the dialog layout
        val tipTextView = dialog.findViewById<TextView>(R.id.tipText)
        val closeButton = dialog.findViewById<Button>(R.id.closeButton)

        // Set the water conservation tip text
        tipTextView.text = tip

        // Close the dialog when the button is clicked
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        // Show the dialog
        dialog.show()
    }
}
