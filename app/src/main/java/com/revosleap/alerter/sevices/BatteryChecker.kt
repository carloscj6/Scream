package com.revosleap.alerter.sevices

import android.app.Service
import android.content.*
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.BatteryManager
import android.os.Binder
import android.os.IBinder
import com.revosleap.alerter.MainActivity
import com.revosleap.alerter.R
import com.revosleap.alerter.interfaces.Charge

class BatteryChecker : Service() {
    private val binder = ServiceBinder()
    private var itsCharge: Charge? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        registerReceiver(chargeReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        hideIcon()
        return START_STICKY
    }

    fun setCharge(charge: Charge) {
        itsCharge = charge
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class ServiceBinder : Binder() {
        val batteryChecker: BatteryChecker
            get() = this@BatteryChecker
    }

    private val chargeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val audioManager = context?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            itsCharge?.chargePercentage(level)
            if (level!! <= 20 && !isPowerConnected(context)) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0
                )
                val player = MediaPlayer.create(context, R.raw.screaming_goat)
                player.start()
            }
        }

    }

    fun isPowerConnected(context: Context): Boolean {
        val intentBatt = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val plugged = intentBatt?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }

    private fun hideIcon() {
        val componentName = ComponentName(this, MainActivity::class.java)
        packageManager.setComponentEnabledSetting(
            componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}
