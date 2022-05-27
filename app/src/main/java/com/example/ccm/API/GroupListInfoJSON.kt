package com.example.ccm.API

import java.io.Serializable

data class GroupListInfoJSON(
    val organizationInfoResponseList: Array<GroupInfoJSON>,
):Serializable

data class GroupInfoJSON(
    var organizationId: String,
    var title: String,
    var memberNum: String,
    var description: String,
    var presidentName: String,
):Serializable
