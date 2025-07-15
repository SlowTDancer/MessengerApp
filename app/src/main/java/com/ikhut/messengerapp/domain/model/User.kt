package com.ikhut.messengerapp.domain.model

data class User(
    var username: String = "",
    var job: String = "",
    var password: String = "",
    var imageRes: Int = 0,
    var imageUrl: String? = null,
    var localImagePath: String? = null
)
