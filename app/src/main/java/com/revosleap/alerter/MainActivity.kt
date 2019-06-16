package com.revosleap.alerter

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.revosleap.alerter.interfaces.Charge
import com.revosleap.alerter.sevices.BatteryChecker
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Charge {
    private var batteryChecker: BatteryChecker? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            batteryChecker = (service as BatteryChecker.ServiceBinder).batteryChecker
            batteryChecker?.setCharge(this@MainActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        bindCharge()
    }

    override fun onPause() {
        super.onPause()
        unbindService(serviceConnection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindCharge()
        val chargeIntent = Intent(this@MainActivity, BatteryChecker::class.java)
        startService(chargeIntent)


    }

    @SuppressLint("SetTextI18n")
    override fun chargePercentage(percentage: Int?) {
        textViewPercentage?.text ="${percentage!!}%"
        Log.w("percentage",percentage.toString())
    }

    private fun bindCharge() {
        val chargeIntent = Intent(this@MainActivity, BatteryChecker::class.java)
        bindService(chargeIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }


}
