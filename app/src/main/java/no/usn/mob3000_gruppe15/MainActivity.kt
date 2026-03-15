package no.usn.mob3000_gruppe15

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.android.libraries.places.api.Places
import no.usn.mob3000_gruppe15.ui.theme.DiskgolfTheme
import no.usn.mob3000_gruppe15.ui.navigasjon.DiskgolfApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(!Places.isInitialized()) {
            Places.initialize(this, BuildConfig.PLACES_API_KEY)
        }

        enableEdgeToEdge()
        setContent {
            DiskgolfTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DiskgolfApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}