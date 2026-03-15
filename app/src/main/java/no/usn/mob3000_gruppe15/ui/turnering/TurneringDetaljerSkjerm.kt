package no.usn.mob3000_gruppe15.ui.turnering

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.model.Turnering
import no.usn.mob3000_gruppe15.viewmodel.TurneringViewModel

@Composable
fun TurneringDetaljerSkjerm(
    turnering: Turnering,
    onNavigerTilResultat: () -> Unit = {}
) {
    // Initialiserer ViewModel og observerer UI-tilstanden
    val viewModel: TurneringViewModel = viewModel()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Henter innlogget bruker-ID fra lokal lagring
    var minUserId by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        val dataStore = DataStoreManager(context)
        minUserId = dataStore.id.first()
    }

    // Beregner logikk
    val erEier = (minUserId != null && minUserId == turnering.opprettetAv)
    val erPameldt = turnering.registreringer?.any { it.userId == minUserId } == true
    val antallPameldte = turnering.registreringer?.size ?: 0
    val erFull = antallPameldte >= turnering.deltakere

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        // Seksjon for header-bilde, tittel og status-badge
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(turnering.getImage()),
                    contentDescription = "Turnering banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = turnering.navn,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = when (turnering.status) {
                            "Oppkommende" -> Color(0xFF2196F3)
                            "Pågår" -> Color(0xFF4CAF50)
                            else -> Color.Gray
                        },
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = turnering.status,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Hovedinnhold med informasjon og knapper
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Admin Info (vises kun hvis innlogget bruker eier turneringen)
                if (erEier) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Admin Info", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Påmeldte:")
                                Text("$antallPameldte / ${turnering.deltakere}", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Beskrivelse og detaljkort
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Om turneringen", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(turnering.beskrivelse, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Liste over detaljer (Dato, Sted, Adresse, etc.)
                Text("Detaljer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                TurneringDetaljerKort(Icons.Filled.DateRange, "Dato", turnering.dato)
                TurneringDetaljerKort(Icons.Filled.LocationOn, "Sted", turnering.sted)
                TurneringDetaljerKort(Icons.Filled.Info, "Adresse", turnering.adresse)
                TurneringDetaljerKort(Icons.Filled.People, "Plasser", "$antallPameldte av ${turnering.deltakere} opptatt")
                TurneringDetaljerKort(Icons.Filled.EmojiEvents, "Premiepott", turnering.premiepott)
                TurneringDetaljerKort(Icons.Filled.Email, "Kontakt", turnering.kontakt)

                Spacer(modifier = Modifier.height(8.dp))

                // Påmelding/Avmelding knapp med logikk for status og farger
                val kanKlikke = !uiState.isLoading

                Button(
                    onClick = {
                        if (erPameldt) {
                            // MELD AV
                            viewModel.meldAvTurnering(
                                turneringId = turnering.id,
                                context = context,
                                onSuccess = { Toast.makeText(context, "Du er meldt av!", Toast.LENGTH_SHORT).show() },
                                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                            )
                        } else {
                            // MELD PÅ
                            if (!erFull) {
                                viewModel.meldPaaTurnering(
                                    turneringId = turnering.id,
                                    context = context,
                                    onSuccess = { Toast.makeText(context, "Du er påmeldt!", Toast.LENGTH_SHORT).show() },
                                    onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
                                )
                            } else {
                                Toast.makeText(context, "Turneringen er full", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = kanKlikke,
                    colors = ButtonDefaults.buttonColors(
                        // Rød hvis påmeldt, ellers blå
                        containerColor = if (erPameldt) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Icon(
                            imageVector = if (erPameldt) Icons.Filled.ExitToApp else Icons.Filled.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (erPameldt) "Meld deg av" else if (erFull) "Fulltegnet" else "Meld deg på",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }

                // Knapp til resultater
                OutlinedButton(
                    onClick = onNavigerTilResultat,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Filled.ListAlt, null, Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Resultater")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


// Hjelpekomponent for å vise en rad med ikon, tittel og verdi
@Composable
fun TurneringDetaljerKort(
    ikon: androidx.compose.ui.graphics.vector.ImageVector,
    tittel: String,
    verdi: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = ikon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tittel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = verdi,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}