package no.usn.mob3000_gruppe15.ui.turnering

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TurneringFilterState(
    val stedSøk: String = "",
    val datoFra: Long? = null,
    val datoTil: Long? = null,
    val status: Set<String> = emptySet()
)



@Composable
fun FilterToggleButton(
    isOpen: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filter",
            modifier = Modifier
                .size(20.dp)
                .padding(end = 8.dp),
            tint = Color.White
        )
        Text(
            text = "Filtrer",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TurneringFilterPanel(
    filterTilstand: TurneringFilterState,
    alleStatuser: List<String>,
    alleSted: List<String>,
    onFilterEndret: (TurneringFilterState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FilterSearchField(
            value = filterTilstand.stedSøk,
            onValueChange = { nySøk ->
                onFilterEndret(filterTilstand.copy(stedSøk = nySøk))
            }
        )

        Spacer(Modifier.height(8.dp))

        FilterDateRange(
            datoFra = filterTilstand.datoFra,
            datoTil = filterTilstand.datoTil,
            onDatoFraChanged = { nyDato ->
                onFilterEndret(filterTilstand.copy(datoFra = nyDato))
            },
            onDatoTilChanged = { nyDato ->
                onFilterEndret(filterTilstand.copy(datoTil = nyDato))
            }
        )

        Spacer(Modifier.height(8.dp))

        FilterStatusCheckboxes(
            selectedStatuses = filterTilstand.status,
            alleStatuser = alleStatuser,
            onStatusChanged = { nyeStatuser ->
                onFilterEndret(filterTilstand.copy(status = nyeStatuser))
            }
        )

        Spacer(Modifier.height(8.dp))

        FilterResetButton(
            onReset = {
                onFilterEndret(TurneringFilterState())
            }
        )
    }
}

@Composable
fun FilterSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Søk sted",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("F.eks. Oslo, Bergen...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Søk")
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDateRange(
    datoFra: Long?,
    datoTil: Long?,
    onDatoFraChanged: (Long?) -> Unit,
    onDatoTilChanged: (Long?) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale("no", "NO"))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Dato",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Start Date Picker
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Fra",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = if (datoFra != null) dateFormatter.format(Date(datoFra)) else "Velg dato",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // End Date Picker
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Til",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { showEndDatePicker = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = if (datoTil != null) dateFormatter.format(Date(datoTil)) else "Velg dato",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Start Date Picker Dialog
    if (showStartDatePicker) {
        val startDatePickerState = rememberDatePickerState(initialSelectedDateMillis = datoFra)
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDatoFraChanged(startDatePickerState.selectedDateMillis)
                        showStartDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartDatePicker = false }) {
                    Text("Avbryt")
                }
            }
        ) {
            DatePicker(state = startDatePickerState)
        }
    }

    // End Date Picker Dialog
    if (showEndDatePicker) {
        val endDatePickerState = rememberDatePickerState(initialSelectedDateMillis = datoTil)
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDatoTilChanged(endDatePickerState.selectedDateMillis)
                        showEndDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndDatePicker = false }) {
                    Text("Avbryt")
                }
            }
        ) {
            DatePicker(state = endDatePickerState)
        }
    }
}

@Composable
fun FilterStatusCheckboxes(
    selectedStatuses: Set<String>,
    alleStatuser: List<String>,
    onStatusChanged: (Set<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        alleStatuser.forEach { status ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = status in selectedStatuses,
                    onCheckedChange = { isChecked ->
                        val nyeStatuser = selectedStatuses.toMutableSet()
                        if (isChecked) {
                            nyeStatuser.add(status)
                        } else {
                            nyeStatuser.remove(status)
                        }
                        onStatusChanged(nyeStatuser)
                    }
                )
                Text(text = status, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
fun FilterResetButton(onReset: () -> Unit) {
    Button(
        onClick = onReset,
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )
    ) {
        Text("Tilbakestill filtre", color = Color.White)
    }
}

@Composable
fun FilterTag(text: String) {
    ElevatedCard(
        modifier = Modifier.height(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}