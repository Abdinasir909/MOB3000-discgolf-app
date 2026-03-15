package no.usn.mob3000_gruppe15.ui.klubber

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import no.usn.mob3000_gruppe15.ui.components.IconLabelHorizontal
import no.usn.mob3000_gruppe15.viewmodel.KlubberViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KlubbInfoSkjerm(
    navController: NavController,
    klubbId: String? = null
) {
    val vm: KlubberViewModel = viewModel()
    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val innloggetBrukerId by dataStoreManager.id.collectAsState(initial = null)
    val brukerState by vm.bruker
    val brukernavn = brukerState?.brukernavn ?: innloggetBrukerId ?: "Bruker"

    var erMedlem by remember { mutableStateOf(false) }
    var antallMedlemmer by remember { mutableStateOf(0) }
    val klubberRaw = (vm.klubber.value as? List<*>) ?: listOf<Any>()
    val klubbMap = klubberRaw
        .filterIsInstance<Map<String, Any>>()
        .firstOrNull { it["klubbnavn"].toString() == klubbId }

    if (klubbMap == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    val klubbNavn = klubbMap?.get("klubbnavn").toString()
    val klubbDbId = klubbMap?.get("_id").toString()
    val bildeBase64 = klubbMap?.get("bilde") as? String
    val bildeBitmap = remember(bildeBase64) { bildeBase64?.let { base64ToImageBitmap(it) } }

    LaunchedEffect(innloggetBrukerId) {
        innloggetBrukerId?.let { id ->
            if (id.isNotEmpty()) {
                vm.getBruker(id)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    if (bildeBitmap != null) {
                        Image(
                            bitmap = bildeBitmap,
                            contentDescription = stringResource(R.string.klubb_bilde),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.usn),
                            contentDescription = stringResource(R.string.klubb_bilde),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Color.Black.copy(alpha = 0.35f)
                            )
                    )
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.9f),
                                shape = RoundedCornerShape(50.dp)
                            )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.tilbake),
                            tint = Color.Black
                        )
                    }
                    Text(
                        text = klubbNavn,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = klubbMap?.get("beskrivelse").toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        )
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = stringResource(R.string.kontaktinformasjon),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconLabelHorizontal(
                        icon = Icons.Outlined.Place,
                        label = stringResource(R.string.lokasjon),
                        value = klubbMap?.get("by").toString()
                    )
                    IconLabelHorizontal(
                        icon = Icons.Outlined.Email,
                        label = stringResource(R.string.email),
                        value = klubbMap?.get("kontaktinfo").toString()
                    )
                    IconLabelHorizontal(
                        icon = Icons.Outlined.Language,
                        label = stringResource(R.string.nettside),
                        value = klubbMap?.get("nettside").toString()
                    )
                    IconLabelHorizontal(
                        icon = Icons.Outlined.CalendarToday,
                        label = stringResource(R.string.etablert),
                        value = klubbMap?.get("etablert")?.toString()?.split(".")?.get(0) ?: ""
                    )
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Button(
                        onClick = {
                            innloggetBrukerId?.let { brukerId ->
                                if (brukerId.isNotEmpty() && brukernavn.isNotEmpty()) {
                                    vm.bliMedlem(
                                        brukerId = brukerId,
                                        brukerNavn = brukernavn,
                                        klubbId = klubbDbId,
                                        onSuccess = {
                                            erMedlem = true
                                            antallMedlemmer++
                                            Toast.makeText(context, context.getString(R.string.ble_medlem_suksess), Toast.LENGTH_SHORT).show()
                                            navController.navigate("klubbSide/$klubbNavn")
                                        },
                                        onError = { feilmelding ->
                                            Toast.makeText(context, feilmelding, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !erMedlem && (innloggetBrukerId?.isNotEmpty() == true) && brukernavn.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (erMedlem)
                                MaterialTheme.colorScheme.surfaceVariant
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = if (erMedlem) stringResource(R.string.allerede_medlem) else stringResource(R.string.bli_medlem),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
