package no.usn.mob3000_gruppe15.model

data class OppdaterTurneringRequest(
    val navn: String,
    val beskrivelse: String,
    val dato: String,
    val sted: String,
    val adresse: String,
    val deltakere: Int,
    val premiepott: String,
    val kontakt: String,
    val userId: String
)