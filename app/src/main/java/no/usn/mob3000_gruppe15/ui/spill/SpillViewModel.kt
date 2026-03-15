package no.usn.mob3000_gruppe15.ui.spill

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.local.SpillUiState
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.model.HullVelgerViewModel
import no.usn.mob3000_gruppe15.model.Scorekort
import no.usn.mob3000_gruppe15.model.Spiller
import no.usn.mob3000_gruppe15.model.SpillerRunde
import no.usn.mob3000_gruppe15.repository.BrukerRepository
import no.usn.mob3000_gruppe15.repository.NetworRundeRepository
import no.usn.mob3000_gruppe15.repository.NetworkBrukerRepository
import no.usn.mob3000_gruppe15.repository.RundeRepository
import okio.IOException
import kotlin.collections.take
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class SpillViewModel(
    context: Context
) : ViewModel(), HullVelgerViewModel {

    private val dataStoreManager = DataStoreManager(context)
    private val _uiState = MutableStateFlow(
        SpillUiState()
    )
    val uiState: StateFlow<SpillUiState> = _uiState.asStateFlow()

    private val brukerRepository: BrukerRepository = NetworkBrukerRepository()
    private val rundeRepository: RundeRepository = NetworRundeRepository()

    init {
        viewModelScope.launch {
            dataStoreManager.id.collect { userId ->
                if (userId != null) {
                    try {
                        val poengkortListe = rundeRepository.hentPoengkort(userId).reversed()
                        _uiState.update {
                            it.copy(poengKortListe = poengkortListe)
                        }
                        beregnSpillerStatistikk(poengkortListe)
                    } catch (e: Exception) {
                        Log.e("Nettverksfeil for henting av poengkort", "${e.message}")
                    }
                }
            }
        }
    }


    fun oppdaterAntallKast(hullNr: Int, antKast: Int, spiller: Spiller, hullListe: List<HullUiState>) {
        if (antKast < 0) return
        _uiState.update { state ->
            state.copy(
                spillere = state.spillere.map { enSpiller ->
                    if (enSpiller.id == spiller.id) {
                        val oppdaterteKast = enSpiller.antallKast?.toMutableList()?.apply {
                            set(hullNr - 1, antKast)
                        } ?: emptyList()

                        val midlertidigSpiller = enSpiller.copy(antallKast = oppdaterteKast)
                        val nySammenlagtScore = beregnScoreMotPar(midlertidigSpiller, hullListe)

                        midlertidigSpiller.copy(
                            sammenlagt = nySammenlagtScore,
                            antKastTotal = oppdaterteKast.sum()
                        )
                    } else {
                        enSpiller
                    }
                }
            )
        }
    }

    private fun beregnScoreMotPar(spiller: Spiller, hullListe: List<HullUiState>): Int {
        val totalKast = spiller.antallKast?.filter { it > 0 }?.sum() ?: 0
        if (totalKast == 0) return 0

        val antallSpilteHull = spiller.antallKast?.count { it > 0 } ?: 0
        val totalPar = hullListe
            .take(antallSpilteHull)
            .sumOf { it.par }

        return totalKast - totalPar
    }

    fun fyllAntallKast(antHull: Int) {
        _uiState.update { state ->
            state.copy(
                spillere = state.spillere.map {enSpiller ->
                    enSpiller.copy(antallKast = List(antHull) { 0 })
                }
            )
        }
    }

    fun oppdaterFeilmelding(nyFeilmelding: String) {
        _uiState.update { it.copy(feilmelding = nyFeilmelding) }
    }

    fun oppdaterGjestespillerNavn(nyttNavn: String) {
        _uiState.update { it.copy(nyttGjesteSpillerNavn = nyttNavn) }
    }

    fun leggTilRundeStarter() {
        viewModelScope.launch {
            try {
                val id = dataStoreManager.id.firstOrNull()
                if (id != null) {
                    val bruker = brukerRepository.hentBruker(id)

                    val spiller = Spiller(
                        id = bruker.id,
                        navn = bruker.brukernavn,
                        antallKast = emptyList(),
                        sammenlagt = 0
                    )

                    _uiState.update { currentState ->
                        currentState.copy(
                            spillere = listOf(spiller),
                        )
                    }
                } else {
                    Log.e("HentBruker", "Bruker-ID er null")
                }
            } catch (e: IOException) {
                Log.e("HentBruker", "Nettverksfeil: ${e.message}")
            } catch (e: HttpException) {
                Log.e("HentBruker", "HTTP-feil")
            }
        }
    }


    @OptIn(ExperimentalUuidApi::class)
    fun leggTilGjestespiller(navn: String) {

        if(navn == "") {
            oppdaterFeilmelding("Gjestespillerens navn kan ikke være tomt")
            return
        }

        val uuid = Uuid.random().toString()
        val spiller = Spiller(
            id = "${uuid}_gjest",
            navn = navn,
        )

        leggTilSpiller(spiller)
        oppdaterGjestespillerNavn("")
        oppdaterFeilmelding("")
    }

    fun leggTilSpiller(spiller: Spiller){
        _uiState.update { currentState ->
            currentState.copy(
                spillere = currentState.spillere + spiller
            )
        }
    }

    override fun oppdaterValgtHull(valgtHull: Int, antHull: Int) {
        _uiState.update { currentState ->
            if(valgtHull !in 1..antHull) {
                currentState
            } else {
                currentState.copy(valgtHull = valgtHull)
            }
        }
    }

    fun oppdaterValgtPoengkort(poengkort: Scorekort) {
        _uiState.update { it.copy(
            valgtPoengkort = poengkort
        )
        }
    }

    fun oppdaterHulliste(hullListe: List<HullUiState>) {
        _uiState.update { it.copy(hullListe = hullListe) }
    }

    fun validerKastFørAvslutning(): Boolean {
        val harMangler = _uiState.value.spillere.any { spiller ->
            spiller.antallKast?.any { it == 0 } == true
        }

        if (harMangler) {
            oppdaterFeilmelding("Noen hull mangler kast.\nEr du sikker på at du vil avslutte?")
            return false
        }
        return true
    }

    fun lagreScorekort(valgtBane: Bane) {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val spillere = state.spillere
                val nyScorekort = Scorekort(
                    spillere = spillere,
                    hullListe = state.hullListe,
                    bane = valgtBane
                )
                spillere.forEach { spiller ->
                    if(!spiller.id.contains("_gjest")) {
                        val response = rundeRepository.lagreScorekort(
                            spiller.id,
                            nyScorekort
                        )
                        if (response.isSuccessful) {
                            hentPoengkort()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            oppdaterFeilmelding("Feil fra server: ${response.code()} - $errorBody")
                        }
                    }
                }
                nullStillSpill()
            } catch (e: Exception) {
                Log.e("Nettverksfeil for scorekortlagring", "${e.message}")
            }
        }
    }

    fun hentAlleBrukere() {
        viewModelScope.launch {
            try {
                val brukere = brukerRepository.hentAlleBrukere()
                _uiState.update { it.copy(alleBrukere = brukere) }
            } catch (e: Exception) {
                Log.e("HentBrukere", "Feil: ${e.message}")
            }
        }
    }

    fun oppdaterBrukerSøk(søk: String) {
        _uiState.update { it.copy(søkBruker = søk) }
    }

    fun filtrerBrukere(): List<Bruker> {
        val state = _uiState.value
        val eksisterendeIds = state.spillere.map { it.id }

        return state.alleBrukere
            .filter { it.id !in eksisterendeIds }
            .filter {
                state.søkBruker.isBlank() ||
                        it.brukernavn.contains(state.søkBruker, ignoreCase = true)
            }
    }

    fun leggTilBruker(bruker: Bruker) {
        val spiller = Spiller(
            id = bruker.id,
            navn = bruker.brukernavn,
            antallKast = emptyList(),
            sammenlagt = 0
        )
        leggTilSpiller(spiller)
        _uiState.update { it.copy(søkBruker = "") }
    }


    fun nullStillSpill() {
        _uiState.update { currentState ->
            // Behold poengkortliste og statistikk når spillet nullstilles
            currentState.copy(
                spillere = emptyList(),
                valgtHull = 1,
                hullListe = emptyList(),
                valgtPoengkort = null,
                feilmelding = "",
                nyttGjesteSpillerNavn = "",
                alleBrukere = emptyList(),
                søkBruker = ""
            )
        }
    }

    fun beregnSpillerStatistikk(poengkortListe: List<Scorekort>) {
        val spillerResultater = poengkortListe
            .filter { it.spillere.isNotEmpty() }
            .map { poengkort ->
                val spiller = poengkort.spillere[0]
                val banePar = poengkort.hullListe.sumOf { it.par }
                SpillerRunde(
                    antKastTotal = spiller.antKastTotal,
                    antallKast = spiller.antallKast,
                    sammenlagt = spiller.sammenlagt,
                    banePar = banePar
                )
            }

        if (spillerResultater.isEmpty()) {
            return
        }

        val antallRunder = spillerResultater.size

        val totalKast = spillerResultater.sumOf { runde ->
            if (runde.antKastTotal > 0) {
                runde.antKastTotal
            } else {
                runde.antallKast.sum()
            }
        }

        val gjennomsnitteligKast = if (antallRunder > 0) {
            totalKast / antallRunder
        } else {
            0
        }

        val totalScore = spillerResultater.sumOf { it.sammenlagt }
        val gjennomsnitteligScore = if (antallRunder > 0) {
            totalScore / antallRunder
        } else {
            0
        }

        val bestScore = spillerResultater.minOfOrNull { it.sammenlagt } ?: 0

        val antHoleInOne = spillerResultater.sumOf { runde ->
            runde.antallKast.count { kast -> kast == 1 }
        }

        _uiState.update { currentState ->
            currentState.copy(
                antallRunder = antallRunder,
                gjennomsnitteligScore = gjennomsnitteligScore,
                totalKast = totalKast,
                gjennomsnitteligKast = gjennomsnitteligKast,
                bestScore = bestScore,
                antHoleInOne = antHoleInOne
            )
        }
    }


    fun hentPoengkort() {
        viewModelScope.launch {
            try {
                val id = dataStoreManager.id.firstOrNull()
                if (id != null) {
                    _uiState.update {
                        it.copy(
                            poengKortListe = rundeRepository.hentPoengkort(id).reversed()
                        )
                    }
                    beregnSpillerStatistikk(uiState.value.poengKortListe)
                }

            } catch (e: Exception) {
                Log.e("Nettverksfeil for henting av poengkort", "${e.message}")
            }
        }
    }
}