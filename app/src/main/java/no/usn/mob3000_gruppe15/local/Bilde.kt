package no.usn.mob3000_gruppe15.local

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

object Bilde {
    //OBS! STØTTER OPPLASTING AV bilder opp til  CA. 1.50MB
    fun uriToBase64(
        context: Context,
        uri: Uri,
        maxWidth: Int = 800,
        quality: Int = 80
    ): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val targetWidth = if (bitmap.width > maxWidth) maxWidth else bitmap.width
                val targetHeight = (bitmap.height * targetWidth) / bitmap.width
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
                val outputStream = ByteArrayOutputStream()
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
                val compressedBytes = outputStream.toByteArray()
                outputStream.close()
                Base64.encodeToString(compressedBytes, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            null
        }
    }
}