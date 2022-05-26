package com.example.ccm.API

data class MyOrganizationJSON (
    var organizationInfoResponseList: List<OrganizationInfo>,
    var total: Int
)

data class OrganizationInfo (
    var organizationId: Long,
    var title: String,
    var color: String,
    var memberNum: String,
    var description: String,
    var presidentName: String
)