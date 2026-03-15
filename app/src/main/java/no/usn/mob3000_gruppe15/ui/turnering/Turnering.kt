package no.usn.mob3000_gruppe15.ui.turnering

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.first
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.Turnering
import no.usn.mob3000_gruppe15.ui.navigasjon.Tilbakeskjermer
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme
import no.usn.mob3000_gruppe15.viewmodel.TurneringViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TurneringSkjerm(navController: NavController, turneringViewModel: TurneringViewModel) {
    // Henter tilstand fra ViewModel og setter opp lokale variabler
    val uiState by turneringViewModel.uiState.collectAsState()
    val allTurneringer = uiState.turneringer
    val context = LocalContext.current

    var currentUserId by remember { mutableStateOf<String?>(null) }
    var visMineTurneringer by remember { mutableStateOf(false) }
    var filterTilstand by remember { mutableStateOf(TurneringFilterState()) }
    var erFilterSynlig by remember { mutableStateOf(false) }
    var visSlettDialog by remember { mutableStateOf(false) }
    var turneringSomSkalSlettes by remember { mutableStateOf<Turnering?>(null) }

    // Henter bruker-ID og turneringer når skjermen lastes
    LaunchedEffect(Unit) {
        val dataStore = DataStoreManager(context)
        currentUserId = dataStore.id.first()
        turneringViewModel.hentAlleTurneringer()
    }

    // Filtreringslogikk: Sorterer listen basert på eier, sted, status og dato
    val filteredTurneringer = allTurneringer.filter { turnering ->
        val erMinMatch = if (visMineTurneringer) turnering.opprettetAv == currentUserId else true

        val stedMatch = if (filterTilstand.stedSøk.isNotEmpty()) {
            turnering.sted.contains(filterTilstand.stedSøk, ignoreCase = true) ||
                    turnering.navn.contains(filterTilstand.stedSøk, ignoreCase = true)
        } else true

        val statusMatch = if (filterTilstand.status.isNotEmpty()) {
            turnering.status in filterTilstand.status
        } else true

        // Vi må konvertere String ("2025-06-15") til Long (Milliseconds) for å sammenligne
        val datoMatch = if (filterTilstand.datoFra != null || filterTilstand.datoTil != null) {
            try {
                // Prøv standard formatet vi bruker i "Lag Turnering" (YYYY-MM-DD)
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val turneringDato = sdf.parse(turnering.dato)?.time

                if (turneringDato != null) {
                    val fraOk = filterTilstand.datoFra?.let { turneringDato >= it } ?: true
                    val tilOk = filterTilstand.datoTil?.let { turneringDato <= it } ?: true
                    fraOk && tilOk
                } else {
                    true
                }
            } catch (e: Exception) {
                true
            }
        } else true

        erMinMatch && stedMatch && statusMatch && datoMatch
    }

    // Dialogboks for bekreftelse av sletting
    if (visSlettDialog && turneringSomSkalSlettes != null) {
        AlertDialog(
            onDismissRequest = { visSlettDialog = false },
            title = { Text("Slett Turnering") },
            text = { Text("Er du sikker på at du vil slette \"${turneringSomSkalSlettes?.navn}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        turneringSomSkalSlettes?.let { t ->
                            turneringViewModel.slettTurnering(
                                turneringId = t.id,
                                context = context,
                                onSuccess = {
                                    Toast.makeText(context, "Slettet", Toast.LENGTH_SHORT).show()
                                    visSlettDialog = false
                                    turneringSomSkalSlettes = null
                                },
                                onError = { feil ->
                                    Toast.makeText(context, feil, Toast.LENGTH_SHORT).show()
                                    visSlettDialog = false
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Slett") }
            },
            dismissButton = { TextButton(onClick = { visSlettDialog = false }) { Text("Avbryt") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Header og tittel
            item {
                Column {
                    Text("Turneringer", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (visMineTurneringer) "Dine turneringer" else "Finn turneringer",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Valgknapper for "Alle" vs "Mine" turneringer
            item {
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = !visMineTurneringer,
                        onClick = { visMineTurneringer = false },
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Alle") }
                    SegmentedButton(
                        selected = visMineTurneringer,
                        onClick = { visMineTurneringer = true },
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Mine") }
                }
            }

            // Knapp for å vise/skjule filterpanelet
            item {
                FilterToggleButton(isOpen = erFilterSynlig, onClick = { erFilterSynlig = !erFilterSynlig })
            }

            // Selve filterpanelet (vises kun hvis aktivert)
            if (erFilterSynlig) {
                item {
                    val alleSted = allTurneringer.map { it.sted }.distinct()
                    // La til "Avsluttet" her som du ba om
                    val alleStatuser = listOf("Oppkommende", "Pågår", "Avsluttet")

                    TurneringFilterPanel(
                        filterTilstand = filterTilstand,
                        alleStatuser = alleStatuser,
                        alleSted = alleSted,
                        onFilterEndret = { nyFilter -> filterTilstand = nyFilter }
                    )
                }
            }

            // Knapp for å opprette ny turnering
            item {
                Button(
                    onClick = { navController.navigate(Tilbakeskjermer.LagTurnering.name) },
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Lag Turnering")
                }
            }

            // Lister opp kortene, håndterer laste-status og tomt resultat
            if (uiState.isLoading) {
                item { Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
            } else if (filteredTurneringer.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Text("Ingen turneringer funnet")
                    }
                }
            } else {
                items(filteredTurneringer) { turnering ->
                    TurneringKort(
                        turnering = turnering,
                        navController = navController,
                        turneringViewModel = turneringViewModel,
                        erMinTurnering = (turnering.opprettetAv == currentUserId),
                        onSlettKlikk = {
                            turneringSomSkalSlettes = turnering
                            visSlettDialog = true
                        }
                    )
                }
            }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

// Komponent for hvert enkelt turneringskort med bilde, status og admin-meny
@Composable
fun TurneringKort(
    turnering: Turnering,
    navController: NavController,
    turneringViewModel: TurneringViewModel,
    erMinTurnering: Boolean,
    onSlettKlikk: () -> Unit
) {
    var menyopen by remember { mutableStateOf(false) }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.fillMaxWidth().height(260.dp).clickable {
            turneringViewModel.velgTurnering(turnering)
            navController.navigate(Tilbakeskjermer.TurneringDetaljer.name)
        },
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(turnering.getImage()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier.fillMaxSize().background(
                    Brush.verticalGradient(colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)), startY = 100f)
                )
            )
            // Status Badge
            Box(
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                    .background(
                        color = when(turnering.status) {
                            "Pågår" -> Color(0xFF4CAF50)
                            "Avsluttet" -> Color.Gray
                            else -> Color(0xFF2196F3) // Oppkommende
                        },
                        shape = RoundedCornerShape(20.dp)
                    ).padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(turnering.status, style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Admin-meny for redigering og sletting (kun for eier)
            if (erMinTurnering) {
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    IconButton(onClick = { menyopen = !menyopen }) {
                        Icon(Icons.Default.MoreVert, null, tint = Color.White)
                    }
                    DropdownMenu(expanded = menyopen, onDismissRequest = { menyopen = false }) {
                        DropdownMenuItem(
                            text = { Text("Rediger") },
                            onClick = {
                                menyopen = false
                                turneringViewModel.velgTurnering(turnering)
                                navController.navigate(Tilbakeskjermer.RedigerTurnering.name)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Slett", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                menyopen = false
                                onSlettKlikk()
                            }
                        )
                    }
                }
            }

            // Info tekst nederst på kortet
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp).fillMaxWidth(0.9f)) {
                Text(turnering.navn, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row {
                    Text(turnering.dato, color = Color.White.copy(alpha = 0.9f))
                    Spacer(Modifier.width(12.dp))
                    Text(turnering.sted, color = Color.White.copy(alpha = 0.9f))
                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun TurneringPreview() {
    DiskgolfTheme {
        val navController = rememberNavController()
        TurneringSkjerm(navController = navController, turneringViewModel = TurneringViewModel())
    }
}