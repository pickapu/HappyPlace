package com.example.happyplaces.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.happyplaces.activity.AddHappyPlaceActivity
import com.example.happyplaces.adapters.ItemAdapter
import com.example.happyplaces.database.DatabaseHandler
import com.example.happyplaces.database.HappyPlaceModel
import com.example.happyplaces.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.fabAddHappyPlace.setOnClickListener {
            val intent= Intent(this, AddHappyPlaceActivity::class.java)
            startActivityForResult(intent,ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlaceListFromLocalDB()
    }
    companion object{
       var ADD_PLACE_ACTIVITY_REQUEST_CODE=1
    }
    private fun setupHappyplaceRecyclerView(happyPlaceList:ArrayList<HappyPlaceModel>){
        binding.rvHappyPlace.layoutManager=LinearLayoutManager(this)
        val placeAdapter=ItemAdapter(this,happyPlaceList)
        binding.rvHappyPlace.setHasFixedSize(true)
        binding.rvHappyPlace.adapter=placeAdapter
    }
    private fun getHappyPlaceListFromLocalDB(){
        val dbHandler=DatabaseHandler(this)
        val getHappyPlaceList:ArrayList<HappyPlaceModel> =dbHandler.getHappyPlacesList()
        if(getHappyPlaceList.size>0){
            binding.rvHappyPlace.visibility=View.VISIBLE
            binding.tvNoRecords.visibility=View.GONE
            setupHappyplaceRecyclerView(getHappyPlaceList)
        }else{
            binding.rvHappyPlace.visibility=View.GONE
            binding.tvNoRecords.visibility=View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode==Activity.RESULT_OK){
                getHappyPlaceListFromLocalDB()
            }else{
                Log.e("ActivityMain","canceled")
            }
        }
    }
}