package no.usn.mob3000_gruppe15.ui.baner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.IconLabel
import no.usn.mob3000_gruppe15.model.Værdata
import no.usn.mob3000_gruppe15.ui.components.BaneBilde
import no.usn.mob3000_gruppe15.ui.components.IconLabels
import no.usn.mob3000_gruppe15.ui.components.VindVisning

@Composable
fun BanedetaljerSkjerm(
    bane: Bane,
    viewModel: BanerViewModel,
    onNavigerTilOppsett: () -> Unit
) {
    LaunchedEffect(bane.id) {
        viewModel.hentVærdata(bane)
    }
    val uiState by viewModel.uiState.collectAsState()
    BaneInfoSkjerm(
        bane,
        onNavigerTilOppsett,
        værdata = uiState.værdata,
        modifier = Modifier.padding()
    )
}

@Composable
fun BaneInfoSkjerm(
    bane: Bane,
    onNavigerTilOppsett: () -> Unit,
    værdata: Værdata?,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            all = 25.dp
        ),
        verticalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        item {
            Værkort(
                værdata
            )
        }
        item {
            BaneInfoKort(bane, onNavigerTilOppsett)
        }
    }
}

@Composable
fun Værkort(værdata: Værdata?) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ) {
        if (værdata == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Image(
                        painter = painterResource(R.drawable.yrlogo),
                        contentDescription = stringResource(R.string.yrlogo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.padding(6.dp))
                    VærInfo(
                        temperature = "${værdata.temperatur}°C",
                        vindHastighet = værdata.vindHastighet,
                        vindRetningGrader = værdata.vindRetningGrader
                    )
                }
                SubcomposeAsyncImage(
                    model = stringResource(
                        R.string.https_github_com_metno_weathericons_blob_main_weather_png_png_raw_true,
                        værdata.symbolCode
                    ),
                    contentDescription = værdata.symbolCode,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                    ,
                    loading = {
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BaneInfoKort(
    bane: Bane,
    onNavigerTilOppsett: () -> Unit
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp)
        ) {
            Text(
                text = bane.navn,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        BaneBilde(bane = bane)
        Banebeskrivelse(
            bane
        )
        Button(
            onClick = { onNavigerTilOppsett() },
            modifier = Modifier
                .align(Alignment.End)
                .padding(
                    end = 16.dp,
                    bottom = 16.dp
                )
        ) {
            Text(
                text = stringResource(R.string.start_ny_runde)
            )
        }
    }
}

@Composable
fun Banebeskrivelse(
    bane: Bane
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconLabels(
            listOf(
                IconLabel(Icons.Outlined.AddCircle, label = bane.antHull.toString() + " " + stringResource(R.string.hull, bane.antHull
                )),
                IconLabel(Icons.Outlined.Place, label = bane.lengde.toString() + stringResource(R.string.km)),
                IconLabel(Icons.Outlined.Star, label = bane.rating.toString())
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.vanskelighet),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${bane.vanskelighet}"
            )
        }
        Text(
            text = stringResource(R.string.beskrivelse),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = bane.beskrivelse
        )
    }
}

@Composable
fun VærInfo(
    temperature: String,
    vindHastighet: Double,
    vindRetningGrader: Double
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = temperature
        )
        Spacer(modifier = Modifier.padding(5.dp))
        VindVisning(
            vindHastighet,
            vindRetningGrader
        )
    }
}

@Composable
fun Anmeldelser() {

}