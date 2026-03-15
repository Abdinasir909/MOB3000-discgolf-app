package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import no.usn.mob3000_gruppe15.R

@Composable
fun Alert(
    onAngre: () -> Unit,
    onBekreft: () -> Unit,
    tittel: String,
    innhold: String,
    icon: ImageVector
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Icon")
        },
        title = {
            Text(text = tittel)
        },
        text = {
            Text(text = innhold)
        },
        onDismissRequest = {
            onAngre()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onBekreft()
                }
            ) {
                Text(stringResource(R.string.bekreft))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onAngre()
                }
            ) {
                Text(stringResource(R.string.angre))
            }
        }
    )
}