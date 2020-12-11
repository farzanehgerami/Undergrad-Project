package com.robocon.onvif.demo

import android.support.v7.app.AppCompatActivity
import android.Manifest
import android.telephony.SmsManager
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.pedro.vlc.VlcListener
import com.pedro.vlc.VlcVideoLibrary
import com.robocon.onvif.R
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/*
  This activity helps us to show the live stream of an ONVIF camera thanks to VLC library. and control the
  robot's movements using the navigation buttons.
 */
class StreamActivity : AppCompatActivity(), VlcListener, View.OnClickListener {
    private var vlcVideoLibrary: VlcVideoLibrary? = null
    var num: String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)
        //
        val surfaceView = findViewById<SurfaceView>(R.id.surfaceView)
        val bStartStop = findViewById<Button>(R.id.b_start_stop)
        //
        val up = findViewById<ImageView>(R.id.up)
        val down = findViewById<ImageView>(R.id.down)
        val left = findViewById<ImageView>(R.id.left)
        val right = findViewById<ImageView>(R.id.right)
        val getnum = findViewById<View>(R.id.edt_num) as EditText
        //
        val permissionlistener = object : PermissionListener {  //Getting Permission to send sms(in android 6 and onwards permissions for sms should be granted in app in run time too)
            override fun onPermissionGranted() {
                Toast.makeText(this@StreamActivity, "SMS Permission Granted", Toast.LENGTH_SHORT).show()
                up.setOnClickListener {
                    num = getnum.text.toString()
                    if(num.equals(""))
                        Toast.makeText(this@StreamActivity, "Enter Phone Number First!", Toast.LENGTH_SHORT).show()
                    else
                        sendSMS(num!!,"Forward")
                    // move("Forward")
                }
                down.setOnClickListener {
                    num = getnum.text.toString()
                    if(num.equals(""))
                        Toast.makeText(this@StreamActivity, "Enter Phone Number First!", Toast.LENGTH_SHORT).show()
                    else
                        sendSMS(num!!,"Backward")
                    // move("DBackward")
                }
                left.setOnClickListener {
                    num = getnum.text.toString()
                    if(num.equals(""))
                        Toast.makeText(this@StreamActivity, "Enter Phone Number First!", Toast.LENGTH_SHORT).show()
                    else
                        sendSMS(num!!,"Left")
                    // move("LEFT")
                }
                right.setOnClickListener {
                    num = getnum.text.toString()
                    if(num.equals(""))
                        Toast.makeText(this@StreamActivity, "Enter Phone Number First!", Toast.LENGTH_SHORT).show()
                    else
                        sendSMS(num!!,"Right")
                    // move("RIGHT")
                }
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                Toast.makeText(this@StreamActivity, "SMS Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        TedPermission.with(this@StreamActivity)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If You Reject Permission, You Can Not Use This Service\n\nPlease Turn On Permissions At [Setting] > [Permission]")
                .setPermissions(Manifest.permission.SEND_SMS)
                .check()
        //
        bStartStop.setOnClickListener(this)
        vlcVideoLibrary = VlcVideoLibrary(this, this, surfaceView)
    }
    private fun sendSMS(phoneNumber: String, message: String) {
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(phoneNumber, null, message, null, null)
    }

  /*  private fun move(mode: String) {
        val strReq = object : StringRequest(Request.Method.POST, "your api url", {
// Response Rreturns here

        }, { _ ->
            //Handle error here
        }) {
            override fun getBody(): ByteArray {
                //Configure Body Here if needed
                val params = HashMap<String, String>()
                params["test"] = "test"
                return JSONObject(params).toString().toByteArray()
            }


            override fun getBodyContentType(): String {
                return "application/json"
            }

            override fun getHeaders(): MutableMap<String, String> {
                //Configure header here if needed

                val headers = HashMap<String, String>()
                headers["test"] = "test"

                return headers
            }

        }
        AppController.getInstance(applicationContext).addToRequestQueue(strReq)
        /////////////////////////////////////////////////////////////////////////////

    }*//////// Uncomment this function and its instances and complete it for sending request to web service with provided API


     // Called by VLC library when the video is loading
    override fun onComplete() {
        Toast.makeText(this, "Loading video...", Toast.LENGTH_LONG).show()
    }
    // Called by VLC library when an error occured (most of the time, a problem in the URI)
    override fun onError() {
        Toast.makeText(this, "Error, make sure your endpoint is correct", Toast.LENGTH_SHORT).show()
        vlcVideoLibrary?.stop()
    }

    override fun onClick(v: View?) {

        vlcVideoLibrary?.let { vlcVideoLibrary ->

            if (!vlcVideoLibrary.isPlaying) {
                val url = intent.getStringExtra(RTSP_URL)
                vlcVideoLibrary.play(url)

            } else {
                vlcVideoLibrary.stop()

            }
        }
    }
}