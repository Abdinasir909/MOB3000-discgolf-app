package no.usn.mob3000_gruppe15.model

import android.graphics.Bitmap
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class IconLabel (
    val icon: ImageVector,
    val label: String
)