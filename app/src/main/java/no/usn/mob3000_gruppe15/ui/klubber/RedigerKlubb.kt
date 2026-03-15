package no.usn.mob3000_gruppe15.ui.klubber

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import no.usn.mob3000_gruppe15.viewmodel.KlubberViewModel
import no.usn.mob3000_gruppe15.model.Treningstid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedigerKlubb(
    navController: NavController,
    klubbId: String? = null,
    viewModel: KlubberViewModel = viewModel(),
    banerViewModel: BanerViewModel
){
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val innloggetBrukerId by dataStoreManager.id.collectAsState(initial = null)

    val klubberRaw = (viewModel.klubber.value as? List<*>) ?: listOf<Any>()
    val klubber = klubberRaw.filterIsInstance<Map<String, Any>>()
    val mineKlubber = klubber.filter { klubb ->
        innloggetBrukerId?.let { brukerId ->
            klubb["brukerId"]?.toString() == brukerId
        } ?: false
    }

    if (klubbId == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.velg_klubb_tittel)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.tilbake)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            if (mineKlubber.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            stringResource(R.string.ingen_klubber_a_redigere),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            } else {
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(mineKlubber) { klubb ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("redigerKlubb/${klubb["_id"]}")
                                },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        klubb["klubbnavn"].toString(),
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        klubb["by"].toString(),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    } else {
        val klubb = klubber.firstOrNull { it["_id"].toString() == klubbId }

        if (klubb == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        var klubbnavn by remember { mutableStateOf(klubb["klubbnavn"]?.toString() ?: "") }
        var kontaktinfo by remember { mutableStateOf(klubb["kontaktinfo"]?.toString() ?: "") }
        var beskrivelse by remember { mutableStateOf(klubb["beskrivelse"]?.toString() ?: "") }
        var by by remember { mutableStateOf(klubb["by"]?.toString() ?: "") }
        var nettside by remember { mutableStateOf(klubb["nettside"]?.toString() ?: "") }
        var valgtBildeUri by remember { mutableStateOf<Uri?>(null) }
        var søk by remember { mutableStateOf("") }

        var treningstider by remember {
            mutableStateOf(
                (klubb["treningstider"] as? List<*>)?.mapNotNull {
                    val map = it as? Map<*, *>
                    Treningstid(
                        dag = map?.get("dag")?.toString() ?: "",
                        tid = map?.get("tid")?.toString() ?: ""
                    )
                }?.ifEmpty { listOf(Treningstid()) } ?: listOf(Treningstid())
            )
        }

        val banerState by banerViewModel.uiState.collectAsState()
        val alleBaner: List<Bane> = banerState.baner ?: emptyList()
        val filtrerteBaner = alleBaner.filter { it.navn.contains(søk, ignoreCase = true) }

        var valgteBaner by remember {
            mutableStateOf<List<Bane>>(
                (klubb["baner"] as? List<*>)?.mapNotNull { baneId ->
                    alleBaner.find { it.id == baneId?.toString() }
                } ?: emptyList()
            )
        }

        val bildePickerLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri -> valgtBildeUri = uri }

        val scope = rememberCoroutineScope()
        var visSlettDialog by remember { mutableStateOf(false) }
        var visLagreDialog by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.rediger_baneinfo, klubbnavn)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.tilbake)
                            )
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                                treningstider = treningstider.toMutableList().also {
                                    it[index] = it[index].copy(dag = dag)
                                }
                            },
                            label = { Text(stringResource(R.string.ukedag)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = treningstid.tid,
                            onValueChange = { tid ->
                                treningstider = treningstider.toMutableList().also {
                                    it[index] = it[index].copy(tid = tid)
                                }
                            },
                            label = { Text(stringResource(R.string.tid_klokkeslett)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        IconButton(
                            onClick = {
                                treningstider = if (treningstider.size == 1) {
                                    listOf(Treningstid())
                                } else {
                                    treningstider.filterIndexed { i, _ -> i != index }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = stringResource(R.string.fjern)
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                OutlinedButton(
                    onClick = { treningstider = treningstider + Treningstid() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.ny_tid)
                    )
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
                    onClick = { visLagreDialog = true },
                    enabled = klubbnavn.isNotEmpty() &&
                            kontaktinfo.isNotEmpty() &&
                            kontaktinfo.contains("@") &&
                            (innloggetBrukerId?.isNotEmpty() == true),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        stringResource(R.string.lagre_endringer),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Spacer(Modifier.height(18.dp))

                OutlinedButton(
                    onClick = { visSlettDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.slett_klubb))
                }

                if (visSlettDialog) {
                    AlertDialog(
                        onDismissRequest = { visSlettDialog = false },
                        title = { Text(stringResource(R.string.slett_klubb)) },
                        text = {
                            Text(
                                stringResource(
                                    R.string.vil_du_slette,
                                    klubbnavn
                                )
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    visSlettDialog = false
                                    klubbId?.let { id ->
                                        scope.launch {
                                            viewModel.slettKlubb(id) { success ->
                                                if (success) {
                                                    navController.navigateUp()
                                                }
                                            }
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    stringResource(R.string.ja_slett),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { visSlettDialog = false }) {
                                Text(stringResource(R.string.angre_sletting))
                            }
                        }
                    )
                }

                if (visLagreDialog) {
                    AlertDialog(
                        onDismissRequest = { visLagreDialog = false },
                        title = { Text(stringResource(R.string.bekreft_endringer)) },
                        text = {
                            Text(
                                stringResource(
                                    R.string.vil_du_lagre_for,
                                    klubbnavn
                                )
                            )
                        },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    visLagreDialog = false
                                    innloggetBrukerId?.let { brukerId ->
                                        viewModel.oppdaterKlubb(
                                            context = context,
                                            klubbId = klubbId,
                                            klubbnavn = klubbnavn,
                                            kontaktinfo = kontaktinfo,
                                            beskrivelse = beskrivelse,
                                            by = by,
                                            nettside = nettside,
                                            bildeUri = valgtBildeUri,
                                            treningstider = treningstider,
                                            baner = valgteBaner,
                                            brukerId = brukerId
                                        )
                                        scope.launch {
                                            delay(1000)
                                            navController.navigateUp()
                                        }
                                    }
                                }
                            ) {
                                Text(stringResource(R.string.ja_lagre))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { visLagreDialog = false }) {
                                Text(stringResource(R.string.angre_endringer))
                            }
                        }
                    )
                }
            }
        }
    }
}
