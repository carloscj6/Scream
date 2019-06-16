package com.revosleap.alerter.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.revosleap.alerter.sevices.BatteryChecker

class BootCompletion : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val batteryChecker = Intent(context, BatteryChecker::class.java)
        context.startService(batteryChecker)
    }
}
