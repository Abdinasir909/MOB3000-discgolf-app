package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun VindVisning(
    vindHastighet: Double,
    vindRetningGrader: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "${vindHastighet} m/s",
        )
        Text(
            text = "⬇",
            modifier = Modifier.rotate(vindRetningGrader.toFloat()),
            fontSize = 20.sp
        )
    }
}