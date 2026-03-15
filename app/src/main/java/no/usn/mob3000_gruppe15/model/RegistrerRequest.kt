package no.usn.mob3000_gruppe15.model

data class RegistrerRequest(
    val brukernavn: String,
    val epost: String,
    val passord: String,
    val bekreftPassord: String
)
