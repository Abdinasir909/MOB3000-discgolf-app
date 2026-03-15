package no.usn.mob3000_gruppe15.model

import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.data.model.Bane

data class Scorekort(
    val hullListe: List<HullUiState>,
    val spillere: List<Spiller>,
    val bane: Bane
)
