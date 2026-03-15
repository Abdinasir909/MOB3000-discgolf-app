package no.usn.mob3000_gruppe15.model

data class Spiller(
    val id: String,
    val navn: String,
    val antallKast: List<Int> = emptyList(),
    val antKastTotal: Int = 0,
    val sammenlagt: Int = 0,
)