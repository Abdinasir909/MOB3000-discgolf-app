package no.usn.mob3000_gruppe15.ui.spill

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Straight
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.IconLabel
import no.usn.mob3000_gruppe15.model.Spiller
import no.usn.mob3000_gruppe15.ui.components.Alert
import no.usn.mob3000_gruppe15.ui.components.BanekartLayout
import no.usn.mob3000_gruppe15.ui.components.IconLabels
import no.usn.mob3000_gruppe15.ui.components.LabelVelgAntall
import no.usn.mob3000_gruppe15.ui.components.ScorekortLayout

@SuppressLint("SuspiciousIndentation")
@Composable
fun SpillRundeSkjerm(
    spillViewModel: SpillViewModel,
    bane: Bane,
    onFerdigSpill: () -> Unit
) {
    val uiState by spillViewModel.uiState.collectAsState()
    val hullListe = bane.hullListe
    spillViewModel.oppdaterHulliste(hullListe)
    val startPosisjon = bane.koordinater
    val valgtHull = uiState.valgtHull
    val antHull = hullListe.size
    var visBekreftRunde by remember { mutableStateOf(false) }
    var visOppsummering by remember { mutableStateOf(false) }
    var visBekreftKnapp by remember { mutableStateOf(false) }
    if(valgtHull == hullListe.size) visBekreftKnapp = true
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if(!visOppsummering) {
                hullListe.firstOrNull() { it.nummer == valgtHull }?.let { hull ->
                    SpillLayout(
                        startPos = startPosisjon ?: LatLng(59.9127, 10.7461),
                        hullListe = hullListe,
                        valgtHull = valgtHull,
                        antHull = antHull,
                        onHullValgt = { valgtHull ->
                            spillViewModel.oppdaterValgtHull(
                                valgtHull,
                                antHull
                            )
                        },
                        spillere = uiState.spillere,
                        spillViewModel = spillViewModel,
                        onVisBekreft = { visBekreftRunde = true },
                        onAngre = { visBekreftRunde = false },
                        onBekreft = {
                            visBekreftRunde = false
                            visBekreftKnapp = false
                            visOppsummering = true
                        },
                        onValiderKastFørAvslutning = { spillViewModel.validerKastFørAvslutning() },
                        visBekreftRunde = visBekreftRunde,
                        visBekreftKnapp = visBekreftKnapp,
                        feilmelding = uiState.feilmelding
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.rundeoppsummering),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        ScorekortLayout(
                            hullListe,
                            uiState.spillere
                        )
                    }
                    Button(
                        onClick = { onFerdigSpill() }, 
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(stringResource(R.string.lagre_runde_og_avslutt))
                    }
                }
            }
    }
}

@Composable
fun SpillLayout(
    startPos: LatLng,
    hullListe: List<HullUiState>,
    valgtHull: Int,
    antHull: Int,
    onHullValgt: (Int) -> Unit,
    spillere: List<Spiller>,
    spillViewModel: SpillViewModel,
    onAngre: () -> Unit,
    onBekreft: () -> Unit,
    visBekreftRunde: Boolean,
    onVisBekreft: () -> Unit,
    visBekreftKnapp: Boolean,
    feilmelding: String,
    onValiderKastFørAvslutning: () -> Boolean
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPos, 16f)
    }

    var isMapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(valgtHull, isMapLoaded) {
        if (!isMapLoaded) {
            return@LaunchedEffect
        }

        val hullIndex = valgtHull - 1
        if (hullIndex >= 0 && hullIndex < hullListe.size) {
            val nyPosisjon = hullListe[hullIndex].teePosisjon ?: startPos
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(nyPosisjon),
                durationMs = 700
            )
        }
    }
    IconLabels(
        listOf(
            IconLabel(Icons.Outlined.AddCircle, label = "Hull ${valgtHull} |"),
            IconLabel(Icons.Outlined.Straight, label = "${hullListe[valgtHull-1].distanse}m |"),
            IconLabel(Icons.Outlined.Star, label = "Par ${hullListe[valgtHull-1].par}")
        )
    )
    BanekartLayout(
        posisjon = cameraPositionState,
        hullListe = hullListe,
        valgtHull = valgtHull,
        antHull = antHull,
        onHullValgt = onHullValgt,
        onMapLoaded = { isMapLoaded = true },
        onTeePlassert = {},
        onKurvPlassert = {},
        onDistanseEndret = {},
        viewModel = spillViewModel,
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints {
            val maksListeHøyde = this.maxHeight / 2

            SpillerListeScore(
                spillere = spillere,
                valgtHull = valgtHull,
                spillViewModel = spillViewModel,
                antHull = antHull,
                hullListe = hullListe,
                onAngre = onAngre,
                onBekreft = onBekreft,
                onVisBekreft = onVisBekreft,
                visBekreftRunde = visBekreftRunde,
                visBekreftKnapp = visBekreftKnapp,
                modifier = Modifier.heightIn(max = maksListeHøyde),
                feilmelding = feilmelding,
                onValiderKastFørAvslutning = onValiderKastFørAvslutning
            )
        }
    }
}



@Composable
fun SpillerListeScore(
    spillere: List<Spiller>,
    valgtHull: Int,
    spillViewModel: SpillViewModel,
    antHull: Int,
    hullListe: List<HullUiState>,
    onAngre: () -> Unit,
    onBekreft: () -> Unit,
    visBekreftRunde: Boolean,
    modifier: Modifier = Modifier,
    onVisBekreft: () -> Unit,
    visBekreftKnapp: Boolean,
    feilmelding: String,
    onValiderKastFørAvslutning: () -> Boolean
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 80.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if(!visBekreftRunde) {
            LazyColumn(
            ) {
                items(spillere) { spiller ->
                    HorizontalDivider()
                    ListItem(
                        headlineContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = if (spiller.sammenlagt == 0) {
                                            "E(${spiller.antKastTotal})"
                                        } else if (spiller.sammenlagt > 0) {
                                            "+${spiller.sammenlagt}(${spiller.antKastTotal})"
                                        } else {
                                            "${spiller.sammenlagt}(${spiller.antKastTotal})"
                                        }
                                    )
                                    Text(text = spiller.navn)
                                }
                                LabelVelgAntall(
                                    label = null,
                                    verdi = spiller.antallKast?.getOrNull(valgtHull - 1) ?: 0,
                                    onDekrement = {
                                        spillViewModel.oppdaterAntallKast(
                                            valgtHull,
                                            (spiller.antallKast?.getOrNull(valgtHull - 1) ?: 0) - 1,
                                            spiller,
                                            hullListe = hullListe
                                        )
                                    },
                                    onInkrement = {
                                        spillViewModel.oppdaterAntallKast(
                                            valgtHull,
                                            (spiller.antallKast?.getOrNull(valgtHull - 1) ?: 0) + 1,
                                            spiller,
                                            hullListe = hullListe
                                        )
                                    }
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                Icons.Outlined.AccountCircle,
                                contentDescription = stringResource(R.string._accountsirkel),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                            )
                        }
                    )
                }
            }
            if(visBekreftKnapp) {
                Button(
                    onClick = {
                        onVisBekreft()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.fullf_r_runde)
                    )
                }
            }
        } else if(!onValiderKastFørAvslutning()){
            Alert(
                onAngre = onAngre,
                onBekreft = onBekreft,
                tittel = stringResource(R.string.fullf_r_runde),
                innhold = feilmelding,
                icon = Icons.Outlined.QuestionMark
            )
        } else {
            Alert(
                onAngre = onAngre,
                onBekreft = onBekreft,
                tittel = stringResource(R.string.fullf_r_runde),
                innhold = stringResource(R.string.trykk_bekreft_for_fullf_re),
                icon = Icons.Outlined.QuestionMark
            )
        }
        Spacer(Modifier.padding(20.dp))
    }
}

