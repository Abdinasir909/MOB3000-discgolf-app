package no.usn.mob3000_gruppe15.ui.baner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.rememberCameraPositionState
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.ui.components.Alert
import no.usn.mob3000_gruppe15.ui.components.BanekartLayout
import no.usn.mob3000_gruppe15.ui.components.LabelVelgAntall
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun RedigerHullPosisjonSkjerm(
    viewModel: BanerViewModel,
    onNavigerTilbake: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        uiState.valgtBane?.let { bane ->
            viewModel.settHullListeFraEksisterendeBane(bane.hullListe)
        }
    }

    val hullListe = uiState.hullListe
    val antHull = hullListe.size

    if (hullListe.isEmpty()) {
        Text(stringResource(R.string.ingen_hull_redigere))
        return
    }

    val startPosisjon = uiState.valgtBane?.koordinater ?: LatLng(59.9127, 10.7461)
    val isSisteHull = uiState.valgtHull == antHull

    Column(modifier = Modifier.fillMaxSize()) {
        hullListe.firstOrNull { it.nummer == uiState.valgtHull }?.let { hull ->
            RedigerHullLayout(
                hullListe = hullListe,
                valgtHull = uiState.valgtHull,
                onHullValgt = { valgtHull -> viewModel.oppdaterValgtHull(valgtHull, antHull) },
                onTeePlassert = { pos -> viewModel.oppdaterTee(uiState.valgtHull, pos) },
                onKurvPlassert = { pos -> viewModel.oppdaterKurv(uiState.valgtHull, pos) },
                onParEndret = { par -> viewModel.oppdaterPar(uiState.valgtHull, par) },
                onDistanseEndret = { dist -> viewModel.oppdaterDistanse(uiState.valgtHull) },
                startPosisjon = startPosisjon,
                feilmelding = uiState.feilMelding,
                antHull = antHull,
                isSisteHull = isSisteHull,
                viewModel = viewModel,
                onLagreEndringer = { viewModel.lagreRedigertBane() },
                onAvbryt = onNavigerTilbake,
                onNavigerTilbake = onNavigerTilbake
            )
        }
    }
}

@Composable
fun RedigerHullLayout(
    hullListe: List<HullUiState>,
    onTeePlassert: (LatLng) -> Unit,
    onKurvPlassert: (LatLng) -> Unit,
    onParEndret: (Int) -> Unit,
    onDistanseEndret: (Int) -> Unit,
    startPosisjon: LatLng,
    valgtHull: Int,
    onHullValgt: (Int) -> Unit,
    antHull: Int,
    feilmelding: String,
    isSisteHull: Boolean,
    viewModel: BanerViewModel,
    onLagreEndringer: () -> Unit,
    onAvbryt: () -> Unit,
    onNavigerTilbake: () -> Unit
) {
    val valgtHullObjekt = hullListe.firstOrNull { it.nummer == valgtHull }
    val startPos: LatLng = valgtHullObjekt?.teePosisjon ?: startPosisjon
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPos, 16f)
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(valgtHull, valgtHullObjekt?.teePosisjon, valgtHullObjekt?.kurvPosisjon, isMapLoaded) {
        if (! isMapLoaded) return@LaunchedEffect

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
            text = stringResource(R.string.tap_og_hold_p_tee_eller_kurv_for_flytte_posisjon),
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
            antHull = antHull,
            onHullValgt = onHullValgt,
            onMapLoaded = { isMapLoaded = true },
            modifier = Modifier.fillMaxWidth(),
            viewModel = viewModel
        ) {
            ParAvstandRedigering(
                distanse = valgtHullObjekt?.distanse ?: 0,
                par = valgtHullObjekt?.par ?: 3,
                onParEndret = onParEndret
            )
            LagreAvbrytKnapper(
                onAvbryt = onAvbryt,
                valgtHull = valgtHull,
                onHullValgt = onHullValgt,
                isSisteHull = isSisteHull,
                onLagreEndringer = onLagreEndringer,
                onNavigerTilbake = onNavigerTilbake
            )
        }
    }
}

@Composable
fun ParAvstandRedigering(distanse: Int, par: Int, onParEndret: (Int) -> Unit) {
    Column {
        LabelTekstMeter(label = stringResource(R.string.avstand), verdi = "${distanse}m")
        LabelVelgAntall(
            label = stringResource(R.string.par),
            verdi = par,
            onDekrement = { onParEndret(par - 1) },
            onInkrement = { onParEndret(par + 1) }
        )
    }
}

@Composable
fun LagreAvbrytKnapper(
    onAvbryt: () -> Unit,
    valgtHull: Int,
    onHullValgt: (Int) -> Unit,
    isSisteHull: Boolean,
    onLagreEndringer: () -> Unit,
    onNavigerTilbake: () -> Unit
) {
    var visAlert by remember { mutableStateOf(false) }

    if (!visAlert) {
        Row {
            TextButton(
                onClick = onAvbryt,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.avbryt_rediger),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    stringResource(R.string.avbryt_rediger),
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
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = if (isSisteHull) stringResource(R.string._lagre) else "Bekreft",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    if (isSisteHull) stringResource(R.string.lagre_endringer_) else stringResource(R.string.bekreft),
                    style = Typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    } else {
        Alert(
            onAngre = { visAlert = false },
            onBekreft = {
                visAlert = false
                onLagreEndringer()
                onNavigerTilbake()
            },
            tittel = stringResource(R.string.bekreft_lagring_av_endringer_),
            innhold = stringResource(R.string.endringene_p_banen_blir_lagret_hvis_du_bekrefter),
            icon = Icons.Outlined.QuestionMark
        )
    }
}