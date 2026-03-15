package no.usn.mob3000_gruppe15.ui.baner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.IconLabel
import no.usn.mob3000_gruppe15.ui.components.IconLabels
import androidx.compose.ui.res.stringResource
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.components.BaneBilde

@Composable
fun BanerSkjerm(
    viewModel: BanerViewModel,
    onNavigerTilDetaljer: (Bane) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var søk by remember { mutableStateOf("") }

    val filtrert = remember(uiState.baner, søk) {
        if (søk.isBlank()) uiState.baner
        else uiState.baner.filter { bane ->
            bane.navn.contains(søk, ignoreCase = true) ||
                    bane.plassering.contains(søk, ignoreCase = true)
        }
    }

    if(!uiState.baner.isEmpty()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = søk,
                onValueChange = { søk = it },
                label = { Text(stringResource(R.string.s_k_etter_baner)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(percent = 50)
            )
            BaneListe(
                banerListe = filtrert,
                onBaneClicked = onNavigerTilDetaljer as (Bane) -> Unit
            )
        }
    } else {
        Text(
            text = stringResource(R.string.ingen_baner_er_opprettet) +
                    stringResource(R.string.trykk_legg_til_bane_for_opprette_en_bane),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun BaneListe(onBaneClicked: (Bane) -> Unit, banerListe: List<Bane>, modifier: Modifier = Modifier) {
    if(!banerListe.isEmpty()) {
        LazyColumn(modifier = modifier) {
            items(banerListe) { bane ->
                BaneKort(
                    bane = bane,
                    onBaneClicked = { onBaneClicked(bane) },
                    iconLabels = listOf(
                        IconLabel(Icons.Outlined.AddCircle, label = bane.antHull.toString() + " " + stringResource(R.string.hull)),
                        IconLabel(Icons.Outlined.Place, label = bane.lengde.toString() + " " + stringResource(R.string.km)),
                        IconLabel(Icons.Outlined.Star, label = bane.rating.toString())
                    )
                )
                Spacer(Modifier.padding(12.dp))
            }
        }
    } else {
        Text(
            text = stringResource(R.string.ingen_treff),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun BaneKort(
    bane: Bane,
    onBaneClicked: () -> Unit,
    iconLabels: List<IconLabel>
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = { onBaneClicked() },
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 12.dp, start = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            BaneBilde(
                bane = bane
            )
            Text(
                text = bane.navn,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top =4.dp)
            )
            BaneDetaljer(
                bane.vanskelighet,
                bane.plassering,
                iconLabels = iconLabels
            )
        }
    }
}

@Composable
fun BaneDetaljer(
    vanskelighet: String,
    plassering: String,
    iconLabels: List<IconLabel>
)
{
    Column (
        modifier = Modifier
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = plassering
            )
            IconLabels(iconLabels)
        }
        Text(
            text = stringResource(R.string.vanskelighetsgrad, vanskelighet),
            modifier = Modifier.padding(top = 12.dp, bottom = 12.dp)
        )
    }
}


