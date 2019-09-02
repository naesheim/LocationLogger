package com.example.naesheim.hansellogger

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView
import khttp.post
import org.jetbrains.anko.doAsync
import com.google.android.gms.location.LocationServices
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class MainActivity() : AppCompatActivity() {

    var apiURL = "https://us-central1-naesheim-home.cloudfunctions.net/add_new_location"

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button: Button = findViewById(R.id.button)
        checkPermission()

        button.setOnClickListener {
            sendRequest()
        }
    }

    @SuppressLint("MissingPermission")
    fun sendRequest() {
        var fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location ->
            val longitude = location.longitude
            val latitude = location.latitude

            doAsync {
                val payload = mapOf("longitude" to longitude, "latitude" to latitude, "time" to "now")
                val request = post(apiURL, data = payload)

                uiThread {
                    toast(request.text)
                }

            }

            val textView: TextView = findViewById(R.id.textview1)
            textView.text = "updated location"
        }
    }

    fun checkPermission(){
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }
}


