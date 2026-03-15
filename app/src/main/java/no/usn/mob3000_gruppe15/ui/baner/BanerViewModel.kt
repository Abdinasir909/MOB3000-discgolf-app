package no.usn.mob3000_gruppe15.ui.baner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.local.BanerUiState
import no.usn.mob3000_gruppe15.local.Bilde
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.HullVelgerViewModel
import no.usn.mob3000_gruppe15.repository.BaneRepository
import kotlin.math.roundToInt
import no.usn.mob3000_gruppe15.repository.NetworkBaneRepository
import no.usn.mob3000_gruppe15.repository.NetworkVærRepository
import no.usn.mob3000_gruppe15.repository.VærRepository
import retrofit2.HttpException
import java.io.IOException

class BanerViewModel(context: Context) : ViewModel(), HullVelgerViewModel {
    val startKartView = LatLng(60.64, 8.39)

    private val dataStoreManager = DataStoreManager(context)
    private val baneRepository: BaneRepository = NetworkBaneRepository()
    private val værRepository: VærRepository = NetworkVærRepository()

    private val _uiState = MutableStateFlow(
        BanerUiState()
    )
    val uiState: StateFlow<BanerUiState> = _uiState.asStateFlow()


    init {
        hentBaner()
    }

    override fun oppdaterValgtHull(valgtHull: Int, antHull: Int) {
        _uiState.update { currentState ->
            if (valgtHull !in 1..antHull) {
                currentState
            }
            if (valgtHull == 1) {
                currentState.copy(valgtHull = valgtHull)
            } else {
                val forrigeHullIndeks = valgtHull - 2
                val forrigeHull = currentState.hullListe[forrigeHullIndeks]

                if (forrigeHull.teePosisjon != null && forrigeHull.kurvPosisjon != null) {
                    oppdaterFeilmelding("")
                    currentState.copy(valgtHull = valgtHull)
                } else {
                    oppdaterFeilmelding("Du må fullføre hull ${forrigeHull.nummer} før du kan gå videre.")
                    currentState
                }
            }
        }
    }

    fun oppdaterBanenavn(nyttNavn: String) {
        _uiState.update { it.copy(nyttBanenavn = nyttNavn) }
    }

    fun oppdaterPosisjonNavn(posisjonNavn: String) {
        _uiState.update { it.copy(nyPosisjonNavn = posisjonNavn) }
        oppdaterFeilmelding("")
    }
    fun oppdaterAntHull(antall: String) {
        _uiState.update { it.copy(nyttAntHull = antall) }
    }
    fun oppdaterBeskrivelse(beskrivelse: String) {
        _uiState.update { it.copy(nyBeskrivelse = beskrivelse) }
    }
    fun oppdaterNyVanskelighet(vanskelighet: String) {
        _uiState.update { it.copy(nyVanskelighet = vanskelighet) }
    }

    fun opprettHullListe(antall: Int) {
        _uiState.update { state ->
            state.copy(
                hullListe = genererHullListe(antall)
            )
        }
    }

    fun genererHullListe(antall: Int): List<HullUiState> {
        return List(antall) { index ->
            HullUiState(
                nummer = index + 1,
                distanse = 0,
                par = 3,
                teePosisjon = null,
                kurvPosisjon = null,
            )
        }
    }

    fun oppdaterTee(hullNr: Int, pos: LatLng) {
        _uiState.update { state ->
            state.copy(
                hullListe = state.hullListe.map {
                    oppdaterFeilmelding("")
                    if (it.nummer == hullNr) it.copy(teePosisjon = pos) else it
                }
            )
        }
    }

    fun oppdaterKurv(hullNr: Int, pos: LatLng) {
        _uiState.update { state ->
            state.copy(
                hullListe = state.hullListe.map {
                    oppdaterFeilmelding("")
                    if (it.nummer == hullNr) it.copy(kurvPosisjon = pos) else it
                }
            )
        }
    }

    fun angreHull(valgtHull: Int) {
        _uiState.update { state ->
            state.copy(
                hullListe = state.hullListe.map {
                    if (it.nummer == valgtHull) {
                        it.copy(
                            kurvPosisjon = null,
                            teePosisjon = null
                        )
                    } else {
                        it
                    }
                }
            )
        }
    }

    fun oppdaterDistanse(hullNr: Int) {
        _uiState.update { state ->
            state.copy(
                hullListe = state.hullListe.map { hull ->
                    if (hull.nummer == hullNr && hull.teePosisjon != null && hull.kurvPosisjon != null) {
                        val distanse = SphericalUtil
                            .computeDistanceBetween(hull.teePosisjon, hull.kurvPosisjon)
                            .roundToInt()
                        hull.copy(distanse = distanse)
                    } else {
                        hull
                    }
                }
            )
        }
    }

    fun oppdaterPar(hullNr: Int, par: Int) {
        _uiState.update { state ->
            state.copy(
                hullListe = state.hullListe.map { hull ->
                    if (hull.nummer == hullNr) {
                        val nyPar = if(par < 2) {
                            6
                        } else if(par > 6) {
                            2
                        } else {
                            par
                        }
                        hull.copy(par = nyPar)
                    } else {
                        hull
                    }
                }
            )
        }
    }

    fun validerBaneinfo(): Boolean {
        val state = _uiState.value

        return if (state.nyttBanenavn == "" ||
            state.nyttAntHull == "" ||
            state.nyVanskelighet == "" ||
            state.nyStartPos == null
        ) {
            oppdaterFeilmelding("Vennligst fyll ut alle feltene før du fortsetter.")
            false
        } else {
            oppdaterFeilmelding("")
            opprettHullListe(antall = state.nyttAntHull.toInt())
            true
        }
    }

    fun oppdaterFeilmelding(feilmelding: String) {
        _uiState.update {
            it.copy(
                feilMelding = feilmelding
            )
        }
    }

    fun oppdaterBaneposisjon(posisjon: LatLng) {
        _uiState.update { currentState ->
            currentState.copy(
                nyStartPos = posisjon
            )
        }
    }



    fun velgBane(bane: Bane) {
        val valgtBane = bane

        _uiState.update { currentState ->
            currentState.copy(
                valgtBane = valgtBane,
                værdata = null
            )
        }
    }

    fun oppdaterBildeUri(uri: Uri?) {
        _uiState.update { it.copy(nyBildeUri = uri) }
    }

    fun oppdaterMineBaner(id: String?) {
        _uiState.update { currentState ->
            currentState.copy(
                mineBaner = currentState.baner.filter { it.eier == id }
            )
        }
    }

    fun lagreNyBane(context: Context) {
        viewModelScope.launch {
            val state = uiState.value

            val bildeBase64 = state.nyBildeUri?.let {
                Bilde.uriToBase64(context, it)
            }
            val dataStoreManager = DataStoreManager(context)
            val id = dataStoreManager.id.firstOrNull()
            try {
                val state = uiState.value
                val nyBane = Bane(
                    navn = state.nyttBanenavn,
                    vanskelighet = state.nyVanskelighet,
                    beskrivelse = state.nyBeskrivelse,
                    plassering = state.nyPosisjonNavn,
                    lengde = beregnDistanse(state.hullListe),
                    rating = 5.0, //TODO: Anmeldelse av baner
                    antHull = state.nyttAntHull.toInt(),
                    hullListe = state.hullListe,
                    koordinater = state.nyStartPos,
                    bilde = bildeBase64,
                    eier = id
                )

                val response = baneRepository.leggTilBane(nyBane)

                if (response.isSuccessful) {
                    hentBaner()
                    _uiState.update { it.copy(nyBildeUri = null) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    oppdaterFeilmelding("Feil fra server: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("Nettverksfeil for banelagring: ", "$e.message")
            }
        }
    }

    fun beregnDistanse(hullListe: List<HullUiState>): Double {
        var totalDistanse = 0
        for(hull in hullListe) {
            totalDistanse += hull.distanse
        }
        val kilometer = totalDistanse.toDouble() / 1000
        return (kilometer*10).roundToInt() / 10.0
    }

    fun hentVærdata(bane: Bane) {
        viewModelScope.launch {
            bane.koordinater?.let { coords ->
                try {
                    val værdata = værRepository.hentVærData(coords.latitude, coords.longitude)
                    _uiState.update { it.copy(værdata = værdata) }
                } catch (e: IOException) {
                    Log.e("HentVærdata", "Nettverksfeil: ${e.message}")
                } catch (e: HttpException) {
                    Log.e("HentVærdata", "HTTP-feil: ${e.code()} - ${e.message()}")
                }
            }
        }
    }

    fun hentBaner() {
        viewModelScope.launch {
            try {
                val baneListe = baneRepository.hentBaner()

                _uiState.update { currentState ->
                    currentState.copy(
                        baner = baneListe
                    )
                }
                val id = dataStoreManager.id.firstOrNull()
                oppdaterMineBaner(id)
            } catch (e: IOException) {
                Log.e("HentBaner", "Nettverksfeil: ${e.message}")
                oppdaterFeilmelding("Kunne ikke laste baner. Sjekk internettforbindelsen.")
            } catch (e: HttpException) {
                Log.e("HentBaner", "HTTP-feil")
                oppdaterFeilmelding("En feil oppstod ved henting av baner.")
            }
        }
    }

    fun settHullListeFraEksisterendeBane(hullListe: List<HullUiState>) {
        _uiState.update { state ->
            state.copy(
                hullListe = hullListe,
                valgtHull = 1
            )
        }
    }

    fun lagreRedigertBane() {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val oppdatertBane = state.valgtBane?.copy(
                    hullListe = state.hullListe,
                    lengde = beregnDistanse(state.hullListe)
                )

                if (oppdatertBane?.id == null) {
                    oppdaterFeilmelding("Kunne ikke lagre: Bane-ID mangler.")
                    return@launch
                }

                val response = baneRepository.oppdaterBane(oppdatertBane)

                if (response.isSuccessful) {
                    hentBaner()
                    _uiState.update { it.copy(valgtBane = oppdatertBane) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("OppdaterBane", "Feil: ${response.code()} - $errorBody")
                    oppdaterFeilmelding("Feil fra server: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("Nettverksfeil for bane-oppdatering: ", "${e.message}")
                oppdaterFeilmelding("Kunne ikke lagre endringer. Prøv igjen.")
            }
        }
    }

    fun validerRedigertBaneinfo(): Boolean {
        val state = _uiState.value

        return if (state.nyttBanenavn == "" || state.nyVanskelighet == "") {
            oppdaterFeilmelding("Vennligst fyll ut alle feltene før du fortsetter.")
            false
        } else {
            oppdaterFeilmelding("")
            true
        }
    }

    fun lagreRedigertBaneinfo(context: Context) {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val bildeBase64 = state.nyBildeUri?.let {
                    Bilde.uriToBase64(context, it)
                }
                val oppdatertBane = state.valgtBane?.copy(
                    navn = state.nyttBanenavn,
                    vanskelighet = state.nyVanskelighet,
                    beskrivelse = state.nyBeskrivelse,
                    bilde = bildeBase64
                )

                if (oppdatertBane?.id == null) {
                    oppdaterFeilmelding("Kunne ikke lagre: Bane-ID mangler.")
                    return@launch
                }

                val response = baneRepository.oppdaterBane(oppdatertBane)

                if (response.isSuccessful) {
                    hentBaner()
                    _uiState.update { it.copy(valgtBane = oppdatertBane) }
                } else {
                    val errorBody = response.errorBody()?.string()
                    oppdaterFeilmelding("Feil fra server: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("Nettverksfeil for baneinfo-oppdatering: ", "${e.message}")
                oppdaterFeilmelding("Kunne ikke lagre endringer. Prøv igjen.")
            }
        }
    }

    fun slettBane() {
        viewModelScope.launch {
            try {
                val state = uiState.value
                val baneId = state.valgtBane?.id

                if (baneId == null) {
                    oppdaterFeilmelding("Kunne ikke slette: Bane-ID mangler.")
                    return@launch
                }

                val response = baneRepository.slettBane(baneId)

                if (response.isSuccessful) {
                    _uiState.update { it.copy(valgtBane = null) }
                    hentBaner()
                } else {
                    val errorBody = response.errorBody()?.string()
                    oppdaterFeilmelding("Feil fra server: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("Nettverksfeil for bane-sletting: ", "${e.message}")
                oppdaterFeilmelding("Kunne ikke slette bane.  Prøv igjen.")
            }
        }
    }
}
