package no.usn.mob3000_gruppe15.ui.baner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.components.DropDownMeny
import no.usn.mob3000_gruppe15.ui.components.InputArea
import no.usn.mob3000_gruppe15.ui.components.InputFelt
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun VelgBaneinfoSkjerm(
    viewModel: BanerViewModel,
    onNavigerVelgBanePosisjon: () -> Unit,
    onNavigerVelgPlasseringSkjerm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = uiState.feilMelding,
            color = MaterialTheme.colorScheme.error
        )
        InfoForm(
            viewModel,
            onNavigerVelgBanePosisjon,
            onNavigerVelgPlasseringSkjerm
        )

    }
}

@Composable
fun InfoForm(
    viewModel: BanerViewModel,
    onNavigerVelgBanePosisjon: () -> Unit,
    onNavigerVelgPlasseringSkjerm: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val vanskelighetOptions = listOf(
        stringResource(R.string.lett),
        stringResource(R.string.middels),
        stringResource(R.string.vanskelig),
    )

    val bildePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri?  ->
        viewModel.oppdaterBildeUri(uri)
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement
            .spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.oppgi_info_om_banen),
            style = Typography.labelLarge
        )
        InputFelt(
            value = uiState.nyttBanenavn,
            label = stringResource(R.string.banenavn),
            onValueChange = { viewModel.oppdaterBanenavn(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        InputFelt(
            value = uiState.nyttAntHull,
            label = stringResource(R.string.antall_hull),
            onValueChange = { viewModel.oppdaterAntHull(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            )
        )
        DropDownMeny(
            valgtTekst = uiState.nyVanskelighet,
            label = stringResource(R.string.vanskelighet),
            onValueChange = { viewModel.oppdaterNyVanskelighet(it) },
            items = vanskelighetOptions
        )
        Button(
            onClick = {onNavigerVelgBanePosisjon()},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.velg_baneposisjon)
            )
        }
        Text(
            text = stringResource(R.string.valgt_posisjon, uiState.nyPosisjonNavn)
        )
        InputArea(
            value = uiState.nyBeskrivelse,
            label = stringResource(R.string.beskrivelse),
            onValueChange = { viewModel.oppdaterBeskrivelse(it) },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            )
        )
        Text(
            text = stringResource(R.string.banebilde),
            style = Typography.labelMedium
        )

        if (uiState.nyBildeUri != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = uiState.nyBildeUri,
                    contentDescription = stringResource(R.string.valgt_banebilde),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { viewModel.oppdaterBildeUri(null) },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.fjern_bilde),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        } else {
            OutlinedButton(
                onClick = {
                    bildePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.velg_bilde))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.velg_banebilde))
            }
        }
        Button(
            onClick = {
                if(viewModel.validerBaneinfo()) {
                    onNavigerVelgPlasseringSkjerm()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.bekreft),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}



