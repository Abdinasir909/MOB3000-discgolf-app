package no.usn.mob3000_gruppe15.ui.baner


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun VelgBaneForRedigeringSkjerm(
    viewModel: BanerViewModel,
    onNavigerTilRedigerBaneinfo: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var søk by remember { mutableStateOf("") }


    val filtrert = remember(uiState.mineBaner, søk) {
        if (søk.isBlank()) uiState.mineBaner
        else uiState.mineBaner.filter { bane ->
            bane.navn.contains(søk, ignoreCase = true) ||
                    bane.plassering.contains(søk, ignoreCase = true)
        }
    }

    if(!uiState.mineBaner.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = stringResource(R.string.velg_en_av_dine_baner_for_redigering),
                style = Typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            OutlinedTextField(
                value = søk,
                onValueChange = { søk = it },
                label = { Text(stringResource(R.string.s_k_etter_baner_)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(percent = 50)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtrert) { bane ->
                    BaneKort(
                        bane = bane,
                        onBaneClicked = {
                            viewModel.velgBane(bane)
                            onNavigerTilRedigerBaneinfo()
                        },
                        iconLabels = listOf()
                    )
                }
            }
        }
    } else {
        Text(
            text = stringResource(R.string.ingen_baner_er_registrert_p_din_bruker) +
                    stringResource(R.string.trykk_legg_til_bane_for_opprette_en_bane),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}