package com.example.waterwise

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.waterwise.databinding.ActivityRegisterBinding
import com.example.waterwise.ui.slideshow.Main

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private var imageUri: Uri? = null
    private lateinit var profileImageView : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileImageView = findViewById(R.id.profileImageView)

        val selectImageLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                profileImageView.setImageURI(imageUri)
            }
        }

        binding.profileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            selectImageLauncher.launch(intent)
        }

        binding.signUpButton.setOnClickListener {
            val name = binding.nameInput.text.toString()
            val email = binding.emailInput.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USERNAME", name)
                    putExtra("EMAIL", email)
                    putExtra("PROFILE_IMAGE_URI", imageUri.toString())
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginRedirect.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
