package no.usn.mob3000_gruppe15.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import no.usn.mob3000_gruppe15.R
import no.usn.mob3000_gruppe15.data.model.Bane

@Composable
fun BaneBilde(bane: Bane) {
    if (!bane.bilde.isNullOrEmpty()) {
        val bytes = Base64.decode(bane.bilde, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = stringResource(R.string.banebilde_),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(bane.imageResourceId),
            contentDescription = stringResource(R.string.banebilde_),
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentScale = ContentScale.Crop
        )
    }
}