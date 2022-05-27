package com.example.ccm.API

data class GetSchedulesJSON (
    val scheduleDtoList: List<GetScheduleJSON>
)

data class GetScheduleJSON(
    var id: Long,
    var title: String,
    var startDate: String,
    var endDate: String,
    var startAlarm: String,
    var isShared: Boolean,
    var isAlarm: Boolean,
    var memberDto: MemberDTO
)

data class MemberDTO (
    var username: String,
    var name: String
)
