package com.example.ccm.API

data class CreateGroupJSON(
    var title: String,
    var description: String,
    var color: String
)


data class CreateGroupCode(
    var participationCodeResponse : String
)
