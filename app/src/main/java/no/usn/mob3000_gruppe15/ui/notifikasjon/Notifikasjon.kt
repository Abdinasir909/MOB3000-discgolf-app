package no.usn.mob3000_gruppe15.ui.notifikasjon

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Notifikasjon (){
    Box(
    modifier = Modifier
    .fillMaxSize()
    .padding(16.dp),
    contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ingen notifikasjoner enda",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
