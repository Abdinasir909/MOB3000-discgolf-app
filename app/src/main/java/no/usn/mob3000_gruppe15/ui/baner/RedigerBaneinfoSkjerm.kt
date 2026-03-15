package no.usn.mob3000_gruppe15.ui.baner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.components.Alert
import no.usn.mob3000_gruppe15.ui.components.DropDownMeny
import no.usn.mob3000_gruppe15.ui.components.InputArea
import no.usn.mob3000_gruppe15.ui.components.InputFelt
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun RedigerBaneinfoSkjerm(
    viewModel: BanerViewModel,
    onNavigerTilbake: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        uiState.valgtBane?.let { bane ->
            viewModel.oppdaterBanenavn(bane.navn)
            viewModel.oppdaterNyVanskelighet(bane.vanskelighet)
            viewModel.oppdaterBeskrivelse(bane.beskrivelse)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = uiState.feilMelding,
            color = MaterialTheme.colorScheme.error
        )
        RedigerInfoForm(
            viewModel = viewModel,
            onNavigerTilbake = onNavigerTilbake
        )
    }
}

@Composable
fun RedigerInfoForm(
    viewModel: BanerViewModel,
    onNavigerTilbake: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val vanskelighetOptions = listOf(stringResource(R.string.lett),
        stringResource(R.string.middels), stringResource(R.string.vanskelig)
    )
    var visAlert by remember { mutableStateOf(false) }
    val bildePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri?  ->
        viewModel.oppdaterBildeUri(uri)
    }

    Column(
        modifier = Modifier.fillMaxWidth(0.8f),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.rediger_baneinformasjon),
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
        Text(
            text = stringResource(R.string._antallhull, uiState.valgtBane?.antHull ?: 0),
            style = Typography.bodyLarge
        )
        DropDownMeny(
            valgtTekst = uiState.nyVanskelighet,
            label = stringResource(R.string._vanskelighet),
            onValueChange = { viewModel.oppdaterNyVanskelighet(it) },
            items = vanskelighetOptions
        )
        Text(
            text = stringResource(R.string.plassering, uiState.valgtBane?.plassering ?: ""),
            style = Typography.bodyLarge
        )
        InputArea(
            value = uiState.nyBeskrivelse,
            label = stringResource(R.string.beskrivelse_),
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

        if (!visAlert) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onNavigerTilbake,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.avbryt),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        stringResource(R.string._avbrytrediger),
                        style = Typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
                TextButton(
                    onClick = {
                        if (viewModel.validerRedigertBaneinfo()) {
                            visAlert = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.lagre),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        stringResource(R.string.lagre_endringer),
                        style = Typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        } else {
            Alert(
                onAngre = { visAlert = false },
                onBekreft = {
                    visAlert = false
                    viewModel.lagreRedigertBaneinfo(context)
                    onNavigerTilbake()
                },
                tittel = stringResource(R.string.bekreft_lagring_av_endringer),
                innhold = stringResource(R.string.baneinformasjonen_blir_oppdatert_hvis_du_bekrefter),
                icon = Icons.Outlined.QuestionMark
            )
        }
    }
}