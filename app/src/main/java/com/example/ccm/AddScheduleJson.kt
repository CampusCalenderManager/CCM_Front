package com.example.ccm

data class AddScheduleJson(
    var title: String = "",
    var startDate: String = "",
    var endDate: String = "",
    var startAlarm: String = "",
    var endAlarm: String = "",
    var isShared: String = "",
    var color: String = "",
    var organizationId: String = "",
    )

