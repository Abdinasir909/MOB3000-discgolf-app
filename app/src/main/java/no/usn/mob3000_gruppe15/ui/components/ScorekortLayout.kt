package no.usn.mob3000_gruppe15.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.HullUiState
import no.usn.mob3000_gruppe15.model.Spiller

@Composable
fun ScorekortLayout(hullListe: List<HullUiState>, spillere: List<Spiller>) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.scroll_horisontalt_p_tabellene),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        HorizontalDivider()
        Text(
            text = stringResource(R.string.scroll_vertikalt_p_skjermen),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
        HorizontalDivider()
        Spacer(modifier = Modifier.padding(8.dp))
        spillere.forEach {spiller ->
            var totalKast = spiller.antKastTotal
            var sammenlagt = spiller.sammenlagt
            Text(
                text = spiller.navn,
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = stringResource(R.string.antall_kast_sammenlagt, totalKast, sammenlagt),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.labelLarge
            )
            Row {
                KolonneMedEtiketter()
                LazyRow {
                    itemsIndexed(hullListe) { indeks, hull ->
                        DataKolonne(
                            hullNummer = indeks + 1,
                            par = hull.par,
                            kast = spiller.antallKast?.getOrElse(indeks) { 0 } ?: 0
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun KolonneMedEtiketter() {
    Column(Modifier
        .width(IntrinsicSize.Min)
    ) {
        TabellCelle(
            tekst = stringResource(R.string.hull),
            fontWeight = FontWeight.Bold,
            harKantlinje = false,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        TabellCelle(
            tekst = stringResource(R.string.par),
            fontWeight = FontWeight.Bold,
            harKantlinje = false,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        TabellCelle(
            tekst = stringResource(R.string.kast),
            fontWeight = FontWeight.Bold,
            harKantlinje = false,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}

@Composable
private fun DataKolonne(hullNummer: Int, par: Int, kast: Int) {
    val color = when (kast) {
        0 -> Color(255, 2, 2, 255)
        1 -> Color(105, 211, 255, 255)
        2 -> Color(103, 200, 243, 255)
        3 -> Color(255, 240, 167, 255)
        4 -> Color(245, 213, 117, 255)
        else -> {
            Color(236, 205, 65, 255)
        }
    }
    Column(Modifier.width(50.dp)) {
        TabellCelle(tekst = hullNummer.toString())
        TabellCelle(tekst = par.toString())
        TabellCelle(
            tekst = kast.toString(),
            bakgrunnsFarge = color,
            tekstFarge = Color.Black
        )
    }
}

@Composable
private fun TabellCelle(
    tekst: String,
    bakgrunnsFarge: Color = Color.Unspecified,
    tekstFarge: Color = Color.Unspecified,
    fontWeight: FontWeight = FontWeight.Normal,
    harKantlinje: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = if (harKantlinje) { modifier
            .fillMaxWidth()
            .height(50.dp)
            .border(1.dp, Color.Gray)
            .background(bakgrunnsFarge)
        } else {
            modifier
                .fillMaxWidth()
                .height(50.dp)},
            contentAlignment = Alignment.Center,
    ) {
        Text(
            text = tekst,
            fontWeight = fontWeight,
            fontSize = 16.sp,
            color= tekstFarge,
            textAlign = TextAlign.Center,
        )
    }
}

