package no.usn.mob3000_gruppe15.ui.turnering

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.usn.mob3000_gruppe15.viewmodel.TurneringViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LagTurneringSkjerm(
    navController: androidx.navigation.NavController,
    onTilbake: () -> Unit = {}
) {
    val vm: TurneringViewModel = viewModel()
    val context = LocalContext.current

    var navn by remember { mutableStateOf("") }
    var beskrivelse by remember { mutableStateOf("") }
    var dato by remember { mutableStateOf("") }
    var sted by remember { mutableStateOf("") }
    var adresse by remember { mutableStateOf("") }
    var deltakere by remember { mutableStateOf("") }
    var premiepott by remember { mutableStateOf("") }
    var kontakt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val erSkjemaGyldig = navn.isNotBlank() && beskrivelse.isNotBlank() &&
            dato.isNotBlank() && sted.isNotBlank() &&
            adresse.isNotBlank() && deltakere.isNotBlank() &&
            premiepott.isNotBlank() && kontakt.isNotBlank()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(0.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header
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
                        text = "Lag Turnering",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Opprett en ny discgolf turnering",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Turneringnavn
        item {
            OutlinedTextField(
                value = navn,
                onValueChange = { navn = it },
                label = { Text("Turneringnavn *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.EmojiEvents, contentDescription = null)
                }
            )
        }

        // Beskrivelse
        item {
            OutlinedTextField(
                value = beskrivelse,
                onValueChange = { beskrivelse = it },
                label = { Text("Beskrivelse *") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                leadingIcon = {
                    Icon(Icons.Filled.Description, contentDescription = null)
                }
            )
        }

        // dato
        item {
            val calendar = java.util.Calendar.getInstance()
            val year = calendar.get(java.util.Calendar.YEAR)
            val month = calendar.get(java.util.Calendar.MONTH)
            val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

            val datePickerDialog = android.app.DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    // Backend vil ha: YYYY-MM-DD
                    dato = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                },
                year, month, day
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dato,
                    onValueChange = {},
                    label = { Text("Velg dato") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = null) },
                )
                // En usynlig boks over tekstfeltet som fanger opp klikk hvis man trykker midt på
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { datePickerDialog.show() }
                )
            }
        }

        // Sted
        item {
            OutlinedTextField(
                value = sted,
                onValueChange = { sted = it },
                label = { Text("Sted (by, fylke) *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.LocationOn, contentDescription = null)
                }
            )
        }

        // Adresse
        item {
            OutlinedTextField(
                value = adresse,
                onValueChange = { adresse = it },
                label = { Text("Adresse *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Home, contentDescription = null)
                }
            )
        }

        // Deltakere
        item {
            OutlinedTextField(
                value = deltakere,
                onValueChange = { if (it.all { c -> c.isDigit() }) deltakere = it },
                label = { Text("Maks deltakere *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Group, contentDescription = null)
                }
            )
        }

        // Premiepott
        item {
            OutlinedTextField(
                value = premiepott,
                onValueChange = { premiepott = it },
                label = { Text("Premiepott *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.AttachMoney, contentDescription = null)
                }
            )
        }

        // Kontakt
        item {
            OutlinedTextField(
                value = kontakt,
                onValueChange = { kontakt = it },
                label = { Text("Kontaktinformasjon *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Phone, contentDescription = null)
                }
            )
        }

        // Opprett knapp
        item {
            Button(
                onClick = {
                    isLoading = true
                    vm.lagTurnering(
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
                            isLoading = false
                            Toast.makeText(context, "Turnering opprettet!", Toast.LENGTH_SHORT).show()
                            onTilbake()
                        },
                        onError = { feil ->
                            isLoading = false
                            Toast.makeText(context, feil, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = erSkjemaGyldig && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Oppretter...")
                } else {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Opprett Turnering")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}