package no.usn.mob3000_gruppe15.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.model.RegistrerResponse
import no.usn.mob3000_gruppe15.network.NetworkModule
import no.usn.mob3000_gruppe15.repository.AuthRepository
import retrofit2.Response

class OpprettBrukerViewModel(
    private val authRepository: AuthRepository = AuthRepository(NetworkModule.apiService)
): ViewModel() {

    //Brukernavn + set-metode
    var brukernavn by mutableStateOf("")
        private set
    fun brukernavnInput(bIntput: String) { brukernavn = bIntput }

    //Epost + set-metode
    var epost by mutableStateOf("")
        private set
    fun epostInput(eInput: String) { epost = eInput }

    //Passord + set-metode
    var passord by mutableStateOf("")
        private set
    fun passordInput(pInput: String) { passord = pInput }

    //kontroll (Gjenta passord) + set-metode
    var bekreftPassord by mutableStateOf("")
        private set
    fun bekreftPassordInput(p2Input: String) { bekreftPassord = p2Input }


    //Sjekk om laster
    var isLoading by mutableStateOf(false)
        private set

    //Registreringsresultat
    var registrerResultat by mutableStateOf<RegistrerResponse?>(null)
        private set

    //Feilmelding
    var errorMessage by mutableStateOf<String?>(null)
        private set


    //Funksjon for å opprette bruker
    fun opprettBruker() {
        if (brukernavn.isBlank() || epost.isBlank() || passord.isBlank() || bekreftPassord.isBlank()) {
            errorMessage = "Alle felt må fylles ut!"
            return
        }

        if(passord != bekreftPassord) {
            errorMessage = "Passordene matcher ikke!"
            return
        }

        if( !(isValidPassword(passord)) ) {
            errorMessage = "Passord må være minst 8 tegn, maks 20 tegn og ha minst én stor bokstav og ett spesialtegn."
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response: Response<RegistrerResponse> =
                    authRepository.registrer(brukernavn, epost, passord, bekreftPassord)

                if (response.isSuccessful && response.body() != null) {
                    registrerResultat = response.body()
                } else {
                    errorMessage = "Kunne ikke opprette bruker"
                }
            } catch (e: Exception) {
                errorMessage = "Noe gikk galt"
            } finally {
                isLoading = false
            }
        }
    }

    //Sjekke om gyldig passord.     (Hjelp av KI med Regex)
    fun isValidPassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#\$%^&+=]).{8,}\$")
        return passwordRegex.matches(password)
    }

}
