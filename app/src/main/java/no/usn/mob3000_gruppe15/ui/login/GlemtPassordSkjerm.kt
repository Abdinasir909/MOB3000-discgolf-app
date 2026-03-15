package no.usn.mob3000_gruppe15.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.navigasjon.LOGIN_ROUTE
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme

/** Innså at det å tilbakestille passord heller ikke fungerte i webløsningne fra
 * forrige emne (Det var ikke jeg som hadde jobbet med den da, glemte at den endte
 * med å kun være "til pynt"/ikke implementert. Det samme er tilfellet i denne
 * applikajsonen
 *
 */

@Composable
fun GlemtPassordSkjerm(
    navController: NavController,
    viewModel: GlemtPassordViewModel = viewModel()
) {
    GlemtPassordContent(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun GlemtPassordContent(
    navController: NavController,
    viewModel: GlemtPassordViewModel
) {
    val epost = viewModel.epost
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val glemtPassordResponse = viewModel.glemtPassordResponse
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        LaunchedEffect(glemtPassordResponse) {
            glemtPassordResponse?.let {
                if (it.success) {
                    snackbarHostState.showSnackbar(it.melding ?: "Lenke sendt")
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Text(
                    text = stringResource(R.string.glemt_passord),
                    style = MaterialTheme.typography.displaySmall
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                OutlinedTextField(
                    value = epost,
                    onValueChange = { viewModel.epostInput(it) },
                    label = { Text(stringResource(R.string.email)) },
                    enabled = !isLoading
                )
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }

            item {
                Button(
                    onClick = {
                        keyboardController?.hide()
                        viewModel.tilbakestillPassord()
                    },
                    modifier = Modifier.size(width = 190.dp, height = 50.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator( // <-- Samme som LoginSkjerm (Googlet/KI)
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.send_lenke),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                Text(
                    text = stringResource(R.string.tilbake_til_login),
                    modifier = Modifier.clickable {
                        navController.navigate(LOGIN_ROUTE)
                    }
                )
            }
            item {
                if (!errorMessage.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            }
        }
    }
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GlemtPassordPreview() {
    DiskgolfTheme {
        val dummyNavController = rememberNavController()
        GlemtPassordSkjerm(navController = dummyNavController)
    }
}