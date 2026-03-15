package no.usn.mob3000_gruppe15.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.LagTurneringRequest
import no.usn.mob3000_gruppe15.model.Turnering
import no.usn.mob3000_gruppe15.network.NetworkModule

// UI-tilstand for turneringslisten som holder på data, laste-status og eventuelle feilmeldinger
data class TurneringListeUiState(
    val turneringer: List<Turnering> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val valgtTurnering: Turnering? = null
)

// ViewModel som håndterer logikk og kommunikasjon mot API for turneringer
class TurneringViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TurneringListeUiState())
    val uiState: StateFlow<TurneringListeUiState> = _uiState.asStateFlow()

    // Laster inn alle turneringer automatisk når ViewModel opprettes
    init {
        hentAlleTurneringer()
    }

    // Setter en spesifikk turnering som valgt i tilstanden
    fun velgTurnering(turnering: Turnering) {
        _uiState.value = _uiState.value.copy(valgtTurnering = turnering)
    }

    // Henter listen over alle turneringer fra API-et og oppdaterer UI-tilstanden
    fun hentAlleTurneringer() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val response = NetworkModule.apiService.hentTurneringer()
                if (response.isSuccessful) {
                    val liste = response.body() ?: emptyList()
                    _uiState.value = _uiState.value.copy(
                        turneringer = liste,
                        isLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Kunne ikke hente data: ${response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    // Oppretter en ny turnering ved å hente bruker-ID lokalt og sende data til API
    fun lagTurnering(
        context: Context,
        navn: String,
        beskrivelse: String,
        dato: String,
        sted: String,
        adresse: String,
        deltakere: Int,
        premiepott: String,
        kontakt: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val dataStore = DataStoreManager(context)
                val userId = dataStore.id.first()

                if (userId.isNullOrEmpty()) {
                    onError("Fant ingen bruker-ID. Logg ut og inn igjen.")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }

                val request = LagTurneringRequest(
                    navn = navn,
                    beskrivelse = beskrivelse,
                    dato = dato,
                    sted = sted,
                    adresse = adresse,
                    deltakere = deltakere,
                    premiepott = premiepott,
                    kontakt = kontakt,
                    userId = userId
                )

                val response = NetworkModule.apiService.lagTurnering(request)

                if (response.isSuccessful) {
                    val turneringId = response.body()?.turneringId ?: ""

                    hentAlleTurneringer()

                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess(turneringId)
                } else {
                    val feilmelding = response.errorBody()?.string() ?: "Ukjent feil"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = feilmelding)
                    onError(feilmelding)
                }
            } catch (e: Exception) {
                Log.e("TURNERING", "Feil ved opprettelse: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Ukjent feil")
            }
        }
    }

    // Sletter en turnering basert på ID
    fun slettTurnering(
        turneringId: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val dataStore = DataStoreManager(context)
                val userId = dataStore.id.first()

                if (userId.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError("Fant ikke bruker-ID. Prøv å logge inn på nytt.")
                    return@launch
                }

                val response = NetworkModule.apiService.slettTurnering(turneringId, userId)

                if (response.isSuccessful) {
                    hentAlleTurneringer()
                    onSuccess()
                } else {
                    val feil = response.errorBody()?.string() ?: "Kunne ikke slette turnering"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = feil)
                    onError(feil)
                }
            } catch (e: Exception) {
                Log.e("TURNERING", "Feil ved sletting: ${e.message}")
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Ukjent feil")
            }
        }
    }


    // Oppdaterer informasjonen på en eksisterende turnering
    fun oppdaterTurnering(
        turneringId: String,
        context: Context,
        navn: String,
        beskrivelse: String,
        dato: String,
        sted: String,
        adresse: String,
        deltakere: Int,
        premiepott: String,
        kontakt: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val dataStore = DataStoreManager(context)
                val userId = dataStore.id.first()

                if (userId.isNullOrEmpty()) {
                    onError("Fant ikke bruker-ID.")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }
                val request = no.usn.mob3000_gruppe15.model.OppdaterTurneringRequest(
                    navn = navn,
                    beskrivelse = beskrivelse,
                    dato = dato,
                    sted = sted,
                    adresse = adresse,
                    deltakere = deltakere,
                    premiepott = premiepott,
                    kontakt = kontakt,
                    userId = userId
                )

                val response = NetworkModule.apiService.oppdaterTurnering(turneringId, request)

                if (response.isSuccessful) {
                    hentAlleTurneringer() // Oppdater listen i bakgrunnen
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    val feil = response.errorBody()?.string() ?: "Feil ved oppdatering"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = feil)
                    onError(feil)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Ukjent feil")
            }
        }
    }


    // Melder den innloggede brukeren på en turnering
    fun meldPaaTurnering(
        turneringId: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val dataStore = DataStoreManager(context)
                val userId = dataStore.id.first()

                if (userId.isNullOrEmpty()) {
                    onError("Du må være logget inn for å melde deg på.")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@launch
                }


                val request = no.usn.mob3000_gruppe15.model.PameldingRequest(userId)

                val response = NetworkModule.apiService.meldPaaTurnering(turneringId, request)

                if (response.isSuccessful) {

                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                    hentAlleTurneringer()
                } else {
                    val feil = response.errorBody()?.string() ?: "Kunne ikke melde på"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = feil)
                    onError(feil)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Ukjent feil")
            }
        }
    }


    // Melder den innloggede brukeren av en turnering
    fun meldAvTurnering(
        turneringId: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                val dataStore = DataStoreManager(context)
                val userId = dataStore.id.first()

                if (userId.isNullOrEmpty()) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onError("Fant ikke bruker-ID.")
                    return@launch
                }

                val response = NetworkModule.apiService.meldAvTurnering(turneringId, userId)

                if (response.isSuccessful) {
                    hentAlleTurneringer() // Oppdater listen for å fjerne navnet/telle ned
                    onSuccess()
                } else {
                    val feil = response.errorBody()?.string() ?: "Kunne ikke melde av"
                    _uiState.value = _uiState.value.copy(isLoading = false, error = feil)
                    onError(feil)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
                onError(e.message ?: "Ukjent feil")
            }
        }
    }
}