package no.usn.mob3000_gruppe15.ui.baner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.components.Alert

@Composable
fun RedigerBane(
    onNavigerRedigerBaneInfo: () -> Unit,
    onNavigerRedigerHullPosisjon: () -> Unit,
    viewModel: BanerViewModel,
    onNavigerTilbake: () -> Unit
) {
    var visAlert by remember { mutableStateOf(false) }

    if (! visAlert) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onNavigerRedigerBaneInfo() }
            ) {
                Text(text = stringResource(R.string.rediger_baneinfo))
            }
            Button(
                onClick = { onNavigerRedigerHullPosisjon() }
            ) {
                Text(text = stringResource(R.string.rediger_hullposisjoner))
            }
            Spacer(
                modifier = Modifier.padding(20.dp)
            )
            Button(
                onClick = { visAlert = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = stringResource(R.string.slett_bane))
            }
        }
    } else {
        Alert(
            onAngre = { visAlert = false },
            onBekreft = {
                visAlert = false
                viewModel.slettBane()
                onNavigerTilbake()
            },
            tittel = stringResource(R.string.bekreft_sletting_av_bane),
            innhold = stringResource(R.string.er_du_sikker_p_at_du_vil_slette_denne_banen),
            icon = Icons.Outlined.Warning
        )
    }
}