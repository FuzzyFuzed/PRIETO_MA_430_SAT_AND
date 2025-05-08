package com.example.prieto_financex.data.model


data class UserData(
    var userId: Int = 0,             // changed userID to userId for consistency
    var userName: String,            // changed username to userName for consistency
    var userEmail: String,           // changed email to userEmail for consistency
    var userPassword: String,        // changed password to userPassword for consistency
    val isVerified: Boolean = false,
    val profileImageUrl: String? = null // changed profileImage to profileImageUrl for clarity
)