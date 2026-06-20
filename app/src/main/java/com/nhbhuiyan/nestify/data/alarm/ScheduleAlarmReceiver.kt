package com.nhbhuiyan.nestify.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ScheduleAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getLongExtra("EXTRA_SCHEDULE_ID", -1L)
        val message = intent.getStringExtra("EXTRA_MESSAGE") ?: "Schedule Alarm"

        if (scheduleId != -1L) {
            val serviceIntent = Intent(context, AlarmService::class.java).apply {
                putExtra("EXTRA_SCHEDULE_ID", scheduleId)
                putExtra("EXTRA_MESSAGE", message)
            }
            context.startForegroundService(serviceIntent)
        }
    }
}
