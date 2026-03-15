package no.usn.mob3000_gruppe15.repository

import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.network.ApiService
import retrofit2.Response

class MegRepository(private val apiService: ApiService) {

    // Hent bruker (info, brukes i MegSkjerm. Brukernavn, Epost, etc.)
    suspend fun hentBruker(userId: String): Bruker {
        return apiService.hentBruker(userId)
    }

    // Oppdater bruker (brukernavn, epost, telefon, postnummer, fødselsår)
    suspend fun oppdaterBruker(id: String, bruker: Bruker): Response<Unit> {
        return apiService.oppdaterBruker(id, bruker)
    }
}