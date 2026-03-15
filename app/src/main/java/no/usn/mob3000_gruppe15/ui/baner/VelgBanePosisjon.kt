package no.usn.mob3000_gruppe15.ui.baner

import no.usn.mob3000_gruppe15.ui.components.placesAutoCompleteSok
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.widget.PlaceAutocomplete
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.BanerUiState
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun VelgBanePosisjon(
    viewModel: BanerViewModel,
    onNavigerVelgBaneInfo: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(viewModel.startKartView, 5f)
    }

    val context = LocalContext.current
    val launcher = placesAutoCompleteSok(
        cameraPositionState,
        onPlassValgt = {sted -> viewModel.oppdaterPosisjonNavn(sted.displayName)}
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = if(uiState.nyPosisjonNavn == "") {
                stringResource(R.string.trykk_velg_sted_for_velge_sted)
            } else {
                stringResource(R.string.trykk_p_skjermen_for_velge_posisjon)
            },
            style = Typography.labelLarge
        )
        Text(
            text = uiState.feilMelding,
            style = Typography.labelLarge,
            color = MaterialTheme.colorScheme.error
        )
        Button (
            onClick = {
                val intent = PlaceAutocomplete.createIntent(context)
                launcher.launch(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = stringResource(R.string.s_k),
            )
            Text(text = stringResource(R.string.velg_sted))
        }

        Box(modifier = Modifier.weight(1f)) {
            Kart(
                onStartPosisjonValgt = { pos -> viewModel.oppdaterBaneposisjon(pos) },
                posisjon = cameraPositionState,
                bane = uiState
            )
        }

        Button(
            onClick = {
                if (uiState.nyStartPos != null &&
                    uiState.nyPosisjonNavn != ""
                    ) {
                    viewModel.oppdaterFeilmelding("")
                    onNavigerVelgBaneInfo()
                } else if(uiState.nyPosisjonNavn != "") {
                    viewModel.oppdaterFeilmelding(context.getString(R.string.velg_posisjon_for_g_videre))
                } else {
                    viewModel.oppdaterFeilmelding(context.getString(R.string.velg_plass_for_g_videre))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = stringResource(R.string.bekreft_posisjon))
        }
    }
}

@Composable
fun Kart(
    onStartPosisjonValgt: (LatLng) -> (Unit),
    posisjon: CameraPositionState,
    bane: BanerUiState
) {
    var isMapLoaded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = posisjon,
            onMapLoaded = { isMapLoaded = true },
            onMapClick = { latLng ->
                onStartPosisjonValgt(latLng)
            }
        ) {
            bane.nyStartPos?.let { pos ->
                val startPosState = rememberUpdatedMarkerState(position = pos)
                Marker(
                    state = startPosState,
                    title = stringResource(R.string.valgt_posisjon)
                )
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