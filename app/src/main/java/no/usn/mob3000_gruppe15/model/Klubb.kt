package no.usn.mob3000_gruppe15.model

    data class Klubb(
        val id: String,
        val navn: String,
        val plass: String,
        val initials: String,
        val beskrivelse: String = "",
        val medlemmer: Int = 0,
        val rating: Double = 4.5,
        val distance: String = "2.3 km",
        val bildeBase64: String? = null,
        val treningstider: List<Treningstid> = emptyList(),
        val brukerId: String
    )

data class Treningstid(
    val dag: String = "",
    val tid: String = ""
)

