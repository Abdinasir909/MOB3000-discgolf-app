package no.usn.mob3000_gruppe15.repository

import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.network.NetworkModule
import retrofit2.Response

interface BaneRepository {
    suspend fun leggTilBane(bane: Bane): Response<Unit>
    suspend fun hentBaner(): List<Bane>
    suspend fun oppdaterBane(bane: Bane): Response<Unit>
    suspend fun slettBane(id: String? ): Response<Unit>
}

class NetworkBaneRepository : BaneRepository {
    private val apiService = NetworkModule.apiService

    override suspend fun leggTilBane(bane: Bane): Response<Unit> {
        return apiService.leggTilBane(bane)
    }

    override suspend fun hentBaner(): List<Bane> {
        return apiService.hentBaner()
    }

    override suspend fun oppdaterBane(bane: Bane): Response<Unit> {
        return apiService.oppdaterBane(bane.id, bane)
    }

    override suspend fun slettBane(id: String?): Response<Unit> {
        return apiService.slettBane(id)
    }
}