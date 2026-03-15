package no.usn.mob3000_gruppe15.ui.components
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun HullTabs(
    antHull: Int,
    valgtHull: Int,
    onHullValgt: (Int) -> Unit
) {
    PrimaryScrollableTabRow(selectedTabIndex = valgtHull - 1) {
        for (i in 1..antHull) {
            Tab(
                selected = valgtHull == i,
                onClick = { onHullValgt(i) },
                text = { Text(text = "Hull$i")
                }
            )
        }
    }
}