package no.usn.mob3000_gruppe15.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.ui.navigasjon.LOGIN_ROUTE
import no.usn.mob3000_gruppe15.ui.navigasjon.StartSkjermer
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme

@Composable
fun LoginSkjerm(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    LoginContent(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun LoginContent(
    navController: NavController,
    viewModel: LoginViewModel
) {
    val context = LocalContext.current
    val epost = viewModel.epost
    val passord = viewModel.passord
    val isLoading = viewModel.isLoading
    val loginResult = viewModel.loginResult
    val errorMessage = viewModel.errorMessage

    var visEpostPassordSkjema by remember { mutableStateOf(false) }

    if (loginResult != null) {
        LaunchedEffect(loginResult) {
            navController.navigate(StartSkjermer.Hjem.name) {
                popUpTo(LOGIN_ROUTE) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.loginskjermbilde),
            contentDescription = stringResource(R.string.login),
            modifier = Modifier.size(180.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.titleLarge,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.login_with_user),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (visEpostPassordSkjema) {
            // Epost/passord-skjema (opprinnelig skjerm)
            OutlinedTextField(
                value = epost,
                onValueChange = { viewModel.oppgiEpost(it) },
                label = { Text(stringResource(R.string.email)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = passord,
                onValueChange = { viewModel.oppgiPassord(it) },
                label = { Text(stringResource((R.string.password))) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.loggInn(context) },
                modifier = Modifier.padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator( // <-- Googlet (fikk KI svar) om hvordan legge til dette
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tilbake_til_innloggingsvalg),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    visEpostPassordSkjema = false
                    viewModel.nullstillFelter()
                }
            )
        } else {
            Button(
                onClick = { viewModel.loggInnMedTestbruker(context) },
                modifier = Modifier.padding(8.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.logg_inn_med_testbruker),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { visEpostPassordSkjema = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.logg_inn_med_epost_og_passord),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        if (!errorMessage.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.forgot_password),
            modifier = Modifier.clickable {
                navController.navigate("glemt-passord")
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.create_user),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable {
                navController.navigate("opprett_bruker")
            }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    DiskgolfTheme {
        val dummyNavController = rememberNavController()
        LoginSkjerm(navController = dummyNavController)
    }
}