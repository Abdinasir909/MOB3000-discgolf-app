package no.usn.mob3000_gruppe15.ui.hjem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.navigasjon.StartSkjermer
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.usn.mob3000_gruppe15.ui.meg.MegViewModel



@Composable
fun HjemSkjerm(
    brukernavn: String = "Bruker 1",
    navController: NavController,
    viewModel: MegViewModel
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // litt padding rundt hele skjermen
    ) {
        val boxWidth = maxWidth * 0.95f
        val scrollState = rememberScrollState()
        val containerHeight = maxHeight

        Column(
            modifier = Modifier
                .width(boxWidth)
                .align(Alignment.TopCenter)
                .verticalScroll(scrollState), // gjør det scrollbart
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Velkomstmelding boxen på toppen (bruker funksjonen)
            VelkommenBruker(viewModel.brukernavn)

            // Hovedboks med innhold, bilde text osv...
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f), shape = RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tekster øverst med padding
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Klar for en ny runde?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 22.sp
                        )

                        Spacer(modifier = Modifier.height(containerHeight * 0.01f))

                        Text(
                            "Spill med venner eller alene",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 18.sp
                        )
                    }

                    // Bilde hjemmeskjerm
                    Image(
                        painter = painterResource(id = R.drawable.velkomstpagebilde),
                        contentDescription = "Velkomstside bilde",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.Crop
                    )

                    // Tekster og knapp nederst med padding
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp), // padding rundt innholdet
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Tekster, start aligned to start
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                "Start et nytt spill",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 20.sp
                            )
                            Text(
                                "Velg bane og antall spillere",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(containerHeight * 0.04f))

                            Text(
                                "Gjør klar diskene og start en ny runde! Loggfør underveis og følg fremgangen.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge,
                                fontSize = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(containerHeight * 0.04f))

                        // Knapp
                        Button(
                            onClick = {
                                navController?.navigate(StartSkjermer.Spill.name) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController?.graph?.startDestinationId ?: 0) {
                                        saveState = true
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(bottom = containerHeight * 0.02f)
                                .height(40.dp)
                                .fillMaxWidth(0.35f)
                        ) {
                            Text(
                                "Spill",
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

//Funksjon for å vise fram velkomstmelding hvor vi kan bruke den på toppen av kjermen
@Composable
fun VelkommenBruker(brukernavn: String){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 60.dp),
        contentAlignment = Alignment.CenterStart
    ){
        Text(
            text = "Velkommen $brukernavn!",
            style = MaterialTheme.typography.titleLarge,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp, top = 12.dp, bottom = 12.dp)
        )
    }
}