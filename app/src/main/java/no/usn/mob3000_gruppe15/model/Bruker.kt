package no.usn.mob3000_gruppe15.model

import com.google.gson.annotations.SerializedName

data class Bruker(
    @SerializedName("_id")
    val id: String,
    val brukernavn: String,
    val epost: String,
    val rolle: String,
    val telefon: String? = null,
    val postnummer: String? = null,
    val fodselsaar: String? = null
)