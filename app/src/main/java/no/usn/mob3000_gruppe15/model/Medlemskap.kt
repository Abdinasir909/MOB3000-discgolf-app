package no.usn.mob3000_gruppe15.model

data class OppdaterKlubbRequest(
    val medlemmer: List<MedlemRequest>
)

data class MedlemRequest(
    val id: String,
    val navn: String
)
