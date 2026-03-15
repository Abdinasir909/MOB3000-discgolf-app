package no.usn.mob3000_gruppe15.ui.turnering

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.viewmodel.TurneringViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedigerTurneringSkjerm(
    navController: androidx.navigation.NavController,
    turneringViewModel: TurneringViewModel,
    onTilbake: () -> Unit = {}
) {
    // Henter data fra ViewModel og sjekker om en turnering er valgt
    val uiState by turneringViewModel.uiState.collectAsState()
    val turnering = uiState.valgtTurnering
    val context = LocalContext.current

    if (turnering == null) {
        LaunchedEffect(Unit) { onTilbake() }
        return
    }

    // Initialiserer skjema-feltene med eksisterende data fra turneringen
    var navn by remember { mutableStateOf(turnering.navn) }
    var beskrivelse by remember { mutableStateOf(turnering.beskrivelse) }
    var dato by remember { mutableStateOf(turnering.dato) }
    var sted by remember { mutableStateOf(turnering.sted) }
    var adresse by remember { mutableStateOf(turnering.adresse) }
    var deltakere by remember { mutableStateOf(turnering.deltakere.toString()) }
    var premiepott by remember { mutableStateOf(turnering.premiepott) }
    var kontakt by remember { mutableStateOf(turnering.kontakt) }

    val isLoading = uiState.isLoading

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header med tilbake-knapp og tittel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onTilbake) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Tilbake")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Rediger Turnering",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Inntastingsfelt for navn og beskrivelse
        item {
            OutlinedTextField(
                value = navn,
                onValueChange = { navn = it },
                label = { Text("Turneringnavn") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = beskrivelse,
                onValueChange = { beskrivelse = it },
                label = { Text("Beskrivelse") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        // Dato-velger: Konverterer dato-streng til kalender og viser dialog
        item {
            val calendar = Calendar.getInstance()

            try {
                val parts = dato.split("-")
                if (parts.size == 3) {
                    calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                }
            } catch (e: Exception) { }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _: DatePicker, y: Int, m: Int, d: Int ->
                    dato = String.format("%04d-%02d-%02d", y, m + 1, d)
                }, year, month, day
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dato,
                    onValueChange = {},
                    label = { Text("Dato (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, null) }
                )
                Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
            }
        }

        // Resten av inntastingsfeltene (Sted, Adresse, Deltakere, etc.)
        item {
            OutlinedTextField(
                value = sted,
                onValueChange = { sted = it },
                label = { Text("Sted") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = { Text("Adresse") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = deltakere,
                onValueChange = { if (it.all { c -> c.isDigit() }) deltakere = it },
                label = { Text("Maks deltakere") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = premiepott,
                onValueChange = { premiepott = it },
                label = { Text("Premiepott") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = kontakt,
                onValueChange = { kontakt = it },
                label = { Text("Kontaktinfo") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Lagre-knapp som sender oppdaterte data til ViewModel
        item {
            Button(
                onClick = {
                    turneringViewModel.oppdaterTurnering(
                        turneringId = turnering.id,
                        context = context,
                        navn = navn,
                        beskrivelse = beskrivelse,
                        dato = dato,
                        sted = sted,
                        adresse = adresse,
                        deltakere = deltakere.toIntOrNull() ?: 0,
                        premiepott = premiepott,
                        kontakt = kontakt,
                        onSuccess = {
                            Toast.makeText(context, "Endringer lagret", Toast.LENGTH_SHORT).show()
                            onTilbake()
                        },
                        onError = { msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Lagre Endringer")
                }
            }
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}