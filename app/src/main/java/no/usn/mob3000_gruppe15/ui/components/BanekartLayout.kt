package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.model.HullVelgerViewModel

@Composable
fun BanekartLayout(
    posisjon: CameraPositionState,
    hullListe: List<HullUiState>,
    valgtHull: Int,
    onTeePlassert: (LatLng) -> Unit?,
    onKurvPlassert: (LatLng) -> Unit?,
    onDistanseEndret: (Int) -> Unit?,
    antHull: Int,
    onHullValgt: (Int) -> Unit,
    onMapLoaded: () -> Unit,
    viewModel: HullVelgerViewModel,
    modifier: Modifier,
    mellomKartOgToolbar: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
    ) {
        Banekart(
            posisjon = posisjon,
            hullListe = hullListe,
            valgtHull = valgtHull,
            onMapLoaded = onMapLoaded,
            onTeePlassert = onTeePlassert,
            onKurvPlassert = onKurvPlassert,
            onDistanseEndret = onDistanseEndret,
            modifier = Modifier.weight(1f)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 80.dp)
                .background(MaterialTheme.colorScheme.surface)
                .pointerInput(antHull, valgtHull) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        when {
                            dragAmount < -20 && valgtHull < antHull -> {
                                viewModel.oppdaterValgtHull(valgtHull + 1, antHull)
                            }
                            dragAmount > 20 && valgtHull > 1 -> {
                                viewModel.oppdaterValgtHull(valgtHull - 1, antHull)
                            }
                        }
                    }
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            content = mellomKartOgToolbar
        )
        Spacer(Modifier.padding(20.dp))
        HullTabs(
            antHull = antHull,
            valgtHull = valgtHull,
            onHullValgt = onHullValgt
        )
    }
}