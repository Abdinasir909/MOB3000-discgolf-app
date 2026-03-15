package no.usn.mob3000_gruppe15.model

import com.google.gson.annotations.SerializedName
import no.usn.mob3000_gruppe15.R

data class Registrering(
    val userId: String,
    val navn: String? = null,
    val dato: String? = null
)

data class Turnering(
    @SerializedName("_id") val id: String,
    val navn: String,
    val beskrivelse: String,
    val dato: String,
    val sted: String,
    val adresse: String,
    val deltakere: Int,
    val premiepott: String,
    val kontakt: String,
    val status: String = "Oppkommende",
    val opprettetAv: String? = null,

    // Listen over de som har meldt seg på
    val registreringer: List<Registrering>? = null
) {
    fun getImage(): Int {
        return R.drawable.usn
    }
}