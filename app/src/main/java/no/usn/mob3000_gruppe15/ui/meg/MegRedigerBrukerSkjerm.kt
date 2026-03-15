package no.usn.mob3000_gruppe15.ui.meg

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.local.FeltType
import no.usn.mob3000_gruppe15.local.DataStoreManager
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MegRedigerBrukerSkjerm(
    navController: NavController,
    viewModel: MegViewModel
) {
    MegRedigerContent(navController, viewModel)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MegRedigerContent(
    navController: NavController,
    viewModel: MegViewModel
) {
    val backgroundColor = Color(0xFFF3F3F3)
    val logoutBtnColor = Color(0xFFFF1919)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var valgtFelt by remember { mutableStateOf<FeltType?>(null) }

    val context = LocalContext.current
    val dataStoreManager = DataStoreManager(context)
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.my_information),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 28.dp, bottom = 18.dp)
                    .fillMaxWidth()
            )
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
            ) {
                // Bruker de lagrede verdiene (ikke temp) for visning
                ProfilRad(
                    label = stringResource(R.string.user_name),
                    verdi = viewModel.brukernavn,
                    ikon = Icons.Default.PersonOutline,
                    onClick = {
                        viewModel.startRedigering(FeltType.BRUKERNAVN)
                        valgtFelt = FeltType.BRUKERNAVN
                    }
                )
                ProfilRad(
                    label = stringResource(R.string.email),
                    verdi = viewModel.epost,
                    ikon = Icons.Default.MailOutline,
                    onClick = {
                        viewModel.startRedigering(FeltType.EPOST)
                        valgtFelt = FeltType.EPOST
                    }
                )
                ProfilRad(
                    label = stringResource(R.string.phone),
                    verdi = viewModel.tlfNr.ifEmpty { "-ikke satt-" },
                    ikon = Icons.Default.Call,
                    onClick = {
                        viewModel.startRedigering(FeltType.TELEFON)
                        valgtFelt = FeltType.TELEFON
                    }
                )
                ProfilRad(
                    label = stringResource(R.string.postal_code),
                    verdi = viewModel.postNr.ifEmpty { "-ikke satt-" },
                    ikon = Icons.Default.LocationOn,
                    onClick = {
                        viewModel.startRedigering(FeltType.POSTNUMMER)
                        valgtFelt = FeltType.POSTNUMMER
                    }
                )
                ProfilRad(
                    label = stringResource(R.string.year_of_birth),
                    verdi = viewModel.fodselsaar.ifEmpty { "-ikke satt-" },
                    ikon = Icons.Default.CalendarMonth,
                    onClick = {
                        viewModel.startRedigering(FeltType.FODSELSAAR)
                        valgtFelt = FeltType.FODSELSAAR
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(2.dp)) }
        item {
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
            ) {
                ProfilRad(
                    label = stringResource(R.string.change_password),
                    verdi = "••••••••",
                    ikon = Icons.Default.Lock,
                    onClick = {
                        viewModel.startRedigering(FeltType.PASSORD)
                        valgtFelt = FeltType.PASSORD
                    }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(38.dp)) }
        item {
            Button(
                onClick = {
                    coroutineScope.launch {
                        dataStoreManager.clearLoginStatus()
                        navController.navigate("login-route") {
                            launchSingleTop = true
                        }
                    }
                },
                modifier = Modifier
                    .size(width = 120.dp, height = 40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = logoutBtnColor,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.logout),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        item {
            if (valgtFelt != null) {
                ModalBottomSheet(
                    onDismissRequest = {
                        viewModel.avbrytRedigering()
                        valgtFelt = null
                    },
                    sheetState = sheetState
                ) {
                    ProfilRadBottomSheet(
                        feltType = valgtFelt!!,
                        onLagre = {
                            valgtFelt = null
                        },
                        onAvbryt = {
                            viewModel.avbrytRedigering()
                            valgtFelt = null
                        },
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // AlertDialog for suksess/feil
    if (viewModel.visOppdateringDialog != null) {
        val erSuksess = viewModel.visOppdateringDialog == true
        val harValideringsFeil = viewModel.valideringsFeilmelding != null

        AlertDialog(
            onDismissRequest = {
                viewModel.skjulDialog()
                viewModel.nullstillValideringsFeil()
            },
            title = {
                Text(
                    text = if (erSuksess) stringResource(R.string.suksess) else stringResource(R.string.feil),
                    color = if (erSuksess) Color.Unspecified else Color(0xFFFF1919)
                )
            },
            text = {
                Text(
                    text = when {
                        erSuksess -> stringResource(R.string.oppdatering_vellykket)
                        harValideringsFeil -> viewModel.valideringsFeilmelding!!
                        else -> stringResource(R.string.kunne_ikke_oppdatere_pr_v_igjen)
                    }
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.skjulDialog()
                    viewModel.nullstillValideringsFeil()
                }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun ProfilRad(
    label: String,
    verdi: String?,
    ikon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 44.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = ikon,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
                if (verdi != null) {
                    Text(
                        text = verdi,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.width(30.dp))
        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = stringResource(R.string.go_to)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilRadBottomSheet(
    feltType: FeltType,
    onLagre: () -> Unit,
    onAvbryt: () -> Unit,
    viewModel: MegViewModel
) {
    // Bruker temp-verdier for redigering
    val brukernavn = viewModel.tempBrukernavn
    val epost = viewModel.tempEpost
    val tlfNr = viewModel.tempTlfNr
    val postNr = viewModel.tempPostNr
    val fodselsaar = viewModel.tempFodselsaar
    val gammeltPassord = viewModel.tempOldPassword
    val nyttPassord = viewModel.tempNewPassord
    val bekreftPassord = viewModel.tempBekreftPassord

    val logoutBtnColor = Color(0xFFFF1919)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.change) + " " + feltType.displayName,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        when (feltType) {
            FeltType.BRUKERNAVN -> {
                OutlinedTextField(
                    value = brukernavn,
                    onValueChange = {
                        viewModel.oppgiBrukernavn(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(stringResource(R.string.user_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }
            FeltType.EPOST -> {
                OutlinedTextField(
                    value = epost,
                    onValueChange = {
                        viewModel.oppgiEpost(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(stringResource(R.string.email_adresse)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FeltType.TELEFON -> {
                OutlinedTextField(
                    value = tlfNr,
                    onValueChange = {
                        viewModel.oppgiTlfNr(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(stringResource(R.string.phone_number)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FeltType.POSTNUMMER -> {
                OutlinedTextField(
                    value = postNr,
                    onValueChange = {
                        viewModel.oppgiPostNr(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(text = stringResource(R.string.postal_code)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FeltType.FODSELSAAR -> {
                OutlinedTextField(
                    value = fodselsaar,
                    onValueChange = {
                        viewModel.oppgiFodselsaar(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(text = stringResource(R.string.birth_date_month_year)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            FeltType.PASSORD -> {
                OutlinedTextField(
                    value = gammeltPassord,
                    onValueChange = {
                        viewModel.oppgiGammeltPassord(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(text = stringResource(R.string.old_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = nyttPassord,
                    onValueChange = {
                        viewModel.opggiNyttPassord(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(text = stringResource(R.string.new_password)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    visualTransformation = PasswordVisualTransformation()
                )
                OutlinedTextField(
                    value = bekreftPassord,
                    onValueChange = {
                        viewModel.oppgiBekreftPassord(it)
                        viewModel.nullstillValideringsFeil()
                    },
                    label = { Text(text = stringResource(R.string.confirm_new_password)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                val verdi = when (feltType) {
                    FeltType.BRUKERNAVN -> brukernavn
                    FeltType.EPOST -> epost
                    FeltType.TELEFON -> tlfNr
                    FeltType.POSTNUMMER -> postNr
                    FeltType.FODSELSAAR -> fodselsaar
                    FeltType.PASSORD -> nyttPassord
                }

                // Valider først
                if (viewModel.validerFelt(feltType, verdi)) {
                    // Gyldig - oppdater og lukk
                    viewModel.oppdaterBrukerFelt(feltType, verdi)
                    onLagre()
                }
                // Ugyldig - AlertDialog vises automatisk (visOppdateringDialog = false settes i validerFelt)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = onAvbryt,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = logoutBtnColor,
                contentColor = Color.White
            )
        ) {
            Text(text = stringResource(R.string.cancel))
        }
    }
}

@Composable
fun ProfilInputFelt(
    label: String,
    verdi: String,
    onVerdiEndret: (String) -> Unit
) {
    OutlinedTextField(
        value = verdi,
        onValueChange = onVerdiEndret,
        label = { Text(text = label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}