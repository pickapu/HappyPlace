package com.example.happyplaces

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.happyplaces.databinding.ActivityAddHappyPlaceBinding

class AddHappyPlaceActivity : AppCompatActivity() {
    private lateinit var binding:ActivityAddHappyPlaceBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setSupportActionBar(binding.tbAddplace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.tbAddplace.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}