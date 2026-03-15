package no.usn.mob3000_gruppe15.model

import com.google.android.gms.maps.model.LatLng

data class Hull (
    val nummer: Int,
    val distanse: Int = 0,
    val par: Int = 4,
    val teePosisjon: LatLng? = null,
    val kurvPosisjon: LatLng? = null,
)