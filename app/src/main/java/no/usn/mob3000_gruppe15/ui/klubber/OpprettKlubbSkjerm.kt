package no.usn.mob3000_gruppe15.ui.klubber

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.data.model.Bane
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.ui.baner.BanerViewModel
import no.usn.mob3000_gruppe15.ui.navigasjon.StartSkjermer
import no.usn.mob3000_gruppe15.viewmodel.KlubberViewModel
import no.usn.mob3000_gruppe15.model.Treningstid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OpprettKlubbSkjerm(
    navController: NavController,
    viewModel: KlubberViewModel = viewModel(),
    banerViewModel: BanerViewModel
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val innloggetBrukerId by dataStoreManager.id.collectAsState(initial = null)
    var klubbnavn by remember { mutableStateOf("") }
    var kontaktinfo by remember { mutableStateOf("") }
    var beskrivelse by remember { mutableStateOf("") }
    var by by remember { mutableStateOf("") }
    var nettside by remember { mutableStateOf("") }
    var treningstider by remember { mutableStateOf(listOf(Treningstid())) }
    var valgtBildeUri by remember { mutableStateOf<Uri?>(null) }
    var søk by remember { mutableStateOf("") }
    var valgteBaner by remember { mutableStateOf<List<Bane>>(emptyList()) }

    val bildePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> valgtBildeUri = uri }

    val scope = rememberCoroutineScope()

    val banerState by banerViewModel.uiState.collectAsState()
    val alleBaner: List<Bane> = banerState.baner ?: emptyList()
    val filtrerteBaner = alleBaner.filter {
        it.navn.contains(søk, ignoreCase = true)
    }

    val brukerState by viewModel.bruker
    val brukerId = innloggetBrukerId ?: ""

    LaunchedEffect(innloggetBrukerId) {
        innloggetBrukerId?.let { id ->
            if (id.isNotEmpty()) {
                viewModel.getBruker(id)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedButton(
                onClick = {
                    bildePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ) {

                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.velg_klubb_bilde))
            }
            if (valgtBildeUri != null) {
                Text(
                    text = stringResource(R.string.valgt_bilde) + " ${valgtBildeUri?.toString() ?: "Ingen valgt"}",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = klubbnavn,
            onValueChange = { klubbnavn = it },
            label = { Text(stringResource(R.string.klubbnavn_)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = kontaktinfo,
            onValueChange = { kontaktinfo = it },
            label = { Text(stringResource(R.string.epost_kontaktinfo)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = beskrivelse,
            onValueChange = { beskrivelse = it },
            label = { Text(stringResource(R.string.beskrivelse_valgfritt)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 5,
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = by,
            onValueChange = { by = it },
            label = { Text(stringResource(R.string.by)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = nettside,
            onValueChange = { nettside = it },
            label = { Text(stringResource(R.string.nettside_valgfritt)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.ukentlige_treningstider), style = MaterialTheme.typography.titleMedium)

        treningstider.forEachIndexed { index, treningstid ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = treningstid.dag,
                    onValueChange = { dag ->
                        treningstider = treningstider.toMutableList().also { it[index] = it[index].copy(dag = dag) }
                    },
                    label = { Text(stringResource(R.string.ukedag)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = treningstid.tid,
                    onValueChange = { tid ->
                        treningstider = treningstider.toMutableList().also { it[index] = it[index].copy(tid = tid) }
                    },
                    label = { Text(stringResource(R.string.tid_klokkeslett)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                )
                IconButton(
                    onClick = { treningstider = treningstider.filterIndexed { i, _ -> i != index } },
                    enabled = treningstider.size > 1
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.fjern))
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = { treningstider = treningstider + Treningstid() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.ny_tid))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.legg_til_treningsdag))
        }

        Spacer(Modifier.height(24.dp))

        Text(stringResource(R.string.velg_baner), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = søk,
            onValueChange = { søk = it },
            label = { Text(stringResource(R.string.s_k_etter_baner)) },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))

        for (bane in filtrerteBaner) {
            val alleredeValgt = valgteBaner.contains(bane)
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = alleredeValgt,
                    onCheckedChange = { valgt ->
                        valgteBaner = if (valgt) {
                            valgteBaner + bane
                        } else {
                            valgteBaner - bane
                        }
                    }
                )
                Text("${bane.navn} (${bane.antHull} ${stringResource(R.string.hull)})")
            }
            Spacer(Modifier.height(4.dp))
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                if (brukerId.isNotEmpty()) {
                    viewModel.opprettKlubb(
                        context = context,
                        klubbnavn = klubbnavn,
                        kontaktinfo = kontaktinfo,
                        beskrivelse = beskrivelse,
                        by = by,
                        nettside = nettside,
                        bildeUri = valgtBildeUri,
                        treningstider = treningstider,
                        baner = valgteBaner,
                        brukerId = brukerId,
                        medlemmer = emptyList()
                    )
                    scope.launch {
                        delay(1000)
                        navController.navigate(StartSkjermer.Hjem.name) {
                            popUpTo(StartSkjermer.Hjem.name) { inclusive = false }
                        }
                    }
                }
            },
            enabled = klubbnavn.isNotEmpty() &&
                    kontaktinfo.isNotEmpty() &&
                    kontaktinfo.contains("@") &&
                    brukerId.isNotEmpty(),
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                stringResource(R.string.opprett_ny_klubb),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}
