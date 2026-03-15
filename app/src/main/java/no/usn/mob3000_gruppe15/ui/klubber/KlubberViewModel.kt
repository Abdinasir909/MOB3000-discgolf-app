package no.usn.mob3000_gruppe15.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import no.usn.mob3000_gruppe15.local.Bilde
import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.model.KlubbRequest
import no.usn.mob3000_gruppe15.model.MedlemRequest
import no.usn.mob3000_gruppe15.model.OppdaterKlubbRequest
import no.usn.mob3000_gruppe15.network.NetworkModule
import no.usn.mob3000_gruppe15.model.Treningstid
import no.usn.mob3000_gruppe15.network.NetworkModule.apiService


class KlubberViewModel : ViewModel() {
    private val _klubber = mutableStateOf<List<Any>>(emptyList())
    val klubber: State<List<Any>> = _klubber
    private val _bruker = mutableStateOf<Bruker?>(null)
    val bruker: State<Bruker?> = _bruker

    init {
        hentAlleKlubber()
     }

    fun hentAlleKlubber() {
        viewModelScope.launch {
            try {
                val response = NetworkModule.apiService.hentAlleKlubber()
                _klubber.value = response.body() ?: emptyList()
            } catch (e: Exception) {
                Log.e("KLUBBER", "Feil ved henting: ${e.message}")
            }
        }
    }
    
    fun getBruker(brukerId: String?) {
    viewModelScope.launch {
        try {
            if (brukerId != null && brukerId.isNotEmpty()) {
                val response = NetworkModule.apiService.getBruker(brukerId)
                _bruker.value = response.body()
            }
        } catch (e: Exception) {
            _bruker.value = null
        }
    }
}


    fun opprettKlubb(
        context: Context,
        klubbnavn: String,
        kontaktinfo: String,
        beskrivelse: String,
        by: String,
        nettside: String,
        nyheter: List<Any> = emptyList(),
        baner: List<Any> = emptyList(),
        bildeUri: Uri?,
        treningstider: List<Treningstid> = emptyList(),
        brukerId: String,
        medlemmer: List<Map<String, String>>? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val bildeBase64 = bildeUri?.let { Bilde.uriToBase64(context, it) }
                        Log.d("KLUBB_BILDEVM", "Uri = $bildeUri")
            Log.d("KLUBB_BILDEVM", "Bilde base64 lengte = ${bildeBase64?.length ?: "null"}")
            withContext(Dispatchers.Main) {
                val request = KlubbRequest(
                    klubbnavn = klubbnavn,
                    kontaktinfo = kontaktinfo,
                    beskrivelse = beskrivelse,
                    by = by,
                    nettside = nettside,
                    bilde = bildeBase64,
                    treningstider = treningstider,
                    nyheter = nyheter,
                    baner = baner,
                    brukerId = brukerId,
                    medlemmer = medlemmer
                )
                Log.d("KLUBB_BILDEVM", "Request innhold = $request")
                try {
                    val response = NetworkModule.apiService.opprettKlubb(request)
                    hentAlleKlubber()
                } catch (e: Exception) {
                    Log.e("KLUBBER", "Feil: ${e.message}")
                }
            }
        }
    }

    fun oppdaterKlubb(
        context: Context,
        klubbId: String,
        klubbnavn: String,
        kontaktinfo: String,
        beskrivelse: String,
        by: String,
        nettside: String,
        nyheter: List<Any> = emptyList(),
        baner: List<Any> = emptyList(),
        bildeUri: Uri? = null,
        treningstider: List<Treningstid> = emptyList(),
        brukerId: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val bildeBase64 = bildeUri?.let { Bilde.uriToBase64(context, it) }
            withContext(Dispatchers.Main) {
                val request = KlubbRequest(
                    klubbnavn = klubbnavn,
                    kontaktinfo = kontaktinfo,
                    beskrivelse = beskrivelse,
                    by = by,
                    nettside = nettside,
                    bilde = bildeBase64,
                    treningstider = treningstider,
                    nyheter = nyheter,
                    baner = baner,
                    brukerId = brukerId
                )
                try {
                    val response = NetworkModule.apiService.oppdaterKlubb(klubbId, request)
                    hentAlleKlubber()
                } catch (e: Exception) {
                    Log.e("KLUBBER", "Feil ved opprettelse: ${e.message}")
                }
            }
        }
    }

    fun bliMedlem(
        brukerId: String,
        brukerNavn: String,
        klubbId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val klubb = NetworkModule.apiService.hentKlubb(klubbId).body()
                val medlemmer = mutableListOf<MedlemRequest>()

                val medlemmerFraDb = klubb?.get("medlemmer") as? List<*>
                medlemmerFraDb?.forEach { medlem ->
                    val m = medlem as? Map<*, *>
                    val id = m?.get("id")?.toString() ?: ""
                    val navn = m?.get("navn")?.toString() ?: ""
                    medlemmer.add(MedlemRequest(id, navn))
                }

                medlemmer.add(MedlemRequest(brukerId, brukerNavn))

                val response = NetworkModule.apiService.oppdaterMedlemskap(
                    klubbId,
                    OppdaterKlubbRequest(medlemmer = medlemmer)
                )

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Feil")
                }
            } catch (e: Exception) {
                onError("Feil")
            }
        }
    }

    fun slettKlubb(klubbId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                apiService.slettKlubb(klubbId) 
                hentAlleKlubber()
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

}
