package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.ui.baner.HullNummerMarkor
import no.usn.mob3000_gruppe15.ui.baner.IkonMarkor

@Composable
fun Banekart(
    posisjon: CameraPositionState,
    hullListe: List<HullUiState>,
    onMapLoaded: () -> Unit,
    valgtHull: Int,
    onTeePlassert: (LatLng) -> Unit?,
    onKurvPlassert: (LatLng) -> Unit?,
    onDistanseEndret: (Int) -> Unit?,
    modifier: Modifier
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    var visDetaljer by remember { mutableStateOf(false) }
    val aggreGeringGrense = 15f
    visDetaljer = posisjon.position.zoom > aggreGeringGrense
    Box(modifier = modifier.fillMaxWidth()) {
        GoogleMap(
            cameraPositionState = posisjon,
            onMapLoaded = {
                isMapLoaded = true
                onMapLoaded()
            },
            onMapLongClick = { latLng ->
                val valgt = hullListe.firstOrNull { it.nummer == valgtHull }
                if (valgt?.teePosisjon == null) onTeePlassert(latLng)
                else if (valgt.kurvPosisjon == null) onKurvPlassert(latLng)
            }
        ) {
            if (visDetaljer) {
                hullListe.forEach { hull ->
                    val erValgt = hull.nummer == valgtHull
                    val zIndex = if (erValgt) 1f else 0f
                    hull.teePosisjon?.let { pos ->
                        key(hull.nummer, stringResource(R.string.tee), erValgt) {
                            val teeState = rememberMarkerState(position = pos)
                            LaunchedEffect(teeState.position) {
                                if (teeState.position != pos && hull.nummer == valgtHull) onTeePlassert(teeState.position)
                            }
                            MarkerComposable(
                                state = teeState,
                                keys = arrayOf(hull.nummer),
                                draggable = hull.nummer == valgtHull,
                                zIndex = zIndex
                            ) {
                                IkonMarkor(
                                    R.drawable.teemarkor,
                                    erValgt,
                                    hull.nummer)
                            }
                        }
                    }
                    hull.kurvPosisjon?.let { pos ->
                        key(hull.nummer, stringResource(R.string.kurv), erValgt) {
                            val kurvState = rememberMarkerState(position = pos)
                            LaunchedEffect(kurvState.position) {
                                if (kurvState.position != pos && hull.nummer == valgtHull) onKurvPlassert(kurvState.position)
                            }
                            MarkerComposable(
                                state = kurvState,
                                keys = arrayOf(hull.nummer),
                                draggable = hull.nummer == valgtHull,
                                zIndex = zIndex
                            ) {
                                IkonMarkor(
                                    R.drawable.kurvmarkor,
                                    erValgt,
                                    hull.nummer)
                            }
                        }
                    }
                    if (hull.teePosisjon != null && hull.kurvPosisjon != null) {
                        key(hull.nummer, stringResource(R.string.kurv), erValgt) {
                            LaunchedEffect(
                                hull.teePosisjon,
                                hull.kurvPosisjon
                            ) { onDistanseEndret(hull.nummer) }
                            Polyline(
                                points = listOf(hull.teePosisjon, hull.kurvPosisjon),
                                color = if(erValgt) Color.Blue else MaterialTheme.colorScheme.outline,
                                zIndex = zIndex,
                                width = 10f
                            )
                            val midtpunkt = SphericalUtil.interpolate(
                                hull.teePosisjon,
                                hull.kurvPosisjon,
                                0.5
                            )
                            key(hull.nummer, stringResource(R.string.punkt), erValgt) {
                                MarkerComposable(
                                    keys = arrayOf(hull.nummer),
                                    state = rememberUpdatedMarkerState(position = midtpunkt),
                                    anchor = Offset(0.5f, 0.5f),
                                    zIndex = zIndex
                                ) {
                                    HullNummerMarkor(
                                        hull.nummer,
                                        erValgt,
                                        30.dp
                                    )
                                }
                            }
                        }
                    }
                }
                hullListe.windowed(2).forEach { par ->
                    val kurv = par[0].kurvPosisjon
                    val teeNeste = par[1].teePosisjon
                    if (kurv != null && teeNeste != null) {
                        Polyline(
                            points = listOf(kurv, teeNeste),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            width = 6f,
                            pattern = listOf(Gap(20f), Dash(30f))
                        )
                    }
                }
            } else {
                hullListe.forEach { hull ->
                    if (hull.teePosisjon != null && hull.kurvPosisjon != null) {
                        val erValgt = valgtHull == hull.nummer
                        MarkerComposable(
                            state = rememberMarkerState(position = hull.teePosisjon),
                            anchor = Offset(0.5f, 0.5f)
                        ) {
                            HullNummerMarkor(hull.nummer, erValgt, 30.dp)
                        }
                    }
                }
            }
        }
        if (!isMapLoaded) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}