package no.usn.mob3000_gruppe15.repository

import no.usn.mob3000_gruppe15.model.Værdata
import no.usn.mob3000_gruppe15.network.NetworkModule

interface VærRepository {
    suspend fun hentVærData(
        latitude: Double,
        longitude: Double
    ): Værdata
}

class NetworkVærRepository: VærRepository {
    private val værApiService = NetworkModule.værApiService

    override suspend fun hentVærData(
        latitude: Double,
        longitude: Double
    ): Værdata {
        val response = værApiService.hentVærData(latitude, longitude)

        val nåværendeVærData = response.properties.timeseries.firstOrNull()
        val details = nåværendeVærData?.data?.instant?.details

        val temperatur = details?.air_temperature ?: 0.0
        val symbolCode = nåværendeVærData?.data?.next_1_hours?.summary?.symbol_code ?: "ukjent"
        val vindHastighet = details?.wind_speed ?: 0.0
        val vindRetningGrader = details?.wind_from_direction ?: 0.0

        return Værdata(
            temperatur = temperatur,
            symbolCode = symbolCode,
            vindHastighet = vindHastighet,
            vindRetningGrader = vindRetningGrader
        )
    }
}