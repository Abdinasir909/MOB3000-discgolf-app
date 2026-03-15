package no.usn.mob3000_gruppe15.model


data class KlubbRequest(
    val klubbnavn: String,
    val kontaktinfo: String,
    val beskrivelse: String = "",
    val by: String = "",
    val nettside: String = "",
    val nyheter: List<Any> = emptyList(),
    val baner: List<Any> = emptyList(),
    val treningstider: List<Treningstid> = emptyList(),
    val bilde: String?,
    val brukerId: String,
    val medlemmer: List<Map<String, String>>? = null
)