package com.robocon.onvif.demo

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.robocon.onvif.R
import com.robocon.onvif.onvifcamera.*

import com.robocon.onvif.onvifcamera.OnvifRequest.Type.GetStreamURI
import com.robocon.onvif.onvifcamera.OnvifRequest.Type.GetProfiles
import com.robocon.onvif.onvifcamera.OnvifRequest.Type.GetDeviceInformation
import com.robocon.onvif.onvifcamera.OnvifRequest.Type.GetServices

const val RTSP_URL = "com.rvirin.onvif.onvifcamera.demo.RTSP_URL"

/*
  Main activity of this project. It allows the user to type his camera IP address,
  Username and Password.
 */
class MainActivity : AppCompatActivity(), OnvifListener {

    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun requestPerformed(response: OnvifResponse) {

        Log.d("INFO", response.parsingUIMessage)

        toast?.cancel()

        if (!response.success) {
            Log.e("ERROR", "Request Failed: ${response.request.type} \n Response: ${response.error}")
            toast = Toast.makeText(this, "Request Failed: ${response.request.type}", Toast.LENGTH_SHORT)
            toast?.show()
        }
        // if GetServices have been completed, we request the device information
            else if (response.request.type == GetServices) {
            currentDevice.getDeviceInformation()
        }
        // if GetDeviceInformation have been completed, we request the profiles
        else if (response.request.type == GetDeviceInformation) {

            val textView = findViewById<TextView>(R.id.explanationTextView)
            textView.text = response.parsingUIMessage
            toast = Toast.makeText(this, "Device information Retrieved", Toast.LENGTH_SHORT)
            toast?.show()

            currentDevice.getProfiles()

        }
        // if GetProfiles have been completed, we request the Stream URI
        else if (response.request.type == GetProfiles) {
            val profilesCount = currentDevice.mediaProfiles.count()
            toast = Toast.makeText(this, "$profilesCount Profiles Retrieved", Toast.LENGTH_SHORT)
            toast?.show()

            currentDevice.getStreamURI()

        }
        // if GetStreamURI have been completed, we're ready to play the video
        else if (response.request.type == GetStreamURI) {

            val button = findViewById<TextView>(R.id.button)
            button.text = getString(R.string.Play)

            toast = Toast.makeText(this, "Stream URI Retrieved,\nReady for Streaming", Toast.LENGTH_SHORT)
            toast?.show()
        }
    }

    fun buttonClicked(view: View) {

        // If we were able to retrieve information from the camera, and if we have a rtsp uri,
        // We open StreamActivity and pass the rtsp URI
        if (currentDevice.isConnected) {
            currentDevice.rtspURI?.let { uri ->
                val intent = Intent(this, StreamActivity::class.java).apply {
                    putExtra(RTSP_URL, uri)
                }
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "RTSP URI haven't been Retrieved", Toast.LENGTH_SHORT).show()
            }
        } else {

            // get the information type by the user to create the Onvif device
            val ipAddress = (findViewById<EditText>(R.id.ipAddress)).text.toString()
            val login = (findViewById<EditText>(R.id.login)).text.toString()
            val password = (findViewById<EditText>(R.id.password)).text.toString()

            if (ipAddress.isNotEmpty() &&
                    login.isNotEmpty() &&
                    password.isNotEmpty()) {

                // Create ONVIF device with user inputs and retrieve camera informations
                currentDevice = OnvifDevice(ipAddress, login, password)
                currentDevice.listener = this
                currentDevice.getServices()

            } else {
                toast?.cancel()
                toast = Toast.makeText(this,
                        "Please Enter an IP Address Username and Password",
                        Toast.LENGTH_SHORT)
                toast?.show()
            }
        }
    }
}
