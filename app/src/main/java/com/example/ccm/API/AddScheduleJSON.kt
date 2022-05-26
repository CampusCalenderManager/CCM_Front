package com.example.ccm.API

data class AddScheduleJSON(
    var title: String,
    var content: String,
    var startDate: String,
    var endDate: String,
    var startAlarm: String,
    var isShared: Boolean,
    var isAlarm: Boolean,
    var organizationId: Long,
)

