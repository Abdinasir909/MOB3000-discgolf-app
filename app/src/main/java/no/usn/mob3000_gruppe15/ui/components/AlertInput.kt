package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import no.usn.mob3000_gruppe15.R

@Composable
fun AlertInput(
    value: String,
    label: String,
    onOppdaterValue: (String) -> Unit,
    onAngre: () -> Unit,
    onBekreft: () -> Unit,
    tittel: String,
    innhold: String,
    icon: ImageVector
) {
    Dialog(onDismissRequest = { onAngre() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        icon,
                        contentDescription = "Ikon",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(bottom = 8.dp)
                    )
                    Text(text = tittel, style = MaterialTheme.typography.titleLarge)
                    Text(text = innhold, style = MaterialTheme.typography.bodyMedium)
                }
                InputFelt(
                    value = value,
                    label = label,
                    onValueChange = onOppdaterValue,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onAngre) {
                        Text(stringResource(R.string.angre))
                    }
                    TextButton(onClick = onBekreft) {
                        Text(stringResource(R.string.bekreft))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AlertInputPreview() {
    AlertInput(
        value = stringResource(R.string.gjestespillerens_navn),
        label = stringResource(R.string.navn),
        onOppdaterValue = {},
        onAngre = {},
        onBekreft = {},
        tittel = stringResource(R.string.legg_til_gjestespiller),
        innhold = stringResource(R.string.skriv_inn_navn_p_gjestespiller),
        icon = Icons.Outlined.AddCircle,
    )
}
