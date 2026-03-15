package no.usn.mob3000_gruppe15.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme

@Composable
fun OpprettBrukerSkjerm(
    navController: NavController,
    viewModel: OpprettBrukerViewModel = viewModel()
) {
    OpprettBrukerContent(
        navController = navController,
        viewModel = viewModel
    )
}

@Composable
fun OpprettBrukerContent(
    navController: NavController,
    viewModel: OpprettBrukerViewModel
) {
    val brukernavn = viewModel.brukernavn
    val epost = viewModel.epost
    val passord = viewModel.passord
    val bekreftPassord = viewModel.bekreftPassord
    val isLoading = viewModel.isLoading
    val errorMessage = viewModel.errorMessage
    val registrerResultat = viewModel.registrerResultat

    LaunchedEffect(registrerResultat) {
        if (registrerResultat != null) {
            navController.navigate("login") {
                popUpTo("opprett_bruker") { inclusive = true }
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                text = stringResource(R.string.create_user),
                style = MaterialTheme.typography.displaySmall
            )
        }
        item { Spacer(modifier = Modifier.height(32.dp)) }
        item {
            OutlinedTextField(
                value = brukernavn,
                onValueChange = { viewModel.brukernavnInput(it) },
                label = { Text(stringResource(R.string.username)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item {
            OutlinedTextField(
                value = epost,
                onValueChange = { viewModel.epostInput(it) },
                label = { Text(stringResource(R.string.email)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
        }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item { OutlinedTextField(
            value = passord,
            onValueChange = { viewModel.passordInput(it) },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            )
        ) }
        item { Spacer(modifier = Modifier.height(12.dp)) }
        item { OutlinedTextField(
            value = bekreftPassord,
            onValueChange = { viewModel.bekreftPassordInput(it) },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            )
        ) }
        item { Spacer(modifier = Modifier.height(62.dp)) }
        item {
            Button(
                onClick = { viewModel.opprettBruker() },
                modifier = Modifier.size(width = 190.dp, height = 50.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator( // <-- samme som LoginSkjerm (Googlet/KI)
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.confirm),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        item {
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun OpprettBrukerPreview() {
    DiskgolfTheme {
        val dummyNavController = rememberNavController()
        OpprettBrukerSkjerm(navController = dummyNavController)
    }
}