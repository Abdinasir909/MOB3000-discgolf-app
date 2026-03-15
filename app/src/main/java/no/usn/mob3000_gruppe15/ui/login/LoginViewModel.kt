package no.usn.mob3000_gruppe15.ui.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.LoginResponse
import no.usn.mob3000_gruppe15.network.NetworkModule
import no.usn.mob3000_gruppe15.repository.AuthRepository
import retrofit2.Response

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository(NetworkModule.apiService)
): ViewModel() {

    // Testbruker-kredentialer
    companion object {
        private const val TEST_BRUKERNAVN = "testbruker"
        private const val TEST_PASSORD = "Passord!"
    }

    //Epost + Set-metode/Funksjon for å oppdatere tekstfelt
    var epost by mutableStateOf("")
        private set
    fun oppgiEpost(oppgittEpost: String) { epost = oppgittEpost }

    //Passord + Set-metode/Funksjon for å oppdatere tekstfelt
    var passord by mutableStateOf("")
        private set
    fun oppgiPassord(oppgittPassord: String) {
        passord = oppgittPassord
    }

    //Sjekk om laster
    var isLoading by mutableStateOf(false)
        private set

    //Innloggingsresultat
    var loginResult by mutableStateOf<LoginResponse?>(null)
        private set

    //Feilmelding
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Funksjon for å nullstille feltene
    fun nullstillFelter() {
        epost = ""
        passord = ""
        errorMessage = null
    }

    // Funksjon for å logge inn med testbruker
    fun loggInnMedTestbruker(context: Context) {
        epost = TEST_BRUKERNAVN
        passord = TEST_PASSORD
        loggInn(context)
    }

    //Funksjon for å logge inn
    fun loggInn(context: Context) {
        if (epost.isBlank() || passord.isBlank()) {
            errorMessage = "Epost og passord må fylles inn!"
            return
        }

        isLoading = true
        errorMessage = null

        viewModelScope.launch {
            try {
                val response: Response<LoginResponse> =
                    authRepository.login(epost, passord)

                if (response.isSuccessful && response.body() != null) {
                    val dataStoreManager = DataStoreManager(context)
                    dataStoreManager.saveLoginStatus(true)
                    loginResult = response.body()
                    loginResult?.userId?.let { id ->
                        dataStoreManager.saveLoginData(id)
                    }
                } else {
                    errorMessage = "Feil brukernavn eller passord"
                }
            } catch(e: Exception) {
                errorMessage = "Noe gikk galt"
            } finally {
                isLoading = false
            }
        }
    }
}