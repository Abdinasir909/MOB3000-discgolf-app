package no.usn.mob3000_gruppe15.local

import android.net.Uri
import com.google.android.gms.maps.model.LatLng
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.Værdata

data class BanerUiState(
    val baner: List<Bane> = emptyList(),
    val mineBaner: List<Bane> = emptyList(),
    val valgtBane: Bane? = null,
    val nyttBanenavn: String = "",
    val nyttAntHull: String = "",
    val nyVanskelighet: String = "",
    val nyBeskrivelse: String = "",
    val nyPosisjonNavn: String = "",
    val nyStartPos: LatLng? = null,
    val hullListe: List<HullUiState> = emptyList(),
    val valgtHull: Int = 1,
    val feilMelding: String = "",
    val værdata: Værdata? = null,
    val nyBildeUri: Uri? = null,
)
