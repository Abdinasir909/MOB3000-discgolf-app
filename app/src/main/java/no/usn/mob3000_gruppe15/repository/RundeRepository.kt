package no.usn.mob3000_gruppe15.repository

import no.usn.mob3000_gruppe15.model.Scorekort
import no.usn.mob3000_gruppe15.network.NetworkModule
import retrofit2.Response

interface RundeRepository{
    suspend fun lagreScorekort(id: String, scorekort: Scorekort): Response<Unit>
    suspend fun hentPoengkort(id: String): List<Scorekort>
}

class NetworRundeRepository: RundeRepository {
    private val apiService = NetworkModule.apiService

    override suspend fun lagreScorekort(
        id: String,
        scorekort: Scorekort
    ): Response<Unit> {
        return apiService.lagreScorekort(id, scorekort)
    }


    override suspend fun hentPoengkort(
        id: String
    ): List<Scorekort> {
        return apiService.hentPoengkort(id)
    }


}