package no.usn.mob3000_gruppe15.repository


import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.network.NetworkModule
import retrofit2.Response

interface BrukerRepository {
    suspend fun hentBruker(id: String?): Bruker
    suspend fun hentAlleBrukere(): List<Bruker>
    suspend fun oppdaterBruker(id: String, bruker: Bruker): Response<Unit>
}

class NetworkBrukerRepository: BrukerRepository {
    private val apiService = NetworkModule.apiService

    override suspend fun hentBruker(id: String?): Bruker {
        return apiService.hentBruker(id)
    }

    override suspend fun hentAlleBrukere(): List<Bruker> {
        return apiService.hentAlleBrukere()
    }

    override suspend fun oppdaterBruker(id: String, bruker: Bruker): Response<Unit> {
        return apiService.oppdaterBruker(id, bruker)
    }
}


