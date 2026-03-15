package no.usn.mob3000_gruppe15.ui.klubber

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.base64ToImageBitmap
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.viewmodel.KlubberViewModel
import no.usn.mob3000_gruppe15.model.Klubb

@Composable
fun KlubberSkjerm(
    navController: NavController,
    visAndreKlubber: Boolean = false
) {
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val innloggetBrukerId by dataStoreManager.id.collectAsState(initial = null)

    val vm: KlubberViewModel = viewModel()
    var søk by remember { mutableStateOf("") }
    var visMinKlubb by remember { mutableStateOf(!visAndreKlubber) }

    val klubbData = vm.klubber.value as? List<Map<String, Any>> ?: emptyList()
    val ratings = listOf(4.2, 4.5, 4.8, 3.9, 4.6)
    val distances = listOf("1.2 km", "2.3 km", "5.4 km", "0.8 km", "3.1 km")

    val alleKlubber = klubbData.mapIndexed { index, m ->
        Klubb(
            id = m["_id"].toString(),
            navn = m["klubbnavn"].toString(),
            plass = m["by"].toString(),
            initials = m["klubbnavn"].toString().firstOrNull()?.uppercase() ?: "?",
            beskrivelse = m["beskrivelse"].toString(),
            rating = ratings[index % ratings.size],
            distance = distances[index % distances.size],
            bildeBase64 = m["bilde"] as? String,
            brukerId = m["brukerId"]?.toString() ?: ""
        )
    }

    val mineKlubber = alleKlubber.filter { klubb ->
        innloggetBrukerId?.let { brukerId ->
            val medlemmer = klubbData.find { it["_id"].toString() == klubb.id }
                ?.get("medlemmer") as? List<*>
            medlemmer?.any {
                (it as? Map<*, *>)?.get("id")?.toString() == brukerId
            } == true
        } ?: false
    }

    val filtrert = remember(alleKlubber, mineKlubber, visMinKlubb, søk) {
        val liste = if (visMinKlubb) mineKlubber else alleKlubber.filter { it !in mineKlubber }
        if (søk.isBlank()) liste
        else liste.filter { k ->
            k.navn.contains(søk, ignoreCase = true) ||
                    k.plass.contains(søk, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        vm.hentAlleKlubber()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = if (visMinKlubb) 0 else 1,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = visMinKlubb,
                onClick = { visMinKlubb = true },
                text = { Text(stringResource(R.string.mine_klubber)) }
            )
            Tab(
                selected = !visMinKlubb,
                onClick = { visMinKlubb = false },
                text = { Text(stringResource(R.string.andre_klubber)) }
            )
        }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = søk,
                onValueChange = { søk = it },
                label = { Text(stringResource(R.string.sok_etter_klubb)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(percent = 50)
            )

            if (filtrert.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (visMinKlubb)
                            stringResource(R.string.ingen_mine_klubber)
                        else
                            stringResource(R.string.ingen_andre_klubber),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filtrert) { klubb ->
                        KlubbKort(
                            klubb = klubb,
                            navController = navController,
                            erMinKlubb = mineKlubber.any { it.id == klubb.id }
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun KlubbKort(klubb: Klubb, navController: NavController, erMinKlubb: Boolean = false) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .clickable {
                if (erMinKlubb) {
                    navController.navigate("klubbSide/${klubb.navn}")
                } else {
                    navController.navigate("klubbInfo/${klubb.navn}")
                }
            }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val imageBitmap: ImageBitmap? = remember(klubb.bildeBase64) {
                klubb.bildeBase64?.let { base64ToImageBitmap(it) }
            }
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = stringResource(R.string.klubb_bilde_beskrivelse),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.usn),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.TopCenter)
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Profil(initials = klubb.initials, modifier = Modifier.size(50.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            klubb.navn,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    klubb.plass,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(2.dp))
                                Text(
                                    klubb.rating.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                klubb.distance,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun Profil(initials: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initials,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
