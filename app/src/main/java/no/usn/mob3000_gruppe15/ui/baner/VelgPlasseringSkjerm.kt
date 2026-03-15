package no.usn.mob3000_gruppe15.ui.baner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.rememberCameraPositionState
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.ui.theme.Typography
import no.usn.mob3000_gruppe15.ui.components.Alert
import no.usn.mob3000_gruppe15.ui.components.BanekartLayout
import no.usn.mob3000_gruppe15.ui.components.LabelVelgAntall

@Composable
fun VelgPlasseringSkjerm(
    viewModel: BanerViewModel,
    onNavigerBaner: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val hullListe = uiState.hullListe
    val antHull = uiState.hullListe.size
    val startPosisjon = uiState.nyStartPos
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        hullListe.firstOrNull { it.nummer == uiState.valgtHull }?.let { hull ->
            VelgPlasseringLayout(
                hullListe = hullListe,
                valgtHull = uiState.valgtHull,
                onHullValgt = { valgtHull -> viewModel.oppdaterValgtHull(valgtHull, antHull) },
                onTeePlassert = { pos -> viewModel.oppdaterTee(uiState.valgtHull, pos) },
                onKurvPlassert = { pos -> viewModel.oppdaterKurv(uiState.valgtHull, pos) },
                onParEndret = { par -> viewModel.oppdaterPar(uiState.valgtHull, par) },
                onDistanseEndret = { dist -> viewModel.oppdaterDistanse(uiState.valgtHull) },
                startPosisjon = startPosisjon ?: LatLng(59.9127, 10.7461),
                onAngreHull = { valgtHull -> viewModel.angreHull(valgtHull) },
                feilmelding = uiState.feilMelding,
                antHull = uiState.nyttAntHull,
                isSisteHull = (uiState.nyttAntHull == uiState.valgtHull.toString()),
                viewModel = viewModel,
                onLagreBane = { viewModel.lagreNyBane(context) },
                onNavigerBaner = onNavigerBaner
            )
        }
    }
}

@Composable
fun VelgPlasseringLayout(
    hullListe: List<HullUiState>,
    onTeePlassert: (LatLng) -> Unit,
    onKurvPlassert: (LatLng) -> Unit,
    onParEndret: (Int) -> Unit,
    onDistanseEndret: (Int) -> Unit,
    startPosisjon: LatLng,
    valgtHull: Int,
    onHullValgt: (Int) -> Unit,
    onAngreHull: (Int) -> Unit,
    antHull: String,
    feilmelding: String,
    isSisteHull: Boolean,
    viewModel: BanerViewModel,
    onLagreBane: () -> Unit,
    onNavigerBaner: () -> Unit
) {
    val valgtHullObjekt = hullListe.firstOrNull { it.nummer == valgtHull }
    val startPos: LatLng = valgtHullObjekt?.teePosisjon ?: startPosisjon
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPos, 16f)
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(valgtHull, valgtHullObjekt?.teePosisjon, valgtHullObjekt?.kurvPosisjon, isMapLoaded) {
        if (!isMapLoaded) {
            return@LaunchedEffect
        }

        val teePos = valgtHullObjekt?.teePosisjon
        val kurvPos = valgtHullObjekt?.kurvPosisjon
        if (teePos != null && kurvPos != null) {
            val midtpunkt = SphericalUtil.interpolate(teePos, kurvPos, 0.5)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(midtpunkt, 16f),
                durationMs = 1000
            )
        } else if (teePos != null) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(teePos, 16f),
                durationMs = 1000
            )
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = if (valgtHullObjekt?.teePosisjon == null) {
                stringResource(R.string.tap_og_hold_for_velge_utkastposisjon)
            } else if (valgtHullObjekt.kurvPosisjon == null) {
                stringResource(R.string.tap_og_hold_for_velge_kurvposisjon)
            } else {
                stringResource(R.string.tap_og_hold_p_et_objekt_for_flytte)
            },
            style = Typography.labelLarge
        )
        Text(
            text = feilmelding,
            color = MaterialTheme.colorScheme.error,
            style = Typography.labelLarge
        )
        BanekartLayout(
            posisjon = cameraPositionState,
            hullListe = hullListe,
            valgtHull = valgtHull,
            onTeePlassert = onTeePlassert,
            onKurvPlassert = onKurvPlassert,
            onDistanseEndret = onDistanseEndret,
            antHull = antHull.toInt(),
            onHullValgt = onHullValgt,
            onMapLoaded = { isMapLoaded = true },
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel
        ) {
            ParAvstand(
                distanse = valgtHullObjekt?.distanse ?: 0,
                par = valgtHullObjekt?.par ?: 3,
                onParEndret = onParEndret
            )
            BekreftAngreKnapper(
                onAngreHull = onAngreHull,
                valgtHull = valgtHull,
                onHullValgt = onHullValgt,
                isSisteHull = isSisteHull,
                onLagreBane = onLagreBane,
                onNavigerBaner = onNavigerBaner
            )
        }
    }
}

@Composable
fun ParAvstand(distanse: Int, par: Int, onParEndret: (Int) -> Unit) {
    Column {
        LabelTekstMeter(label = stringResource(R.string.antall_meter), verdi = "${distanse}m")
        LabelVelgAntall(
            label = stringResource(R.string.velg_par),
            verdi = par,
            { onParEndret(par-1) },
             { onParEndret(par+1) }
        )
    }
}

@Composable
fun BekreftAngreKnapper(
    onAngreHull: (Int) -> Unit,
    valgtHull: Int, onHullValgt: (Int) -> Unit,
    isSisteHull: Boolean,
    onLagreBane: () -> Unit,
    onNavigerBaner: () -> Unit
) {
    var visAlert by remember { mutableStateOf(false) }
    if(visAlert == false) {
        Row {
            TextButton(
                onClick = {
                    onAngreHull(valgtHull)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.addcircle),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    stringResource(R.string.angre),
                    style = Typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.padding(4.dp))
            TextButton(
                onClick = {
                    if (!isSisteHull) {
                        onHullValgt(valgtHull + 1)
                    } else {
                        visAlert = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = stringResource(R.string.addcircle),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    stringResource(R.string.bekreft),
                    style = Typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    } else {
        Alert(
            onAngre = { visAlert = false },
            onBekreft = {
                visAlert = false
                onLagreBane()
                onNavigerBaner()
            },
            tittel = stringResource(R.string.bekreft_opprettelse_av_bane),
            innhold = stringResource(R.string.banen_blir_lagret_visst_du_bekrefter),
            icon = Icons.Outlined.QuestionMark
        )
    }
}

@Composable
fun LabelTekstMeter(label: String, verdi: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = Typography.labelLarge)
        Spacer(Modifier.padding(8.dp))
        Text(text = verdi, style = Typography.titleLarge)
    }
}

@Composable
fun IkonMarkor(
    ikonResId: Int,
    erValgt: Boolean,
    nummer: Int? = null
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Icon(
            painter = painterResource(id = ikonResId),
            contentDescription = stringResource(R.string.kartmark_r),
            modifier = Modifier.size(84.dp),
            tint = Color.Unspecified
        )
        if (nummer != null) HullNummerMarkor(nummer, erValgt, 30.dp)
    }
}

@Composable
fun HullNummerMarkor(
    nummer: Int,
    erValgt: Boolean,
    størrelse: Dp
) {
    Box(
        modifier = Modifier
            .size(størrelse)
            .background(if (erValgt) Color.Blue else Color.Black, CircleShape)
            .border(1.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = nummer.toString(),
            color = Color.White,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}




