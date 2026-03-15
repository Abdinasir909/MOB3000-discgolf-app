package no.usn.mob3000_gruppe15.model

data class LoginResponse (
    val token: String,
    val userId: String?,
    val name: String?
)