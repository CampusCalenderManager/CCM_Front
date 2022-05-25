package com.example.ccm.API

data class AddScheduleJSON(
    var title: String,
    var content: String,
    var startDate: String,
    var endDate: String,
    var startAlarm: String,
    var isShared: String,
    var isAlarm: String,
    var color: String,
    var organizationId: String,
)

