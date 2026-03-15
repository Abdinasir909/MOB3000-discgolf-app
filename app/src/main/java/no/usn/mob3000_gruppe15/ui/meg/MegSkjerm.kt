package no.usn.mob3000_gruppe15.ui.meg

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.SpillUiState
import no.usn.mob3000_gruppe15.model.Scorekort
import no.usn.mob3000_gruppe15.ui.spill.SpillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MegSkjerm(
    navController: NavController,
    spillViewModel: SpillViewModel,
    onNavigerRundeOppsummering: () -> Unit,
    megViewModel: MegViewModel
) {
    val spillUiState = spillViewModel.uiState.collectAsState()

    MegSkjermContent(
        navController = navController,
        viewModel = megViewModel,
        spillViewModel = spillViewModel,
        spillUiState = spillUiState,
        onNavigerRundeOppsummering = onNavigerRundeOppsummering
    )
}

@Composable
fun MegSkjermContent(
    navController: NavController,
    viewModel: MegViewModel,
    spillViewModel: SpillViewModel,
    spillUiState: State<SpillUiState>,
    onNavigerRundeOppsummering: () -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                HorizontalDivider()
                HorizontalDivider()
            }
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding()) // <-- KI linje!
            /* Måtte spørre ChatGPT om hjelp til å fjerne plassen som
             * var reservert i bunnen av LazyColumn. (reservert for NavBar)
             * Plassen var tom/ble ikke brukt pga. vi har en annen løsning.
             */
        ) {
            //Profilinfo (brukernavn & link til settings)
            item {
                Brukernavn(
                    navController,
                    brukernavn = viewModel.brukernavn,
                    epost = viewModel.epost,
                    onSettingsClick = {
                        navController.navigate("MegRediger")
                    }
                )
            }

            //Brukerstatistikk
            item {
                BrukerStatistikk(
                    antallRunder = spillUiState.value.antallRunder,
                    gjennomsnitteligScore = spillUiState.value.gjennomsnitteligScore,
                    totalKast = spillUiState.value.totalKast,
                    gjennomsnitteligKast = spillUiState.value.gjennomsnitteligKast,
                    bestScore = spillUiState.value.bestScore,
                    antHoleInOne = spillUiState.value.antHoleInOne
                )
            }

            //Overskrift for tidligere spilte runder
            item {
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.my_rounds),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            if(!spillUiState.value.poengKortListe.isEmpty()) {
                //Rundekort
                items(spillUiState.value.poengKortListe) { poengkort ->
                    RundeKort(
                        poengkort,
                        onValgtPoengkort = {
                            spillViewModel.oppdaterValgtPoengkort(poengkort)
                            onNavigerRundeOppsummering()
                        }
                    )
                }
            } else {
                item {
                    Text(
                        stringResource(R.string.spill_en_runde_for_se_dine_runder_her),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

//Komponent som viser info om bruker (navn + link til rediger-side)
@Composable
fun Brukernavn(
    navController: NavController,
    brukernavn: String,
    epost: String,
    onSettingsClick: () -> Unit
) {
   OutlinedCard(
       modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 16.dp, vertical = 12.dp)
   ) {
       Row(
           modifier = Modifier
               .fillMaxWidth()
               .padding(16.dp),
           verticalAlignment = Alignment.CenterVertically,
           horizontalArrangement = Arrangement.SpaceBetween
       ) {
           Row(verticalAlignment = Alignment.CenterVertically) {
               Icon(
                   imageVector = Icons.Default.Person,
                   contentDescription = stringResource(R.string.profile_picture),
                   modifier = Modifier
                       .size(48.dp)
                       .clip(CircleShape)
                       .padding(end = 12.dp)
               )
               Column {
                   Text(
                       text = brukernavn,
                       style = MaterialTheme.typography.titleLarge
                   )
                   Text(
                       text = epost,
                       style = MaterialTheme.typography.bodyLarge
                   )
               }
           }
           IconButton(onClick = onSettingsClick) {
               Icon(
                   imageVector = Icons.Default.Settings,
                   contentDescription = stringResource((R.string.settings))
               )
           }
       }
   }
}


@Composable
fun BrukerStatistikk(
    antallRunder: Int,
    gjennomsnitteligScore: Int,
    totalKast: Int,
    gjennomsnitteligKast: Int,
    bestScore: Int,
    antHoleInOne: Int
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.rounds),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = antallRunder.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.gj_score),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = gjennomsnitteligScore.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.total_ant_kast),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = totalKast.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.gj_kast_pr_runde),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = gjennomsnitteligKast.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.best_score),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = bestScore.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.ant_hole_in_one),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = antHoleInOne.toString(),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

//Kort som viser tidligere spilte runder
@Composable
fun RundeKort(
    poengkort: Scorekort,
    onValgtPoengkort: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onValgtPoengkort),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.bane),
                contentDescription = stringResource(R.string.course_photo),
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = poengkort.bane.navn,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.sammenlagt) + poengkort.spillere[0].sammenlagt,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.hole_colon) + " " + poengkort.bane.hullListe.size +
                            " " + stringResource(R.string.disk_throw_colon) + " " + poengkort.spillere[0].antKastTotal,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


/*
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MegSkjermPreview() {
    DiskgolfTheme {
        val dummyNavController = rememberNavController()
        MegSkjerm(navController = dummyNavController)
    }
}*/
