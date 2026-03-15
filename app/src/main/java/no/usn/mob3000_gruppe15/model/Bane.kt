package no.usn.mob3000_gruppe15.data.model

import androidx.annotation.DrawableRes
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState

data class Bane(
    @SerializedName("_id")
    val id: String? = null,
    val navn: String,
    val vanskelighet: String,
    val beskrivelse: String,
    val plassering: String,
    val lengde: Double,
    val rating: Double,
    val antHull: Int,
    val hullListe: List<HullUiState> = emptyList(),
    val koordinater: LatLng? = null,
    val bilde: String? = "",
    @DrawableRes val imageResourceId: Int = R.drawable.bane,
    val eier: String? = ""
)
