package no.usn.mob3000_gruppe15.ui.components

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.widget.PlaceAutocomplete
import com.google.maps.android.compose.CameraPositionState
import kotlinx.coroutines.launch

@Composable
fun placesAutoCompleteSok(
    cameraPositionState: CameraPositionState? = null,
    onPlassValgt: ((Place) -> Unit)? = null
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val placesClient = Places.createClient(context)

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { intent ->
                    val prediction = PlaceAutocomplete.getPredictionFromIntent(intent)
                    if (prediction != null) {
                        val placeId = prediction.placeId
                        val placeFields = listOf(Place.Field.LOCATION, Place.Field.DISPLAY_NAME)
                        val request = FetchPlaceRequest.newInstance(placeId, placeFields)
                        placesClient.fetchPlace(request).addOnSuccessListener { response ->
                            val place = response.place
                            onPlassValgt?.invoke(place)
                            place.location?.let { latLng ->
                                coroutineScope.launch {
                                    cameraPositionState?.animate(
                                        CameraUpdateFactory.newLatLngZoom(latLng, 13f),
                                        1000
                                    )
                                }
                            }
                        }.addOnFailureListener { exception ->
                            print(exception.message)
                        }
                    }
                }
            }
        }
    }
}