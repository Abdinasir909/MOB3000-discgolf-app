package no.usn.mob3000_gruppe15.ui.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.model.GlemtPassordResponse
import no.usn.mob3000_gruppe15.network.NetworkModule
import no.usn.mob3000_gruppe15.repository.AuthRepository
import retrofit2.Response

/** Innså at det å tilbakestille passord heller ikke fungerte i webløsningne fra
 * forrige emne (Det var ikke jeg som hadde jobbet med den da, glemte at den endte
 * med å kun være "til pynt"/ikke implementert. Det samme er tilfellet i denne
 * applikajsonen
 *
 */
class GlemtPassordViewModel(
    private val authRepository: AuthRepository = AuthRepository(NetworkModule.apiService)
): ViewModel() {

    //Epost + set-metode
    var epost by mutableStateOf("")
        private set
    fun epostInput(eInput: String) { epost = eInput }

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var glemtPassordResponse by mutableStateOf<GlemtPassordResponse?>(null)
        private set


    //Funksjon for lenke
    fun tilbakestillPassord() {
        if (epost.isBlank()) {
            errorMessage = "Fyll inn epost adresse!"
            return
        }

        isLoading = true
        errorMessage = null
        glemtPassordResponse = null

        viewModelScope.launch {
            try {
                val response: Response<GlemtPassordResponse> =
                    authRepository.glemtPassord(epost)

                if (response.isSuccessful && response.body() != null) {
                    glemtPassordResponse = response.body()
                } else {
                    errorMessage = "Noe gikk galt"
                }
            } catch (e: Exception) {
                errorMessage = "Noe gikk galt"
            } finally {
                isLoading = false
            }
        }
    }

}