package no.usn.mob3000_gruppe15.ui.spill

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.model.Bruker
import no.usn.mob3000_gruppe15.model.Spiller
import no.usn.mob3000_gruppe15.ui.components.AlertInput
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun RundeOppsettSkjerm(
    bane: Bane,
    onNavigerTilSpillRunde: () -> Unit,
    viewModel: SpillViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    var visGjestDialog by remember { mutableStateOf(false) }
    var visBrukerDialog by remember { mutableStateOf(false) }

    when {
        visGjestDialog -> {
            AlertInput(
                value = uiState.nyttGjesteSpillerNavn,
                label = stringResource(R.string.gjestespillerens_navn_),
                onOppdaterValue = { viewModel.oppdaterGjestespillerNavn(it) },
                onAngre = { visGjestDialog = false },
                onBekreft = {
                    viewModel.leggTilGjestespiller(uiState.nyttGjesteSpillerNavn)
                    visGjestDialog = false
                },
                tittel = stringResource(R.string.gjestespiller),
                innhold = stringResource(R.string.skriv_inn_gjestespillerens_navn),
                icon = Icons.Outlined.PersonAdd,
            )
        }
        visBrukerDialog -> {
            BrukerSøkDialog(
                søkeord = uiState.søkBruker,
                søkeResultater = viewModel.filtrerBrukere(),
                onSøkEndret = { viewModel.oppdaterBrukerSøk(it) },
                onVelgBruker = { bruker ->
                    viewModel.leggTilBruker(bruker)
                    visBrukerDialog = false
                },
                onLukk = { visBrukerDialog = false }
            )
        }
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StartSpillLabels(bane = bane, modifier = Modifier.weight(1f))

                Text(
                    text = uiState.feilmelding,
                    style = Typography.labelLarge,
                    color = MaterialTheme.colorScheme.error
                )

                SpillerListe(
                    spillere = uiState.spillere,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { visGjestDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        Icons.Outlined.PersonAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        stringResource(R.string.legg_til_gjestespiller_),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Button(
                    onClick = {
                        viewModel.hentAlleBrukere()
                        visBrukerDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        stringResource(R.string.legg_til_en_annen_bruker),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Button(
                    onClick = { onNavigerTilSpillRunde() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(stringResource(R.string.spill),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun BrukerSøkDialog(
    søkeord: String,
    søkeResultater: List<Bruker>,
    onSøkEndret: (String) -> Unit,
    onVelgBruker: (Bruker) -> Unit,
    onLukk: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onLukk,
        title = { Text(stringResource(R.string.legg_til_bruker)) },
        text = {
            Column {
                OutlinedTextField(
                    value = søkeord,
                    onValueChange = onSøkEndret,
                    label = { Text(stringResource(R.string.s_k_etter_brukernavn)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(søkeResultater) { bruker ->
                        ListItem(
                            headlineContent = { Text(bruker.brukernavn) },
                            leadingContent = { Icon(Icons.Outlined.AccountCircle, contentDescription = null) },
                            modifier = Modifier.clickable { onVelgBruker(bruker) }
                        )
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onLukk) { Text(stringResource(R.string.lukk)) } }
    )
}

@Composable
fun StartSpillLabels(bane: Bane, modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        HorizontalDivider()
        ListeItem(
            bane.navn,
            Icons.Outlined.Place
        )

        HorizontalDivider()
        ListeItem(
            stringResource(R.string._hull, bane.antHull),
            Icons.Outlined.AddCircle
        )


        HorizontalDivider()
        ListeItem(
            stringResource(R.string.hvem_skal_spille),
            Icons.Outlined.AccountCircle
        )
    }
}

@Composable
fun ListeItem(label: String, imageVector: ImageVector) {
    ListItem(
        headlineContent = {
            Text(
                text = label,
                style = Typography.titleMedium
            )
                          },
        leadingContent = {
            Icon(
                imageVector = imageVector,
                contentDescription = stringResource(R.string.accountsirkel)
            )
        }
    )
}

@Composable
fun SpillerListe(spillere: List<Spiller>, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface)
    ){
        items(spillere) { spiller ->
            HorizontalDivider()
            ListItem(
                headlineContent = {
                    Text(
                        text = spiller.navn,
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Outlined.AccountCircle,
                        contentDescription = stringResource(R.string.accountsirkel_),
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    )
                }
            )
        }
    }
}




