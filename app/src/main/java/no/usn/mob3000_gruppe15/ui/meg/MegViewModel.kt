package no.usn.mob3000_gruppe15.ui.meg

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.local.FeltType
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.network.NetworkModule
import no.usn.mob3000_gruppe15.network.NetworkModule.apiService
import no.usn.mob3000_gruppe15.repository.BrukerRepository
import no.usn.mob3000_gruppe15.repository.MegRepository
import no.usn.mob3000_gruppe15.repository.NetworkBrukerRepository

class MegViewModel(context: Context) : ViewModel() {

    private val dataStoreManager = DataStoreManager(context)
    private val brukerRepository: BrukerRepository = NetworkBrukerRepository()

    // Lagrede verdier fra database (vises i UI)
    var brukernavn by mutableStateOf("")
        private set
    var epost by mutableStateOf("")
        private set
    var tlfNr by mutableStateOf("")
        private set
    var postNr by mutableStateOf("")
        private set
    var fodselsaar by mutableStateOf("")
        private set

    // Midlertidige verdier for redigering i bottom sheet
    var tempBrukernavn by mutableStateOf("")
        private set
    var tempEpost by mutableStateOf("")
        private set
    var tempTlfNr by mutableStateOf("")
        private set
    var tempPostNr by mutableStateOf("")
        private set
    var tempFodselsaar by mutableStateOf("")
        private set
    var tempOldPassword by mutableStateOf("")
        private set
    var tempNewPassord by mutableStateOf("")
        private set
    var tempBekreftPassord by mutableStateOf("")
        private set

    // Set-metoder for midlertidige verdier
    fun oppgiBrukernavn(oppgittBrukernavn: String) { tempBrukernavn = oppgittBrukernavn }
    fun oppgiEpost(oppgittEpost: String) { tempEpost = oppgittEpost }
    fun oppgiTlfNr(oppgittTlfNr: String) { tempTlfNr = oppgittTlfNr }
    fun oppgiPostNr(oppgittPostNr: String) { tempPostNr = oppgittPostNr }
    fun oppgiFodselsaar(oppgittFodselsaar: String) { tempFodselsaar = oppgittFodselsaar }
    fun oppgiGammeltPassord(oppgittOldPassord: String) { tempOldPassword = oppgittOldPassord }
    fun opggiNyttPassord(oppgittNewPassord: String) { tempNewPassord = oppgittNewPassord }
    fun oppgiBekreftPassord(oppgittBekreftPassord: String) { tempBekreftPassord = oppgittBekreftPassord }

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var visOppdateringDialog by mutableStateOf<Boolean?>(null)
        private set

    // Feilmelding for validering
    var valideringsFeilmelding by mutableStateOf<String?>(null)
        private set

    fun nullstillValideringsFeil() {
        valideringsFeilmelding = null
    }

    // Initialiserer midlertidige verdier når bottom sheet åpnes
    fun startRedigering(feltType: FeltType) {
        when (feltType) {
            FeltType.BRUKERNAVN -> tempBrukernavn = brukernavn
            FeltType.EPOST -> tempEpost = epost
            FeltType.TELEFON -> tempTlfNr = tlfNr
            FeltType.POSTNUMMER -> tempPostNr = postNr
            FeltType.FODSELSAAR -> tempFodselsaar = fodselsaar
            FeltType.PASSORD -> {
                tempOldPassword = ""
                tempNewPassord = ""
                tempBekreftPassord = ""
            }
        }
        nullstillValideringsFeil()
    }

    // Avbryter redigering og nullstiller midlertidige verdier
    fun avbrytRedigering() {
        tempBrukernavn = brukernavn
        tempEpost = epost
        tempTlfNr = tlfNr
        tempPostNr = postNr
        tempFodselsaar = fodselsaar
        tempOldPassword = ""
        tempNewPassord = ""
        tempBekreftPassord = ""
        nullstillValideringsFeil()
    }

    /**
     * Validerer et felt og oppdaterer valideringsFeilmelding.
     * Returnerer true hvis feltet er gyldig, false hvis ugyldig.
     */
    fun validerFelt(feltType: FeltType, verdi: String): Boolean {
        valideringsFeilmelding = when (feltType) {
            FeltType.BRUKERNAVN -> validerBrukernavn(verdi)
            FeltType.EPOST -> validerEpost(verdi)
            FeltType.TELEFON -> validerTelefon(verdi)
            FeltType.POSTNUMMER -> validerPostnummer(verdi)
            FeltType.FODSELSAAR -> validerFodselsaar(verdi)
            else -> null
        }

        // Vis dialog med feilmelding hvis validering feiler
        if (valideringsFeilmelding != null) {
            visOppdateringDialog = false
        }

        return valideringsFeilmelding == null
    }

    private fun validerBrukernavn(brukernavn: String): String?  {
        return when {
            brukernavn.isBlank() -> "Brukernavn kan ikke være tomt"
            brukernavn.length <= 5 -> "Brukernavn må være større enn 5 tegn"
            else -> null
        }
    }

    private fun validerEpost(epost: String): String? {
        val epostRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return when {
            epost.isBlank() -> "E-post kan ikke være tom"
            !epost.matches(epostRegex) -> "Ugyldig e-postformat"
            else -> null
        }
    }

    private fun validerTelefon(telefon: String): String? {
        return when {
            telefon.isBlank() -> "Telefonnummer kan ikke være tomt"
            ! telefon.all { it.isDigit() } -> "Telefonnummer kan kun inneholde tall"
            telefon.length != 8 -> "Telefonnummer må være 8 siffer"
            else -> null
        }
    }

    private fun validerPostnummer(postnummer: String): String? {
        return when {
            postnummer.isBlank() -> "Postnummer kan ikke være tomt"
            !postnummer.all { it.isDigit() } -> "Postnummer kan kun inneholde tall"
            postnummer.length != 4 -> "Postnummer må være 4 siffer"
            else -> null
        }
    }

    private fun validerFodselsaar(fodselsaar: String): String? {
        val aar = fodselsaar.toIntOrNull()
        val gjeldendeAar = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        return when {
            fodselsaar.isBlank() -> "Fødselsår kan ikke være tomt"
            aar == null -> "Fødselsår må være et gyldig tall"
            aar < 1900 -> "Fødselsår kan ikke være før 1900"
            aar > gjeldendeAar -> "Fødselsår kan ikke være i fremtiden"
            aar > gjeldendeAar - 13 -> "Du må være minst 13 år gammel"
            else -> null
        }
    }

    fun skjulDialog() {
        visOppdateringDialog = null
    }

    fun oppdaterBrukerFelt(feltType: FeltType, nyVerdi: String) {
        viewModelScope.launch {
            val userId = dataStoreManager.id.firstOrNull() ?: return@launch
            try {
                val eksisterendeBruker = brukerRepository.hentBruker(userId)
                val oppdatertBruker = when (feltType) {
                    FeltType.BRUKERNAVN -> eksisterendeBruker.copy(brukernavn = nyVerdi)
                    FeltType.EPOST -> eksisterendeBruker.copy(epost = nyVerdi)
                    FeltType.TELEFON -> eksisterendeBruker.copy(telefon = nyVerdi)
                    FeltType.POSTNUMMER -> eksisterendeBruker.copy(postnummer = nyVerdi)
                    FeltType.FODSELSAAR -> eksisterendeBruker.copy(fodselsaar = nyVerdi)
                    else -> return@launch
                }
                val response = brukerRepository.oppdaterBruker(userId, oppdatertBruker)
                if (response.isSuccessful) {
                    // Oppdater kun de lagrede verdiene ved suksess
                    when (feltType) {
                        FeltType.BRUKERNAVN -> brukernavn = nyVerdi
                        FeltType.EPOST -> epost = nyVerdi
                        FeltType.TELEFON -> tlfNr = nyVerdi
                        FeltType.POSTNUMMER -> postNr = nyVerdi
                        FeltType.FODSELSAAR -> fodselsaar = nyVerdi
                        else -> {}
                    }
                    visOppdateringDialog = true
                } else {
                    visOppdateringDialog = false
                }
            } catch (e: Exception) {
                visOppdateringDialog = false
            }
        }
    }

    init {
        viewModelScope.launch {
            dataStoreManager.id.collect { userId ->
                if (userId != null) {
                    try {
                        val bruker = brukerRepository.hentBruker(userId)
                        brukernavn = bruker.brukernavn
                        epost = bruker.epost
                        tlfNr = bruker.telefon ?: "-Ikke satt-"
                        postNr = bruker.postnummer ?: "-Ikke satt-"
                        fodselsaar = bruker.fodselsaar ?: "-Ikke satt-"
                        tempBrukernavn = bruker.brukernavn
                        tempEpost = bruker.epost
                        tempTlfNr = bruker.telefon ?: "-Ikke satt-"
                        tempPostNr = bruker.postnummer ?: "-Ikke satt-"
                        tempFodselsaar = bruker.fodselsaar ?: "-Ikke satt-"
                    } catch (e: Exception) {
                        brukernavn = "N/A"
                        epost = "N/A"
                    }
                }
            }
        }
    }
}