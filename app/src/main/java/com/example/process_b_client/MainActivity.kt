package com.example.process_b_client

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.processs_a_musicplayer.IClientCallback
import com.example.processs_a_musicplayer.IMusicPlayer

class MainActivity : AppCompatActivity() {
    private  val TAG = "MainActivity"
    var startMusicbtn:Button? = null
    var stopMusicbtn:Button? = null
    var statusTextView:TextView? = null
    var airPlaneMode: TextView? = null

    var iMusicPlayer:IMusicPlayer? = null

    var callback = object : IClientCallback.Stub() {
        @SuppressLint("SetTextI18n")
        override fun onMessageReceived(msg: String?) {
            Log.d(TAG, "onMessageReceived: $msg on process B")
            runOnUiThread {
                run {
                    airPlaneMode?.text = "AirPlane mode status: ${if (msg?.contains("true") == true) "ON" else "OFF"}"
                }
            }
        }
    }

    var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
           iMusicPlayer = IMusicPlayer.Stub.asInterface(service)
            iMusicPlayer?.registerCallback(callback)
            Log.d(TAG, "onServiceConnected: Connection successful on Process B")
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            iMusicPlayer?.unregisterCallback(callback)
            iMusicPlayer = null
            Log.d(TAG, "onServiceDisconnected: Connection failed on Process B")
        }

    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         startMusicbtn = findViewById<Button>(R.id.startMusicBtn)
         stopMusicbtn = findViewById<Button>(R.id.stopMusicBtn)
         statusTextView = findViewById<TextView>(R.id.statusTextView)
        airPlaneMode = findViewById<TextView>(R.id.airPlaneModeTxV)


    }

    override fun onStart() {
        super.onStart()


    }
    override fun onResume() {
        super.onResume()

        val intent = Intent("MyAidlService")
        intent.setPackage("com.example.processs_a_musicplayer")
        bindService(intent, serviceConnection, BIND_AUTO_CREATE)


        val resolveInfo = packageManager.queryIntentServices(intent, 0)
        Log.d("AppA", "Found services: ${resolveInfo.size}")

        Log.d(TAG, "onResume: Requested to bind the service")

        statusTextView?.text = if (iMusicPlayer?.playerStatus == true) "Music player is playing..." else "Music player is stopped"
        stopMusicbtn?.setOnClickListener {
            iMusicPlayer?.stop()
            statusTextView?.text = if (iMusicPlayer?.playerStatus == true) "Music player is playing..." else "Music player is stopped"
        }

        startMusicbtn?.setOnClickListener {
            iMusicPlayer?.start()
            statusTextView?.text = if (iMusicPlayer?.playerStatus == true) "Music player is playing..." else "Music player is stopped"
        }
    }

    fun convertImplicitIntentToExplicitIntent(ct: Context, implicitIntent: Intent): Intent? {
        val pm = ct.packageManager
        val resolveInfoList = pm.queryIntentServices(implicitIntent, 0)
        if (resolveInfoList == null || resolveInfoList.size != 1) {
            return null
        }
        val serviceInfo = resolveInfoList[0]
        val component = ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name)
        val explicitIntent = Intent(implicitIntent)
        explicitIntent.component = component
        return explicitIntent
    }
}