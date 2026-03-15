package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.theme.Typography

@Composable
fun LabelVelgAntall(
    label: String?,
    verdi: Int,
    onDekrement: () -> Unit,
    onInkrement: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (label != null) {
            Text(
                text = label,
                style = Typography.labelLarge
            )
        }
        Spacer(Modifier.padding(8.dp))
        IconButton(onClick = onDekrement) {
            Icon(Icons.Outlined.DoNotDisturbOn,
                contentDescription = stringResource(R.string.reduser_verdi),
                Modifier.size(30.dp)
            )
        }
        Text(
            text = verdi.toString(),
            style = Typography.titleLarge
        )
        IconButton(onClick = onInkrement) {
            Icon(
                Icons.Outlined.AddCircle,
                contentDescription = stringResource(R.string.k_verdi),
                Modifier.size(30.dp)
            )
        }
    }
}
