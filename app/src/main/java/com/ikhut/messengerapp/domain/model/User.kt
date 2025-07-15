package com.ikhut.messengerapp.domain.model

data class User(
    var username: String = "",
    var job: String = "",
    var password: String = "",
    var profileImageURL: String? = null
)
