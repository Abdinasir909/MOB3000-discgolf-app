package no.usn.mob3000_gruppe15.local

import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.model.Scorekort
import no.usn.mob3000_gruppe15.model.Spiller

data class SpillUiState (
    val spillere: List<Spiller> = emptyList(),
    val rundeAnsvarlig: Bruker? = null,
    val nyttGjesteSpillerNavn: String = "",
    val valgtHull: Int = 1,
    val feilmelding: String = "",
    val hullListe: List<HullUiState> = emptyList(),
    val poengKortListe: List<Scorekort> = emptyList(),
    val valgtPoengkort: Scorekort? = null,
    val antallRunder: Int = 0,
    val gjennomsnitteligScore: Int = 0,
    val totalKast: Int = 0,
    val gjennomsnitteligKast: Int = 0,
    val bestScore: Int = 0,
    val antHoleInOne: Int = 0,
    val søkBruker: String = "",
    val alleBrukere: List<Bruker> = emptyList(),
)