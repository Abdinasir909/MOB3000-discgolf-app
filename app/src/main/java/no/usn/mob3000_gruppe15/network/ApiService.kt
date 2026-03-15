package no.usn.mob3000_gruppe15.network

import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.model.GlemtPassordRequest
import no.usn.mob3000_gruppe15.model.GlemtPassordResponse
import no.usn.mob3000_gruppe15.model.KlubbRequest
import no.usn.mob3000_gruppe15.model.LagTurneringRequest
import no.usn.mob3000_gruppe15.model.LagTurneringResponse
import no.usn.mob3000_gruppe15.model.LoginRequest
import no.usn.mob3000_gruppe15.model.LoginResponse
import no.usn.mob3000_gruppe15.model.OppdaterKlubbRequest
import no.usn.mob3000_gruppe15.model.OppdaterTurneringRequest
import no.usn.mob3000_gruppe15.model.RegistrerRequest
import no.usn.mob3000_gruppe15.model.RegistrerResponse
import no.usn.mob3000_gruppe15.model.Scorekort
import no.usn.mob3000_gruppe15.model.VærApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Angående API-er
 *
 *  Må bruke Response<T>, når man snakker med eksterne API-er
 *  Eks: om kun 'LoginResponse' blir brukt på linje 28,
 *       så krasjer appen om svaret f.eks. blir 404 (dvs. ikke 200)
 *
 *  Kun bruk T når man er helt sikker på at API-svar alltid er 200,
 *  dvs. hvis svar allti vil gi OK!
 */

interface ApiService {

    @POST("session")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("bruker")
    suspend fun registrer(
        @Body request: RegistrerRequest
    ): Response<RegistrerResponse>

    @POST("passord/glemt")
    suspend fun glemtPassord(
        @Body request: GlemtPassordRequest
    ): Response<GlemtPassordResponse>

    @GET("brukerInnlogget/{id}")
    suspend fun hentBruker(
        @Path("id") id: String?,
    ): Bruker

    @GET("brukere")
    suspend fun hentAlleBrukere(): List<Bruker>

    @PATCH("brukere/{id}")
    suspend fun oppdaterBruker(
        @Path("id") id: String,
        @Body request: Bruker
    ): Response<Unit>

    @POST("baner")
    suspend fun leggTilBane(
        @Body request: Bane
    ): Response<Unit>

    @GET("baner")
    suspend fun hentBaner(): List<Bane>

    @PATCH("baner/{id}")
    suspend fun oppdaterBane(
        @Path("id") id: String?,
        @Body request: Bane
    ): Response<Unit>

    @DELETE("baner/{id}")
    suspend fun slettBane(
        @Path("id") id: String?
    ): Response<Unit>

    @GET("klubber")
    suspend fun hentAlleKlubber(): Response<List<Any>>
    @POST("klubber")
    suspend fun opprettKlubb(@Body request: KlubbRequest): Response<Any>

    @DELETE("klubber/{id}")
    suspend fun slettKlubb(@Path("id") klubbId: String)


    @POST("/brukere/{id}/poengkort")
    suspend fun lagreScorekort(
        @Path("id") id: String?,
        @Body request: Scorekort
    ): Response<Unit>

    @GET("/brukere/{id}/poengkort")
    suspend fun hentPoengkort(
        @Path("id") id: String?,
    ): List<Scorekort>


    @PATCH("klubber/{id}")
    suspend fun oppdaterKlubb(
        @Path("id") id: String,
        @Body request: KlubbRequest
    ): Response<Any>

    @GET("api/brukere/{id}")
    suspend fun getBruker(
        @Path("id") id: String
    ): Response<Bruker>

    @PATCH("klubber/{id}")
    suspend fun oppdaterMedlemskap(
        @Path("id") klubbId: String,
        @Body request: OppdaterKlubbRequest
    ): Response<Map<String, Any>>

    @GET("klubber/{id}")
    suspend fun hentKlubb(
        @Path("id") klubbId: String
    ): Response<Map<String, Any>>




    // Turnering crud
    @POST("api/mobile/turneringer")
    suspend fun lagTurnering(@Body request: LagTurneringRequest): Response<LagTurneringResponse>

    @GET("api/mobile/turneringer")
    suspend fun hentTurneringer(): Response<List<no.usn.mob3000_gruppe15.model.Turnering>>

    @DELETE("api/mobile/turneringer/{id}")
    suspend fun slettTurnering(
        @Path("id") id: String,
        @Query("userId") userId: String
    ): Response<Unit>

    @PATCH("api/mobile/turneringer/{id}")
    suspend fun oppdaterTurnering(
        @Path("id") id: String,
        @Body request: OppdaterTurneringRequest
    ): Response<Unit>


    // Turnering påmelding
    @POST("api/mobile/turneringer/{id}/pamelding")
    suspend fun meldPaaTurnering(
        @Path("id") id: String,
        @Body request: no.usn.mob3000_gruppe15.model.PameldingRequest
    ): Response<Unit>


    @DELETE("api/mobile/turneringer/{id}/pamelding")
    suspend fun meldAvTurnering(
        @Path("id") id: String,
        @Query("userId") userId: String
    ): Response<Unit>

}

interface VærApiService{
    @GET("compact")
    suspend fun hentVærData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Header("Cache-Control") cacheControl: String = "no-cache"
    ): VærApiResponse
}