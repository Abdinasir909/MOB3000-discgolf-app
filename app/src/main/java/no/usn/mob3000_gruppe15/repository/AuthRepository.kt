package no.usn.mob3000_gruppe15.repository

import no.usn.mob3000_gruppe15.model.GlemtPassordRequest
import no.usn.mob3000_gruppe15.model.GlemtPassordResponse
import no.usn.mob3000_gruppe15.model.LoginRequest
import no.usn.mob3000_gruppe15.model.LoginResponse
import no.usn.mob3000_gruppe15.model.RegistrerRequest
import no.usn.mob3000_gruppe15.model.RegistrerResponse
import no.usn.mob3000_gruppe15.network.ApiService
import no.usn.mob3000_gruppe15.ui.login.GlemtPassordSkjerm
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(epost: String, passord: String): Response<LoginResponse> {
        val request = LoginRequest(brukernavn = epost, passord = passord)
        return apiService.login(request)
    }

    suspend fun registrer(
        brukernavn: String,
        epost: String,
        passord: String,
        bekreftPassord: String
    ): Response<RegistrerResponse> {
        val request = RegistrerRequest(brukernavn, epost, passord, bekreftPassord)
        return apiService.registrer(request)
    }

    suspend fun glemtPassord(
        epost: String
    ): Response<GlemtPassordResponse> {
        val request = GlemtPassordRequest(epost)
        return apiService.glemtPassord(request)
    }

}