package com.nhbhuiyan.nestify.domain.alarm

import com.nhbhuiyan.nestify.domain.model.ScheduleItem

interface AlarmScheduler {
    fun schedule(item: ScheduleItem)
    fun cancel(item: ScheduleItem)
}
